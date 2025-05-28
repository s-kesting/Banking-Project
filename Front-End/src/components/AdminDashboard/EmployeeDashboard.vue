<template>
  <div class="admin-dashboard">
    <h1>🌐 Admin Dashboard</h1>

    <input
      v-model="searchQuery"
      @input="fetchUsers"
      type="text"
      placeholder="🔍 Search by username..."
      class="search-bar"
    />

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
            v-for="userWithAccount in users"
            :key="userWithAccount.user.userId"
          >
            <tr>
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
              <td colspan="6"></td>
            </tr>

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

    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
//import apiClient from "axios";
import  apiClient  from "@/utils/apiClient";
import { API_ENDPOINTS } from "@/config"; 
import useAuthStore from "@/stores/authStore";

const users = ref([]);
const searchQuery = ref("");
const errorMessage = ref("");
let authStore = useAuthStore()
console.log(authStore.getUserData())
console.log(authStore.token)
// ✅ Load all users with their accounts
const fetchUsers = async () => {
    console.log("fetching users")
  try {
    errorMessage.value = "";
    const queryParam = searchQuery.value
      ? `?username=${searchQuery.value}`
      : "";
        console.log(queryParam)
    const res = await apiClient.get(`${API_ENDPOINTS.employee}/users${queryParam}`);
    users.value = res.data;
  } catch (err) {
    errorMessage.value =
      "❌ Failed to load users: " + (err.response?.data || err.message);
  }
};

// ✅ Update user verify status & account limits
const updateUserAndAccount = async (user, account) => {
  try {
    errorMessage.value = "";

    await apiClient.put(`${API_ENDPOINTS.employee}/users/${user.userId}/verify`, {
      verifyUser: user.verifyUser,
    });

    await apiClient.put(`${API_ENDPOINTS.employee}/accounts/${account.accountId}`, {
      verifyAccount: account.verifyAccount,
      dailyLimit: account.dailyLimit,
      absoluteLimit: account.absoluteLimit,
    });

    alert("✅ Update successful!");
  } catch (err) {
    errorMessage.value =
      "❌ Update failed: " + (err.response?.data || err.message);
  }
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
</style>
