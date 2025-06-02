<template>
  <div class="atm-view">
    <h1>ATM Access</h1>

    <!-- Account Selection -->
    <div v-if="!sessionStarted">
      <label for="account">Select Account:</label>
      <select v-model="selectedAccountId">
        <option disabled value="">-- select --</option>
        <option
          v-for="acc in accounts"
          :key="acc.accountId"
          :value="acc.accountId"
        >
          {{ formatAccountLabel(acc) }}
        </option>
      </select>

      <button @click="startSession" :disabled="!selectedAccountId">
        Proceed
      </button>

      <!-- Debug JSON (optional) -->
      <!-- <pre>{{ accounts }}</pre> -->
    </div>

    <!-- ATM Actions -->
    <div v-else>
      <h2>ATM Session for Account #{{ selectedAccountId }}</h2>

      <input
        v-model.number="amount"
        type="number"
        min="1"
        placeholder="Enter amount"
      />

      <div class="atm-buttons">
        <button @click="deposit">Deposit</button>
        <button @click="withdraw">Withdraw</button>
      </div>

      <p v-if="message" :class="{ error: isError, success: !isError }">
        {{ message }}
      </p>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "ATMView",
  data() {
    return {
      accounts: [],
      selectedAccountId: "",
      sessionId: null,
      sessionStarted: false,
      amount: 0,
      message: "",
      isError: false,
    };
  },
  created() {
    this.fetchAccounts();
  },
  methods: {
    async fetchAccounts() {
      try {
        const res = await axios.get("/api/atm/accounts");
        this.accounts = res.data;
        console.log("Accounts loaded:", this.accounts);
      } catch (err) {
        this.message = "Failed to load accounts.";
        this.isError = true;
      }
    },
    async startSession() {
      try {
        const res = await axios.post("/api/atm/session", {
          accountId: this.selectedAccountId,
        });
        this.sessionId = res.data.sessionId;
        this.sessionStarted = true;
        this.message = "";
      } catch (err) {
        this.message = "Could not start session.";
        this.isError = true;
      }
    },
    async deposit() {
      try {
        const res = await axios.post("/api/atm/deposit", {
          sessionId: this.sessionId,
          accountId: this.selectedAccountId,
          amount: this.amount,
        });
        this.message = `Deposited ${res.data.amount} successfully.`;
        this.isError = false;
      } catch (err) {
        this.message = err.response?.data?.error || "Deposit failed.";
        this.isError = true;
      }
    },
    async withdraw() {
      try {
        const res = await axios.post("/api/atm/withdraw", {
          sessionId: this.sessionId,
          accountId: this.selectedAccountId,
          amount: this.amount,
        });
        this.message = `Withdrew ${res.data.amount} successfully.`;
        this.isError = false;
      } catch (err) {
        this.message = err.response?.data?.error || "Withdrawal failed.";
        this.isError = true;
      }
    },
    formatAccountLabel(account) {
      const type =
        typeof account.accountType === "string"
          ? account.accountType
          : account.accountType?.name || "[No Type]";
      const balance =
        account.balance != null
          ? `${account.balance.toFixed(2)} €`
          : "[No Balance]";
      return `${type} - ${balance}`;
    },
  },
};
</script>

<style scoped>
.atm-view {
  max-width: 500px;
  margin: 2rem auto;
  padding: 1rem;
  font-family: Arial, sans-serif;
}

input,
select,
button {
  margin-top: 0.5rem;
  margin-bottom: 1rem;
  display: block;
}

.atm-buttons button {
  margin-right: 10px;
}

.success {
  color: green;
}

.error {
  color: red;
}
</style>
