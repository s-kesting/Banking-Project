<template>
  <div class="admin-dashboard">
    <h1>🌐 Admin Dashboard</h1>

    <!-- Filters -->
    <div class="filters">
      <input
        v-model="searchQuery"
        @input="fetchUsers"
        placeholder="🔍 Search username"
      />
      <select v-model="userStatusFilter">
        <option value="ALL">User Status: All</option>
        <option value="PENDING">Pending</option>
        <option value="ACTIVE">Active</option>
        <option value="REJECTED">Rejected</option>
      </select>
      <select v-model="accountStatusFilter">
        <option value="ALL">Account Status: All</option>
        <option value="PENDING">Pending</option>
        <option value="ACTIVE">Active</option>
        <option value="REJECTED">Rejected</option>
      </select>
    </div>

    <!-- User List -->
    <div
      v-for="userWithAccount in paginatedUsers"
      :key="userWithAccount.user.userId"
      class="user-block"
    >
      <div class="user-header">
        <div class="user-info">
          <strong>👤 {{ userWithAccount.user.username }}</strong>
          <span>— {{ userWithAccount.user.email }}</span>
          <div class="status-controls">
            <label>🔐 Status:</label>
            <select
              v-model="userWithAccount.user.verifyUser"
              :class="`status-tag ${userWithAccount.user.verifyUser.toLowerCase()}`"
              @change="syncAccountStatus(userWithAccount)"
            >
              <option value="PENDING">PENDING</option>
              <option value="ACTIVE">ACTIVE</option>
              <option value="REJECTED">REJECTED</option>
            </select>
            <button
              class="save-btn"
              @click="updateUserStatus(userWithAccount.user)"
            >
              💾 Save User
            </button>
          </div>
        </div>
        <div
          class="arrow-toggle"
          @click="toggleExpanded(userWithAccount.user.userId)"
        >
          <span v-if="expandedUser === userWithAccount.user.userId">🔽</span>
          <span v-else>▶</span>
        </div>
      </div>

      <!-- Accounts Table -->
      <div
        v-if="
          expandedUser === userWithAccount.user.userId &&
          userWithAccount.accounts.length
        "
        class="account-section"
      >
        <h4>🏦 Accounts</h4>
        <table>
          <thead>
            <tr>
              <th>IBAN</th>
              <th>Type</th>
              <th>Status</th>
              <th>Abs (€)</th>
              <th>Daily (€)</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="account in userWithAccount.accounts"
              :key="account.accountId"
            >
              <td>{{ account.iban }}</td>
              <td>{{ account.accountType }}</td>
              <td>
                <select
                  v-model="account.verifyAccount"
                  :class="`status-tag ${account.verifyAccount.toLowerCase()}`"
                  :disabled="userWithAccount.user.verifyUser === 'REJECTED'"
                >
                  <option value="PENDING">PENDING</option>
                  <option value="ACTIVE">ACTIVE</option>
                  <option value="REJECTED">REJECTED</option>
                </select>
              </td>
              <td>
                <input
                  type="number"
                  v-model.number="account.absoluteLimit"
                  :disabled="userWithAccount.user.verifyUser === 'REJECTED'"
                />
              </td>
              <td>
                <input
                  type="number"
                  v-model.number="account.dailyLimit"
                  :disabled="userWithAccount.user.verifyUser === 'REJECTED'"
                />
              </td>
            </tr>
          </tbody>
        </table>

        <!-- Unified Save Button for All Accounts -->
        <div class="save-accounts-wrapper">
          <button
            class="save-btn"
            @click="updateAllAccounts(userWithAccount)"
            :disabled="userWithAccount.user.verifyUser === 'REJECTED'"
          >
            💾 Save Accounts
          </button>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div class="pagination">
      <button @click="prevPage" :disabled="currentPage === 1">⬅️ Prev</button>
      <span>Page {{ currentPage }} / {{ totalPages }}</span>
      <button @click="nextPage" :disabled="currentPage === totalPages">
        Next ➡️
      </button>
    </div>

    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import apiClient from "@/utils/apiClient";
import { API_ENDPOINTS } from "@/config";

const users = ref([]);
const currentPage = ref(1);
const totalPages = ref(1);
const usersPerPage = 5;
const searchQuery = ref("");
const userStatusFilter = ref("ALL");
const accountStatusFilter = ref("ALL");
const expandedUser = ref(null);
const errorMessage = ref("");

const fetchUsers = async () => {
  try {
    const query = new URLSearchParams();
    query.append("page", currentPage.value - 1);
    query.append("size", usersPerPage);
    if (searchQuery.value.trim())
      query.append("username", searchQuery.value.trim());

    const res = await apiClient.get(
      `${API_ENDPOINTS.employee}/users/paginated?${query.toString()}`
    );
    users.value = res.data.users;
    totalPages.value = res.data.totalPages;
  } catch (err) {
    errorMessage.value =
      "❌ Failed to load users: " + (err.response?.data || err.message);
  }
};

const updateUserStatus = async (user) => {
  try {
    await apiClient.put(
      `${API_ENDPOINTS.employee}/users/${user.userId}/verify`,
      {
        verifyUser: user.verifyUser,
      }
    );
    await fetchUsers(); // Refresh if accounts are generated
  } catch (err) {
    errorMessage.value = "❌ User status update failed.";
  }
};

const updateAllAccounts = async (userWithAccount) => {
  try {
    for (const account of userWithAccount.accounts) {
      await apiClient.put(
        `${API_ENDPOINTS.employee}/accounts/${account.accountId}`,
        {
          verifyAccount: account.verifyAccount,
          absoluteLimit: account.absoluteLimit,
          dailyLimit: account.dailyLimit,
        }
      );
    }
    alert("✅ All accounts updated!");
  } catch (err) {
    errorMessage.value =
      "❌ Account update failed: " + (err.response?.data || err.message);
  }
};

const syncAccountStatus = (userWithAccount) => {
  const status = userWithAccount.user.verifyUser;
  if (status === "REJECTED" || status === "PENDING") {
    userWithAccount.accounts.forEach((acc) => (acc.verifyAccount = status));
  }
};

const toggleExpanded = (userId) => {
  expandedUser.value = expandedUser.value === userId ? null : userId;
};

const filteredUsers = computed(() => {
  return users.value.filter((entry) => {
    const userMatch =
      userStatusFilter.value === "ALL" ||
      entry.user.verifyUser === userStatusFilter.value;
    const accountMatch =
      accountStatusFilter.value === "ALL" ||
      entry.accounts.some(
        (acc) => acc.verifyAccount === accountStatusFilter.value
      );
    return userMatch && accountMatch;
  });
});

const paginatedUsers = computed(() => filteredUsers.value);

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
    fetchUsers();
  }
};
const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--;
    fetchUsers();
  }
};

onMounted(fetchUsers);
</script>

<style scoped>
.admin-dashboard {
  max-width: 1100px;
  margin: 30px auto;
  font-family: "Segoe UI", sans-serif;
}
.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-bottom: 20px;
}
.filters input,
.filters select {
  padding: 8px;
  border-radius: 6px;
  font-size: 14px;
}
.user-block {
  border: 1px solid #ddd;
  border-radius: 8px;
  margin-bottom: 20px;
}
.user-header {
  background-color: #f0f8ff;
  padding: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.user-info {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.status-controls {
  display: flex;
  align-items: center;
  gap: 10px;
}
.status-tag {
  font-weight: bold;
}
.status-tag.active {
  color: green;
}
.status-tag.pending {
  color: orange;
}
.status-tag.rejected {
  color: red;
}
.account-section {
  padding: 15px;
  background-color: #fcfcfc;
}
.account-section table {
  width: 100%;
  border-collapse: collapse;
}
.account-section th,
.account-section td {
  border: 1px solid #ccc;
  padding: 8px;
  text-align: center;
}
.arrow-toggle {
  cursor: pointer;
  font-size: 20px;
}
.save-btn {
  background-color: #3498db;
  color: white;
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  font-weight: bold;
  cursor: pointer;
}
.save-btn:hover {
  background-color: #217dbb;
}
.save-accounts-wrapper {
  margin-top: 15px;
  text-align: right;
}
.pagination {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 20px;
}
.error-message {
  color: red;
  text-align: center;
}
input:disabled,
select:disabled {
  background-color: #f5f5f5;
  color: #888;
  cursor: not-allowed;
}
</style>
