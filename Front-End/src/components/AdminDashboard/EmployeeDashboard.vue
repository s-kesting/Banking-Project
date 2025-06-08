<template>
  <div class="admin-dashboard">
    <h1>🌐 Admin Dashboard</h1>

    <!-- Toasts -->
    <transition name="fade">
      <div v-if="success" class="toast toast-success">✅ {{ success }}</div>
    </transition>
    <transition name="fade">
      <div v-if="error" class="toast toast-error">❌ {{ error }}</div>
    </transition>

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
          <strong>👤 {{ user.username }}</strong>
          <span>— {{ user.email }}</span>
          <div class="status-controls">
            <label>🔐 Status:</label>
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
              💾 Save User
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
/* Toast message fixed top center */
.toast {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 9999;
  padding: 12px 18px;
  border-radius: 8px;
  font-weight: bold;
  color: white;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
  opacity: 0.95;
}
.toast-success {
  background-color: #4caf50;
}
.toast-error {
  background-color: #f44336;
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Dashboard styling */
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

/* Default soft blue header */
.user-header {
  background-color: #f0f8ff;
  padding: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 🔶 Override header if it's a new registration */
.user-block.pending-no-accounts .user-header {
  background-color: #fff8cc; /* soft yellow */
  border-left: 6px solid #f1c40f;
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
input:disabled,
select:disabled {
  background-color: #f5f5f5;
  color: #888;
  cursor: not-allowed;
}
</style>
