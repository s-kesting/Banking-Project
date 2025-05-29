<template>
  <div class="dashboard-container">
    <h1>📊 Employee Overview</h1>

    <div class="cards-wrapper">
      <!-- 👤 User Status Cards -->
      <div
        class="card"
        v-for="(count, status) in userStats"
        :key="status"
        :class="statusColor(status)"
      >
        <h2>{{ status }}</h2>
        <p>{{ count }} Users</p>
      </div>

      <!-- 🏦 Account Status Cards -->
      <div
        class="card"
        v-for="(count, status) in accountStats"
        :key="status"
        :class="statusColor(status)"
      >
        <h2>{{ status }}</h2>
        <p>{{ count }} Accounts</p>
      </div>
    </div>

    <router-link to="/admindashboard" class="detail-btn">More Info</router-link>

    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import  apiClient  from "@/utils/apiClient";
import { useRouter } from "vue-router";
import { API_ENDPOINTS } from "@/config";

const userStats = ref({ ACTIVE: 0, PENDING: 0, REJECTED: 0 });
const accountStats = ref({ ACTIVE: 0, PENDING: 0, REJECTED: 0 });
const errorMessage = ref("");
const router = useRouter();

const fetchOverviewData = async () => {
  try {
    errorMessage.value = "";
    const res = await apiClient.get(`${API_ENDPOINTS.employee}/users`);
    const users = res.data;

    // Reset
    userStats.value = { ACTIVE: 0, PENDING: 0, REJECTED: 0 };
    accountStats.value = { ACTIVE: 0, PENDING: 0, REJECTED: 0 };

    users.forEach((userWithAccount) => {
      const userStatus = userWithAccount.user.verifyUser;
      if (userStatus in userStats.value) {
        userStats.value[userStatus]++;
      }

      userWithAccount.accounts.forEach((acc) => {
        const accStatus = acc.verifyAccount;
        if (accStatus in accountStats.value) {
          accountStats.value[accStatus]++;
        }
      });
    });
  } catch (err) {
    errorMessage.value =
      "❌ Failed to load dashboard: " + (err.response?.data || err.message);
  }
};

const statusColor = (status) => {
  switch (status) {
    case "ACTIVE":
      return "card-green";
    case "PENDING":
      return "card-yellow";
    case "REJECTED":
      return "card-red";
    default:
      return "";
  }
};

const goToDetail = () => {
  router.push("/AdminDashboard");
};

onMounted(fetchOverviewData);
</script>

<style scoped>
.dashboard-container {
  max-width: 900px;
  margin: 50px auto;
  padding: 30px;
  font-family: "Segoe UI", sans-serif;
  text-align: center;
}

.cards-wrapper {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: center;
  margin-top: 30px;
}

.card {
  flex: 1 1 200px;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  color: white;
  font-weight: bold;
  transition: transform 0.2s;
}

.card:hover {
  transform: translateY(-5px);
}

.card-green {
  background-color: #28a745;
}
.card-yellow {
  background-color: #ffc107;
  color: #000;
}
.card-red {
  background-color: #dc3545;
}

.detail-btn {
  margin-top: 40px;
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  font-size: 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}
.detail-btn:hover {
  background-color: #0056b3;
}

.error-message {
  color: red;
  margin-top: 20px;
}
</style>
