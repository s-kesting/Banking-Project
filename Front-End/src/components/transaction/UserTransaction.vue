<template>
  <div class="user-transaction">
    <h2>Transfer Money</h2>

    <!--accountSelector emits select-iban -->
    <AccountSelector @select-iban="setSenderIban" />

    <!--once a senderIban is chosen, show the transfer form: -->
    <form v-if="senderIban" @submit.prevent="onSubmit">
      <!-- receiver IBAN -->
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

      <!-- amount -->
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

      <!-- description (optional) -->
      <div>
        <label for="description">Description (optional):</label>
        <input
          id="description"
          v-model="form.description"
          type="text"
          placeholder="e.g. Rent"
        />
      </div>

      <!-- submit -->
      <button type="submit" :disabled="loading">
        {{ loading ? "Sending…" : "Send Money" }}
      </button>
    </form>

    <!-- prompt to select an account if none chosen yet -->
    <p v-else class="prompt">Please select one of your IBANs above to begin.</p>

    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
    <p v-if="successMessage" class="success">{{ successMessage }}</p>
  </div>
</template>

<script setup>
import { ref } from "vue";
import apiClient from "@/utils/apiClient";
import { API_ENDPOINTS } from "@/config";
import AccountSelector from "./AccountSelector.vue";

// State
const senderIban    = ref("");
const form          = ref({
  receiverIban: "",
  amount:       null,
  description:  ""
});
const loading       = ref(false);
const errorMessage  = ref("");
const successMessage= ref("");

// Update `senderIban` when AccountSelector emits it
function setSenderIban(iban) {
  senderIban.value = iban;
}

// Handle form submission
async function onSubmit() {
  errorMessage.value = "";
  successMessage.value = "";
  loading.value = true;

  const payload = {
    senderIban:   senderIban.value,
    receiverIban: form.value.receiverIban.trim(),
    amount:       form.value.amount,
    description:  form.value.description.trim() || null
  };

  try {
    const res = await apiClient.post(API_ENDPOINTS.usertransaction,payload);
    successMessage.value = `Transaction #${res.data.transactionId} succeeded.`;

    // reset only the receiver‐side fields (leave senderIban intact)
    form.value.receiverIban = "";
    form.value.amount       = null;
    form.value.description  = "";
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

.prompt {
  margin-top: 1rem;
  font-style: italic;
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
