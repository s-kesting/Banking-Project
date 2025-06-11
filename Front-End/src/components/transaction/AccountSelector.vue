<template>
  <div class="account-selector">
    <label for="senderIban">Your IBAN:</label>

    <div v-if="loading" class="loading">Loading your accounts…</div>

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
        {{ acc.iban }} ({{ acc.accountType }}) – €{{ acc.balance.toFixed(2) }}
      </option>
    </select>

    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
  </div>
</template>

<script setup>
import { ref } from "vue";

// declare props instead of fetching inside
const props = defineProps({
  accounts: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  errorMessage: {
    type: String,
    default: ""
  }
});

const emit = defineEmits(["select-iban"]);

const selectedIban = ref("");

// forward selection to parent
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