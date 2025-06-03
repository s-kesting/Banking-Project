<template>
  <div class="admin-dashboard">
    <h1>🌐 Admin Dashboard</h1>

    <!-- 🔍 Search and Filter Section -->
    <div class="filters">
      <input
        v-model="searchQuery"
        @input="fetchUsers"
        type="text"
        placeholder="🔍 Search by username..."
        class="search-bar"
      />

      <label>
        User Status:
        <select v-model="userStatusFilter">
          <option value="ALL">ALL</option>
          <option value="PENDING">PENDING</option>
          <option value="ACTIVE">ACTIVE</option>
          <option value="REJECTED">REJECTED</option>
        </select>
      </label>

      <label>
        Account Status:
        <select v-model="accountStatusFilter">
          <option value="ALL">ALL</option>
          <option value="PENDING">PENDING</option>
          <option value="ACTIVE">ACTIVE</option>
          <option value="REJECTED">REJECTED</option>
        </select>
      </label>
    </div>

    <!-- 📊 Users Table -->
    <div class="table-wrapper">
      <table class="styled-table">
        <thead>
          <tr>
            <th>👤 Username</th>
            <th>📧 Email</th>
            <th>🔐 User Status</th>
            <th>🏦 IBAN</th>
            <th>💳 Type</th>
            <th>🛡️ Account Status</th>
            <th>⚠️ Absolute (€)</th>
            <th>💰 Daily (€)</th>
            <th>🛠️ Actions</th>
          </tr>
        </thead>
        <tbody>
          <template
            v-for="userWithAccount in paginatedUsers"
            :key="userWithAccount.user.userId"
          >
            <!-- 👤 User Row -->
            <tr
              :class="{ 'no-accounts': userWithAccount.accounts.length === 0 }"
            >
              <td>{{ userWithAccount.user.username }}</td>
              <td>{{ userWithAccount.user.email }}</td>
              <td>
                <select
                  v-model="userWithAccount.user.verifyUser"
                  :class="
                    'status-' + userWithAccount.user.verifyUser.toLowerCase()
                  "
                >
                  <option value="PENDING">PENDING</option>
                  <option value="ACTIVE">ACTIVE</option>
                  <option value="REJECTED">REJECTED</option>
                </select>
              </td>

              <!-- 🔘 Save Button in User Row if no accounts yet -->
              <template v-if="userWithAccount.accounts.length === 0">
                <td colspan="5"></td>
                <td>
                  <button @click="updateUserStatus(userWithAccount.user)">
                    💾 Save
                  </button>
                </td>
              </template>

              <!-- ➖ Empty TDs when accounts exist -->
              <template v-else>
                <td colspan="6"></td>
              </template>
            </tr>

            <!-- 🏦 Account Rows -->
            <tr
              v-for="account in userWithAccount.accounts"
              :key="account.accountId"
              class="account-row"
            >
              <td colspan="3"></td>
              <td>{{ account.iban }}</td>
              <td>{{ account.accountType }}</td>
              <td>
                <select
                  v-model="account.verifyAccount"
                  :class="'status-' + account.verifyAccount.toLowerCase()"
                >
                  <option value="PENDING">PENDING</option>
                  <option value="ACTIVE">ACTIVE</option>
                  <option value="REJECTED">REJECTED</option>
                </select>
              </td>
              <td>
                <input type="number" v-model.number="account.absoluteLimit" />
              </td>
              <td>
                <input type="number" v-model.number="account.dailyLimit" />
              </td>
              <td>
                <button
                  @click="updateUserAndAccount(userWithAccount.user, account)"
                >
                  💾 Save
                </button>
              </td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>

    <!-- 🔁 Pagination -->
    <div class="pagination">
      <button @click="prevPage" :disabled="currentPage === 1">
        ⬅️ Previous
      </button>
      <span>Page {{ currentPage }} / {{ totalPages }}</span>
      <button @click="nextPage" :disabled="currentPage === totalPages">
        Next ➡️
      </button>
    </div>

    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from "vue";
import apiClient from "@/utils/apiClient";
import { API_ENDPOINTS } from "@/config";

const users = ref([]);
const searchQuery = ref("");
const errorMessage = ref("");

const userStatusFilter = ref("ALL");
const accountStatusFilter = ref("ALL");

const currentPage = ref(1);
const usersPerPage = 10;

// ✅ Fetch users
const fetchUsers = async () => {
  try {
    errorMessage.value = "";
    const queryParam = searchQuery.value
      ? `?username=${searchQuery.value}`
      : "";
    const res = await apiClient.get(
      `${API_ENDPOINTS.employee}/users${queryParam}`
    );
    users.value = res.data;
    currentPage.value = 1;
    setupUserStatusWatchers();
  } catch (err) {
    errorMessage.value =
      "❌ Failed to load users: " + (err.response?.data || err.message);
  }
};

// ✅ Update both user and one account
const updateUserAndAccount = async (user, account) => {
  try {
    errorMessage.value = "";

    await apiClient.put(
      `${API_ENDPOINTS.employee}/users/${user.userId}/verify`,
      {
        verifyUser: user.verifyUser,
      }
    );

    await apiClient.put(
      `${API_ENDPOINTS.employee}/accounts/${account.accountId}`,
      {
        verifyAccount: account.verifyAccount,
        dailyLimit: account.dailyLimit,
        absoluteLimit: account.absoluteLimit,
      }
    );

    alert("✅ Update successful!");
  } catch (err) {
    errorMessage.value =
      "❌ Update failed: " + (err.response?.data || err.message);
  }
};

// ✅ Only update user when accounts don't exist yet
const updateUserStatus = async (user) => {
  try {
    errorMessage.value = "";

    await apiClient.put(
      `${API_ENDPOINTS.employee}/users/${user.userId}/verify`,
      {
        verifyUser: user.verifyUser,
      }
    );

    await fetchUsers(); // refresh list to get generated accounts
    alert("✅ User status updated and accounts created!");
  } catch (err) {
    errorMessage.value =
      "❌ Update failed: " + (err.response?.data || err.message);
  }
};

// ✅ Filtering logic
const filteredUsers = computed(() => {
  return users.value.filter((userWithAccount) => {
    const userStatusMatches =
      userStatusFilter.value === "ALL" ||
      userWithAccount.user.verifyUser === userStatusFilter.value;

    const accountStatusMatches =
      accountStatusFilter.value === "ALL" ||
      userWithAccount.accounts.some(
        (acc) => acc.verifyAccount === accountStatusFilter.value
      );

    return userStatusMatches && accountStatusMatches;
  });
});

const paginatedUsers = computed(() => {
  const start = (currentPage.value - 1) * usersPerPage;
  return filteredUsers.value.slice(start, start + usersPerPage);
});

const totalPages = computed(() =>
  Math.ceil(filteredUsers.value.length / usersPerPage)
);

const nextPage = () => {
  if (currentPage.value < totalPages.value) currentPage.value++;
};

const prevPage = () => {
  if (currentPage.value > 1) currentPage.value--;
};

const setupUserStatusWatchers = () => {
  users.value.forEach((userWithAccount) => {
    watch(
      () => userWithAccount.user.verifyUser,
      (newStatus) => {
        userWithAccount.accounts.forEach((acc) => {
          acc.verifyAccount = newStatus;
        });
      }
    );
  });
};

onMounted(fetchUsers);
</script>

<style scoped>
.admin-dashboard {
  max-width: 1200px;
  margin: 40px auto;
  padding: 20px;
  font-family: "Segoe UI", sans-serif;
  color: #333;
}

h1 {
  text-align: center;
  margin-bottom: 20px;
}

.search-bar {
  display: block;
  margin: 0 auto 20px;
  padding: 10px 14px;
  width: 350px;
  font-size: 16px;
  border-radius: 6px;
  border: 1px solid #ccc;
}

.table-wrapper {
  overflow-x: auto;
}

.styled-table {
  width: 100%;
  border-collapse: collapse;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.05);
}

th,
td {
  border: 1px solid #ddd;
  padding: 10px;
  text-align: center;
}

th {
  background-color: #f4f7fa;
  font-weight: bold;
}

.account-row {
  background-color: #fcfcfc;
}

input[type="number"] {
  width: 90px;
  padding: 4px;
  font-size: 14px;
}

select {
  padding: 4px;
  font-size: 14px;
}

.status-active {
  color: green;
  font-weight: bold;
}

.status-pending {
  color: orange;
  font-weight: bold;
}

.status-rejected {
  color: red;
  font-weight: bold;
}

button {
  padding: 6px 12px;
  background-color: #007bff;
  color: white;
  border: none;
  font-weight: bold;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}

.error-message {
  color: red;
  font-weight: bold;
  margin-top: 20px;
  text-align: center;
}

.filters {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  gap: 1rem;
  flex-wrap: wrap;
}

.filters label {
  font-weight: bold;
  font-size: 14px;
}

.filters select {
  margin-left: 8px;
  padding: 6px;
  border-radius: 4px;
  font-size: 14px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
  gap: 1rem;
}

.pagination button {
  padding: 6px 12px;
}

.no-accounts {
  background-color: #fff8dc;
}
</style>
