<template>
  <div class="account-selector">
    <label for="senderIban">Your IBAN:</label>

    <!-- If still loading, show a simple loading message -->
    <div v-if="loading" class="loading">Loading your accounts…</div>

    <!-- Once loaded, render the <select> -->
    <select
      v-else
      id="senderIban"
      v-model="selectedIban"
      @change="onChange"
      required
    >
      <option value="" disabled>Select an account</option>
      <option
        v-for="acc in accounts"
        :key="acc.iban"
        :value="acc.iban"
      >
        <!-- Show IBAN, account type, and formatted balance -->
        {{ acc.iban }} ({{ acc.accountType }}) – €{{ acc.balance.toFixed(2) }}
      </option>
    </select>

    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import apiClient from "@/utils/apiClient";
import { API_ENDPOINTS } from "@/config";

// Emit the chosen IBAN up to the parent
const emit = defineEmits(["select-iban"]);

// State
const accounts      = ref([]);
const loading       = ref(true);
const errorMessage  = ref("");
const selectedIban  = ref("");

// Fetch the user’s accounts on mount
onMounted(async () => {
  try {
    const res = await apiClient.get(API_ENDPOINTS.userAccounts);
    accounts.value = res.data;
  } catch (err) {
    console.error("Failed to load accounts:", err);
    errorMessage.value = "Could not load accounts. Please refresh.";
  } finally {
    loading.value = false;
  }
});

// Emit whenever the dropdown value changes
function onChange() {
  emit("select-iban", selectedIban.value);
}
</script>

<style scoped>
.account-selector {
  margin-bottom: 1.5rem;
}

.loading {
  font-style: italic;
  margin-top: 0.5rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
}

select {
  width: 100%;
  padding: 0.5rem;
  box-sizing: border-box;
}

.error {
  margin-top: 0.5rem;
  color: red;
}
</style>