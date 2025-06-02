<template>
  <div class="user-transaction">
    <h2>Transfer Money</h2>
    <form @submit.prevent="onSubmit">
      <div>
        <label for="senderIban">Your IBAN:</label>
        <input
          id="senderIban"
          v-model="form.senderIban"
          type="text"
          placeholder="NL91ABNA0000001234"
          required
        />
      </div>

      <div>
        <label for="receiverIban">Recipient’s IBAN:</label>
        <input
          id="receiverIban"
          v-model="form.receiverIban"
          type="text"
          placeholder="NL91ABNA0000005678"
          required
        />
      </div>

      <div>
        <label for="amount">Amount (€):</label>
        <input
          id="amount"
          v-model.number="form.amount"
          type="number"
          step="0.01"
          placeholder="100.00"
          required
        />
      </div>

      <div>
        <label for="description">Description (optional):</label>
        <input
          id="description"
          v-model="form.description"
          type="text"
          placeholder="e.g. Rent"
        />
      </div>

      <button type="submit" :disabled="loading">
        {{ loading ? "Sending…" : "Send Money" }}
      </button>
    </form>

    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
    <p v-if="successMessage" class="success">{{ successMessage }}</p>
  </div>
</template>

<script setup>
import { ref } from "vue";
import apiClient from "@/utils/apiClient";
import { API_BASE_URL } from "@/config";
import { useAuthStore } from "@/stores/authStore.js";

const form = ref({
  senderIban: "",
  receiverIban: "",
  amount: null,
  description: "",
});
const loading = ref(false);
const errorMessage = ref("");
const successMessage = ref("");

async function onSubmit() {
  errorMessage.value = "";
  successMessage.value = "";
  loading.value = true;

  const payload = {
    senderIban: form.value.senderIban.trim(),
    receiverIban: form.value.receiverIban.trim(),
    amount: form.value.amount,
    description: form.value.description.trim() || null,
  };

  try {
    const authStore = useAuthStore();
    const res = await apiClient.post(`${API_BASE_URL}transactions/user`, payload);
    successMessage.value = `Transaction #${res.data.transactionId} succeeded.`;
    form.value.senderIban = "";
    form.value.receiverIban = "";
    form.value.amount = null;
    form.value.description = "";
  } catch (err) {
    if (err.response) {
      errorMessage.value = err.response.data || "Failed to create transaction.";
    } else {
      errorMessage.value = "Network error; please try again.";
    }
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.user-transaction {
  max-width: 400px;
  margin: 2rem auto;
  font-family: Arial, sans-serif;
}
label {
  display: block;
  margin-top: 1rem;
}
input {
  width: 100%;
  padding: 0.5rem;
  box-sizing: border-box;
}
button {
  margin-top: 1.5rem;
  padding: 0.5rem 1rem;
}
.error {
  margin-top: 1rem;
  color: red;
}
.success {
  margin-top: 1rem;
  color: green;
}
</style>
