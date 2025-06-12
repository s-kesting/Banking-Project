<template>
  <div class="admin-dashboard">
    <h1>Admin Dashboard</h1>

    <!-- Inline Notification -->
    <div v-if="success || error" class="notification-wrapper">
      <p v-if="success" class="notification success">{{ success }}</p>
      <p v-if="error" class="notification error">{{ error }}</p>
    </div>

    <!-- Filters -->
    <div class="filters">
      <input
        v-model="searchQuery"
        @input="fetchUsers"
        placeholder="Search username"
      />
      <select v-model="userStatusFilter">
        <option value="ALL">User Status: All</option>
        <option value="PENDING">Pending</option>
        <option value="ACTIVE">Active</option>
        <option value="REJECTED">Rejected</option>
      </select>
    </div>

    <!-- User Cards -->
    <div
      v-for="user in paginatedUsers"
      :key="user.userId"
      class="user-block"
      :class="{
        'pending-no-accounts':
          user.verifyUser === 'PENDING' &&
          (!user.accounts || user.accounts.length === 0),
      }"
    >
      <div class="user-header">
        <div class="user-info">
          <strong>{{ user.username }}</strong>
          <span>Email: {{ user.email }}</span>
          <div class="status-controls">
            <label>Status:</label>
            <select
              v-model="user.verifyUser"
              :class="`status-tag ${user.verifyUser.toLowerCase()}`"
              @change="() => syncAccountStatus(user)"
            >
              <option value="PENDING">PENDING</option>
              <option value="ACTIVE">ACTIVE</option>
              <option value="REJECTED">REJECTED</option>
            </select>
            <button class="save-btn" @click="updateUserStatus(user)">
              Save User
            </button>
          </div>
        </div>
        <div class="arrow-toggle" @click="toggleExpanded(user.userId)">
          <span v-if="expandedUser === user.userId">🔽</span>
          <span v-else>▶</span>
        </div>
      </div>

      <div
        v-if="expandedUser === user.userId && user.accounts.length"
        class="account-section"
      >
        <h4>Accounts</h4>
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
            <tr v-for="account in user.accounts" :key="account.accountId">
              <td>{{ account.IBAN }}</td>
              <td>{{ account.accountType }}</td>
              <td>
                <select
                  v-model="account.verifyAccount"
                  :class="`status-tag ${account.verifyAccount.toLowerCase()}`"
                  :disabled="user.verifyUser === 'REJECTED'"
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
                  :disabled="user.verifyUser === 'REJECTED'"
                />
              </td>
              <td>
                <input
                  type="number"
                  v-model.number="account.dailyLimit"
                  :disabled="user.verifyUser === 'REJECTED'"
                />
              </td>
            </tr>
          </tbody>
        </table>

        <div class="save-accounts-wrapper">
          <button
            class="save-btn"
            @click="updateAllAccounts(user)"
            :disabled="user.verifyUser === 'REJECTED'"
          >
            Save Accounts
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
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import apiClient from "@/utils/apiClient";
import { API_ENDPOINTS } from "@/config";

const rawRows = ref([]);
const groupedUsers = ref([]);
const currentPage = ref(1);
const totalPages = ref(1);
const usersPerPage = 5;
const searchQuery = ref("");
const userStatusFilter = ref("ALL");
const accountStatusFilter = ref("ALL");
const expandedUser = ref(null);
const success = ref("");
const error = ref("");

const showSuccess = (msg) => {
  success.value = msg;
  error.value = "";
  setTimeout(() => (success.value = ""), 4000);
};

const showError = (msg) => {
  error.value = msg;
  success.value = "";
  setTimeout(() => (error.value = ""), 4000);
};

const fetchUsers = async () => {
  try {
    const query = new URLSearchParams();
    query.append("page", currentPage.value - 1);
    query.append("size", usersPerPage);
    if (searchQuery.value.trim())
      query.append("username", searchQuery.value.trim());

    const res = await apiClient.get(
      `${API_ENDPOINTS.employee}/users/paginated?${query}`
    );
    rawRows.value = res.data.users;
    totalPages.value = res.data.totalPages;

    // Group by userId
    const grouped = {};
    for (const row of rawRows.value) {
      if (!grouped[row.userId]) {
        grouped[row.userId] = {
          userId: row.userId,
          username: row.username,
          email: row.email,
          verifyUser: row.verifyUser,
          accounts: [],
        };
      }
      grouped[row.userId].accounts.push({
        accountId: row.accountId,
        IBAN: row.iban,
        accountType: row.accountType,
        verifyAccount: row.verifyAccount,
        absoluteLimit: row.absoluteLimit,
        dailyLimit: row.dailyLimit,
      });
    }
    groupedUsers.value = Object.values(grouped);
  } catch (err) {
    showError("Failed to load users: " + (err.response?.data || err.message));
  }
};

const updateUserStatus = async (user) => {
  try {
    await apiClient.put(
      `${API_ENDPOINTS.employee}/users/${user.userId}/verify`,
      { verifyUser: user.verifyUser }
    );
    showSuccess("User status updated!");
    await fetchUsers();
  } catch (err) {
    showError("User status update failed.");
  }
};

const updateAllAccounts = async (user) => {
  try {
    for (const account of user.accounts) {
      await apiClient.put(
        `${API_ENDPOINTS.employee}/accounts/${account.accountId}`,
        {
          verifyAccount: account.verifyAccount,
          absoluteLimit: account.absoluteLimit,
          dailyLimit: account.dailyLimit,
        }
      );
    }
    showSuccess("All accounts updated!");
    await fetchUsers();
  } catch (err) {
    showError("Account update failed: " + (err.response?.data || err.message));
  }
};

const toggleExpanded = (userId) => {
  expandedUser.value = expandedUser.value === userId ? null : userId;
};

const filteredUsers = computed(() => {
  return groupedUsers.value.filter((user) => {
    const userMatch =
      userStatusFilter.value === "ALL" ||
      user.verifyUser === userStatusFilter.value;
    const accountMatch =
      accountStatusFilter.value === "ALL" ||
      user.accounts.some(
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
  max-width: 1000px;
  margin: 40px auto;
  font-family: "Segoe UI", sans-serif;
  padding: 0 20px;
  color: #333;
}

h1 {
  font-size: 24px;
  text-align: center;
  margin-bottom: 30px;
  color: #2c3e50;
}

.filters {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  justify-content: center;
}
.filters input,
.filters select {
  padding: 8px 12px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 6px;
}

.user-block {
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.user-header {
  display: flex;
  justify-content: space-between;
  padding: 16px;
  background-color: #f9f9f9;
  border-bottom: 1px solid #eee;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.user-info h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}
.user-info p {
  margin: 0;
  font-size: 14px;
  color: #555;
}

.status-controls {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}
.status-controls label {
  font-size: 14px;
}
.status-controls select {
  padding: 6px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 4px;
}
.status-tag.pending {
  color: #e67e22;
}
.status-tag.active {
  color: #27ae60;
}
.status-tag.rejected {
  color: #c0392b;
}

.arrow-toggle {
  cursor: pointer;
  font-size: 18px;
  user-select: none;
}

.account-section {
  padding: 16px;
}
.account-section h4 {
  margin-bottom: 12px;
  font-size: 16px;
}

.account-section table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}
.account-section th,
.account-section td {
  text-align: left;
  padding: 10px;
  border-bottom: 1px solid #eee;
}
.account-section th {
  background-color: #f1f1f1;
}

input[type="number"],
.account-section select {
  width: 100%;
  padding: 6px;
  font-size: 13px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.save-btn {
  padding: 8px 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
}
.save-btn:hover {
  background-color: #0069d9;
}

.save-accounts-wrapper {
  margin-top: 16px;
  text-align: right;
}

.pagination {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 30px;
}
.pagination button {
  padding: 6px 12px;
  font-size: 14px;
  border: 1px solid #ccc;
  background-color: white;
  border-radius: 4px;
  cursor: pointer;
}
.pagination button:disabled {
  color: #999;
  cursor: not-allowed;
  background-color: #f5f5f5;
}

.notification-wrapper {
  text-align: center;
  margin-bottom: 20px;
}

.notification {
  display: inline-block;
  padding: 10px 16px;
  border-radius: 6px;
  font-weight: 500;
  font-size: 14px;
  max-width: 90%;
  margin: 0 auto;
}

.notification.success {
  background-color: #e6f4ea;
  color: #2e7d32;
  border: 1px solid #c8e6c9;
}

.notification.error {
  background-color: #fdecea;
  color: #c62828;
  border: 1px solid #f5c6cb;
}
</style>
