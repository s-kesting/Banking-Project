<template>
  <div class="transaction-dashboard">
    <h1>💼 All Transactions</h1>
    <table class="styled-table">
      <thead>
        <tr>
          <th>🔁 Transaction ID</th>
          <th>📤 From Account</th>
          <th>📥 To Account</th>
          <th>💸 Amount (€)</th>
          <th>📝 Description</th>
          <th>⏰ Timestamp</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="tx in transactions" :key="tx.transactionId">
          <td>{{ tx.transactionId }}</td>
          <td>{{ tx.senderAccount }}</td>
          <td>{{ tx.receiverAccount }}</td>
          <td>€{{ tx.amount.toFixed(2) }}</td>
          <td>{{ tx.description || "N/A" }}</td>
          <td>{{ formatTimestamp(tx.createdAt) }}</td>
        </tr>
      </tbody>
    </table>

    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import { API_ENDPOINTS } from "@/config";
import apiClient from "@/utils/apiClient";

const transactions = ref([]);
const errorMessage = ref("");

const fetchTransactions = async () => {
  try {
    const res = await apiClient.get(
      `${API_ENDPOINTS.transactions}/allTransactions`
    );
    transactions.value = res.data;
  } catch (err) {
    errorMessage.value =
      "❌ Failed to load transactions: " + (err.response?.data || err.message);
  }
};

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
</style>
