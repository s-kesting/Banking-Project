<template>
  <div class="user-transaction">
    <h2>Transfer Money</h2>

    <!-- pass down accounts, loading and errorMessage -->
    <AccountSelector
      :accounts="accounts"
      :loading="loadingAccounts"
      :error-message="accountsError"
      @select-iban="setSenderIban"
    />

    <form v-if="senderIban" @submit.prevent="onSubmit">
      <IbanSearch @select="setReceiverIban" />

      <div>
        <label for="receiverIban">Recipient’s IBAN:</label>
        <input
          id="receiverIban"
          v-model="form.receiverIban"
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
          required
        />
      </div>

      <div>
        <label for="description">Description (optional):</label>
        <input
          id="description"
          v-model="form.description"
        />
      </div>

      <button type="submit" :disabled="loading">
        {{ loading ? "Sending…" : "Send Money" }}
      </button>
    </form>

    <p v-else class="prompt">
      Please select one of your IBANs above to begin.
    </p>

    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
    <p v-if="successMessage" class="success">{{ successMessage }}</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import apiClient from "@/utils/apiClient";
import { API_ENDPOINTS } from "@/config";

import AccountSelector from "./AccountSelector.vue";
import IbanSearch       from "./IbanSearch.vue";

// load accounts here 
const accounts         = ref([]);
const loadingAccounts = ref(false);
const accountsError   = ref("");

async function fetchAccounts() {
  loadingAccounts.value = true;
  accountsError.value   = "";
  try {
    const { data } = await apiClient.get(`${API_ENDPOINTS.account}/user`);
    accounts.value = data;
  } catch (e) {
    console.error(e);
    accountsError.value = "Could not load accounts.";
  } finally {
    loadingAccounts.value = false;
  }
}
onMounted(fetchAccounts);

// transaction form state 
const senderIban     = ref("");
const form           = ref({ receiverIban: "", amount: null, description: "" });
const loading        = ref(false);
const errorMessage   = ref("");
const successMessage = ref("");

function setSenderIban(iban) {
  senderIban.value = iban;
}

function setReceiverIban(iban) {
  form.value.receiverIban = iban;
}

async function onSubmit() {
  errorMessage.value   = "";
  successMessage.value = "";
  loading.value        = true;

  try {
    const res = await apiClient.post(API_ENDPOINTS.usertransaction, {
      senderIban:   senderIban.value,
      receiverIban: form.value.receiverIban.trim(),
      amount:       form.value.amount,
      description:  form.value.description.trim() || null
    });
    successMessage.value = `Transaction #${res.data.transactionId} succeeded.`;

    // clear only the receiver side
    form.value.receiverIban = "";
    form.value.amount       = null;
    form.value.description  = "";

    // re-fetch accounts so balance updates
    await fetchAccounts();
  } catch (err) {
     // prefer a structured 
    errorMessage.value =
      err.response?.data?.error ||
      err.response?.data || "Failed to create transaction."
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