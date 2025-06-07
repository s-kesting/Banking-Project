<template>
  <div class="atm-view">
    <h1>ATM Access</h1>

    <!-- 1) ACCOUNT SELECTION SCREEN -->
    <div v-if="!sessionStarted && !transactionDone">
      <label for="account">Select Account:</label>
      <select v-model="selectedAccountId" id="account">
        <option disabled value="">-- select an account --</option>
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

      <p v-if="message" class="error">{{ message }}</p>
    </div>

    <!-- 2) DEPOSIT / WITHDRAW SCREEN -->
    <div v-else-if="sessionStarted && !transactionDone">
      <h2>ATM Session for Account #{{ selectedAccountId }}</h2>

      <label for="amount">Amount (€):</label>
      <input
        id="amount"
        v-model.number="amount"
        type="number"
        step="0.01"
        min="0.01"
        placeholder="Enter amount"
      />

      <div class="atm-buttons">
        <button class="deposit-btn" @click="handleDeposit">Deposit</button>
        <button class="withdraw-btn" @click="handleWithdraw">Withdraw</button>
      </div>

      <p v-if="message" :class="{ error: isError, success: !isError }">
        {{ message }}
      </p>
    </div>

    <!-- 3) SUCCESS SCREEN -->
    <div v-else-if="transactionDone">
      <h2 class="success-header">Transaction Complete</h2>
      <p class="success-text">{{ successText }}</p>
      <button class="ok-btn" @click="goHome">OK</button>
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
      sessionStarted: false,
      amount: 0,
      message: "",
      isError: false,
      transactionDone: false,
      successText: "",
    };
  },
  created() {
    this.fetchAccounts();
  },
  computed: {
    currentAccount() {
      return (
        this.accounts.find((a) => a.accountId === this.selectedAccountId) ||
        null
      );
    },
  },
  methods: {
    async fetchAccounts() {
      try {
        const res = await axios.get("/api/atm/accounts");
        this.accounts = res.data;
      } catch {
        this.message = "Failed to load accounts.";
        this.isError = true;
      }
    },

    // No more server-side session; just flip flag
    startSession() {
      this.clearMessages();
      if (!this.selectedAccountId) {
        this.message = "Please choose an account.";
        this.isError = true;
        return;
      }
      this.sessionStarted = true;
    },

    async handleDeposit() {
      this.clearMessages();

      if (this.amount <= 0) {
        this.message = "Amount must be greater than zero.";
        this.isError = true;
        return;
      }

      try {
        await axios.post("/api/atm/deposit", {
          accountId: this.selectedAccountId,
          amount: this.amount,
        });
        this.successText = `Deposited ${this.amount} € successfully.`;
        this.transactionDone = true;
      } catch (e) {
        this.message = e.response?.data?.message || "Deposit failed.";
        this.isError = true;
      }
    },

    async handleWithdraw() {
      this.clearMessages();

      if (this.amount <= 0) {
        this.message = "Amount must be greater than zero.";
        this.isError = true;
        return;
      }

      if (this.currentAccount && this.amount > this.currentAccount.balance) {
        this.message = "Cannot withdraw more than current balance.";
        this.isError = true;
        return;
      }

      try {
        await axios.post("/api/atm/withdraw", {
          accountId: this.selectedAccountId,
          amount: this.amount,
        });
        this.successText = `Withdrew ${this.amount} € successfully.`;
        this.transactionDone = true;
      } catch (e) {
        this.message = e.response?.data?.message || "Withdrawal failed.";
        this.isError = true;
      }
    },

    goHome() {
      this.$router.push({ path: "/dashboard" });
    },

    clearMessages() {
      this.message = "";
      this.isError = false;
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
/* Container */
.atm-view {
  max-width: 500px;
  margin: 2rem auto;
  padding: 1rem;
}

/* Labels */
.atm-view label {
  display: block;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: #2d3748; /* dark gray text */
}

/* Select & Number input */
.atm-view select,
.atm-view input[type="number"] {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 1px solid #e2e8f0; /* light gray border */
  border-radius: 0.375rem; /* 6px rounded */
  font-size: 1rem;
  color: #2d3748;
  background-color: #fff;
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.atm-view select:focus,
.atm-view input[type="number"]:focus {
  border-color: #38a169; /* green */
  box-shadow: 0 0 0 1px #38a169; /* green outline */
}

.atm-view button {
  margin-top: 1rem;
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
  font-weight: 500;
  border: none;
  border-radius: 0.375rem;
  cursor: pointer;
  transition: background-color 0.2s, opacity 0.2s;
}

/* Enabled “Proceed” */
.atm-view button:not(:disabled) {
  background-color: #38a169; /* green */
  color: #fff;
}
.atm-view button:not(:disabled):hover {
  background-color: #2f855a; /* darker green on hover */
}

/* Disabled “Proceed” */
.atm-view button:disabled {
  background-color: #cbd5e0; /* gray */
  color: #a0aec0; /* lighter gray text */
  cursor: not-allowed;
  opacity: 0.75;
}

/* Container for Deposit/Withdraw buttons */
.atm-buttons {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

/* Base styling for both buttons */
.atm-buttons button {
  flex: 1;
  padding: 0.75rem 1rem;
  font-size: 1rem;
  font-weight: 500;
  border: none;
  border-radius: 0.375rem;
  cursor: pointer;
  transition: background-color 0.2s, opacity 0.2s;
}

/* Deposit button */
.atm-buttons button:nth-child(1) {
  background-color: #38a169; /* green */
  color: #fff;
}
.atm-buttons button:nth-child(1):hover {
  background-color: #2f855a; /* darker green */
}

/* Withdraw button */
.atm-buttons button:nth-child(2) {
  background-color: #e53e3e; /* red */
  color: #fff;
}
.atm-buttons button:nth-child(2):hover {
  background-color: #c53030; /* darker red */
}

.atm-buttons button:disabled {
  background-color: #cbd5e0;
  color: #a0aec0;
  cursor: not-allowed;
  opacity: 0.75;
}

/* Message styling */
.atm-view p {
  margin-top: 1rem;
  font-size: 0.875rem;
}
.atm-view p.error {
  color: #e53e3e; /* red */
}
.atm-view p.success {
  color: #38a169; /* green */
}
</style>
