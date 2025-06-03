<template>
  <div class="transaction-dashboard">
    <h1>💼 All Transactions</h1>

    <!-- Search Filter -->
    <div class="search-bar">
      <input v-model="searchQuery" placeholder="🔍 Search by username..." />
    </div>

    <!-- Transaction Table -->
    <table class="styled-table">
      <thead>
        <tr>
          <th>🔁 Transaction ID</th>
          <th>📤 From (Sender)</th>
          <th>📥 To (Receiver)</th>
          <th>💸 Amount (€)</th>
          <th>📝 Description</th>
          <th>⏰ Timestamp</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="tx in transactions" :key="tx.transactionId">
          <td>{{ tx.transactionId }}</td>
          <td>{{ tx.senderUsername }}</td>
          <td>{{ tx.receiverUsername }}</td>
          <td>€{{ tx.amount.toFixed(2) }}</td>
          <td>{{ tx.description || "N/A" }}</td>
          <td>{{ formatTimestamp(tx.createdAt) }}</td>
        </tr>
      </tbody>
    </table>

    <!-- Pagination Controls -->
    <div class="pagination">
      <button @click="prevPage" :disabled="currentPage === 1">◀ Prev</button>
      <span>Page {{ currentPage }} of {{ totalPages }}</span>
      <button @click="nextPage" :disabled="currentPage === totalPages">
        Next ▶
      </button>
    </div>

    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import apiClient from "@/utils/apiClient";
import { API_ENDPOINTS } from "@/config";
import { watch } from "vue";

// State
const transactions = ref([]);
const searchQuery = ref("");
const errorMessage = ref("");

const currentPage = ref(1);
const itemsPerPage = 15;
const totalPages = ref(1);

// Fetch paginated transactions from backend
const fetchTransactions = async () => {
  try {
    const res = await apiClient.get(`${API_ENDPOINTS.transactions}/paginated`, {
      params: {
        page: currentPage.value - 1,
        size: itemsPerPage,
        query: searchQuery.value || undefined,
      },
    });
    transactions.value = res.data.transactions;
    totalPages.value = res.data.totalPages;
  } catch (err) {
    errorMessage.value =
      "❌ Failed to load transactions: " + (err.response?.data || err.message);
  }
};

watch(searchQuery, () => {
  currentPage.value = 1;
  fetchTransactions();
});

// Pagination handlers
const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
    fetchTransactions();
  }
};

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--;
    fetchTransactions();
  }
};

// Format date
const formatTimestamp = (timestamp) => {
  return new Date(timestamp).toLocaleString();
};

onMounted(fetchTransactions);
</script>

<style scoped>
.transaction-dashboard {
  max-width: 1100px;
  margin: 40px auto;
  padding: 20px;
  font-family: "Segoe UI", sans-serif;
  color: #333;
}
h1 {
  text-align: center;
  margin-bottom: 20px;
  color: #2c3e50;
}
.search-bar {
  text-align: center;
  margin-bottom: 20px;
}
.search-bar input {
  padding: 10px;
  width: 300px;
  font-size: 16px;
}
.styled-table {
  width: 100%;
  border-collapse: collapse;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.05);
}
th,
td {
  padding: 10px;
  text-align: center;
  border: 1px solid #ddd;
}
th {
  background-color: #f4f7fa;
  font-weight: bold;
}
tr:nth-child(even) {
  background-color: #f9f9f9;
}
.error {
  color: red;
  text-align: center;
  font-weight: bold;
  margin-top: 20px;
}
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 15px;
  gap: 10px;
}
.pagination button {
  padding: 8px 16px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
.pagination button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>
