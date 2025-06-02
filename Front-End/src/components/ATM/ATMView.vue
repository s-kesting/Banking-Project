<template>
  <div class="atm-container">
    <h2>ATM</h2>

    <!-- Account selection -->
    <label for="account">Select Account:</label>
    <select v-model="selectedAccountId">
      <option
        v-for="account in accounts"
        :key="account.accountId"
        :value="account.accountId"
      >
        {{ account.name }} (Balance: {{ account.balance }})
      </option>
    </select>

    <p v-if="sessionId">ATM session started (ID: {{ sessionId }})</p>

    <!-- Transaction input -->
    <div v-if="sessionId" class="transaction-box">
      <label for="amount">Amount:</label>
      <input type="number" v-model.number="amount" min="1" />

      <button @click="submitTransaction('deposit')">Deposit</button>
      <button @click="submitTransaction('withdraw')">Withdraw</button>
    </div>

    <!-- Feedback -->
    <div v-if="responseMessage" class="feedback">
      <strong>{{ responseStatus }}</strong
      >: {{ responseMessage }}
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
      selectedAccountId: null,
      sessionId: null,
      amount: null,
      responseStatus: "",
      responseMessage: "",
    };
  },
  mounted() {
    this.fetchAccounts();
  },
  watch: {
    async selectedAccountId(newVal) {
      if (newVal && !this.sessionId) {
        await this.startSession();
      }
    },
  },
  methods: {
    getAuthHeader() {
      const token = localStorage.getItem("jwt");
      return { Authorization: `Bearer ${token}` };
    },
    async fetchAccounts() {
      try {
        const res = await axios.get("/api/account/user", {
          headers: this.getAuthHeader(),
        });

        this.accounts = res.data;
      } catch (err) {
        console.error("Failed to load accounts:", err);
      }
    },
    async startSession() {
      try {
        const res = await axios.post(
          "/atm/session",
          { accountId: this.selectedAccountId },
          { headers: this.getAuthHeader() }
        );
        this.sessionId = res.data.sessionId;
      } catch (err) {
        console.error("Failed to start session:", err);
      }
    },
    async submitTransaction(type) {
      try {
        const res = await axios.post(
          `/atm/${type}`,
          {
            sessionId: this.sessionId,
            accountId: this.selectedAccountId,
            amount: this.amount,
          },
          {
            headers: this.getAuthHeader(),
          }
        );

        this.responseStatus = res.data.status;
        this.responseMessage = res.data.message;
        this.sessionId = null; // ends session after transaction
      } catch (err) {
        this.responseStatus = "Error";
        this.responseMessage =
          err.response?.data?.message || "Transaction failed";
      }
    },
  },
};
</script>

<style scoped>
.atm-container {
  max-width: 500px;
  margin: 0 auto;
}
.transaction-box {
  margin-top: 1rem;
}
.feedback {
  margin-top: 1rem;
  font-weight: bold;
}
</style>
