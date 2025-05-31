<template>
  <div class="atm-transfer">
    <h2>ATM Transfer</h2>

    <form @submit.prevent="handleTransfer">
      <div>
        <label for="sessionId">Session ID:</label>
        <input type="number" v-model="form.sessionId" required />
      </div>

      <div>
        <label for="targetAccountId">Target Account ID:</label>
        <input type="number" v-model="form.targetAccountId" required />
      </div>

      <div>
        <label for="amount">Amount:</label>
        <input type="number" v-model="form.amount" step="0.01" required />
      </div>

      <div>
        <label for="description">Description:</label>
        <input type="text" v-model="form.description" />
      </div>

      <button type="submit">Transfer</button>
    </form>

    <div v-if="message" class="message">{{ message }}</div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "ATMTransfer",
  data() {
    return {
      form: {
        sessionId: "",
        targetAccountId: "",
        amount: "",
        description: "",
      },
      message: "",
    };
  },
  methods: {
    handleTransfer() {
      this.message = "";

      axios
        .post("/api/ATM/transfer", {
          sessionId: Number(this.form.sessionId),
          targetAccountId: Number(this.form.targetAccountId),
          amount: Number(this.form.amount),
          description: this.form.description,
        })
        .then((response) => {
          this.message = `Transfer successful. Transaction ID: ${response.data.transactionId}`;
        })
        .catch((error) => {
          this.message = `${error.response?.data || "Transfer failed."}`;
        });
    },
  },
};
</script>

<style scoped>
.atm-transfer {
  max-width: 400px;
  margin: 2rem auto;
  padding: 1.5rem;
  border: 1px solid #ccc;
  border-radius: 8px;
}

.atm-transfer form div {
  margin-bottom: 1rem;
}

.atm-transfer label {
  display: block;
  font-weight: bold;
  margin-bottom: 0.25rem;
}

.atm-transfer input {
  width: 100%;
  padding: 0.5rem;
  box-sizing: border-box;
}

button {
  padding: 0.6rem 1.2rem;
}

.message {
  margin-top: 1rem;
  font-weight: bold;
  color: green;
}
</style>
