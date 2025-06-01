<template>
  <div class="transfer-wrapper">
    <h1 class="title">🏦 Employee Fund Transfer</h1>

    <div class="card">
      <form @submit.prevent="handleTransfer">
        <div class="form-group">
          <label for="sender">🔻 Sender IBAN</label>
          <input
            id="sender"
            v-model="senderIBAN"
            type="text"
            placeholder="NL91ABNA1234567890"
            required
          />
        </div>

        <div class="form-group">
          <label for="receiver">🔺 Receiver IBAN</label>
          <input
            id="receiver"
            v-model="receiverIBAN"
            type="text"
            placeholder="NL91ABNA0987654321"
            required
          />
        </div>

        <div class="form-group">
          <label for="amount">💰 Amount (&euro;)</label>
          <input
            id="amount"
            v-model.number="amount"
            type="number"
            step="0.01"
            placeholder="100.00"
            required
          />
        </div>

        <div class="form-group">
          <label for="description">📝 Description</label>
          <input
            id="description"
            v-model="description"
            type="text"
            placeholder="e.g., Payment for invoice #123"
          />
        </div>

        <button type="submit" class="btn-transfer">Transfer Funds</button>

        <p v-if="error" class="error-message">❌ {{ error }}</p>
        <p v-if="success" class="success-message">✅ {{ success }}</p>
      </form>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import API_ENDPOINTS from "@/config";

export default {
  name: "EmployeeTransfer",
  data() {
    return {
      senderIBAN: "",
      receiverIBAN: "",
      amount: null,
      description: "",
      error: "",
      success: "",
    };
  },
  methods: {
    async handleTransfer() {
      this.error = "";
      this.success = "";

      if (!this.senderIBAN || !this.receiverIBAN || !this.amount) {
        this.error = "All fields must be filled correctly.";
        return;
      }

      try {
        const response = await axios.post(
          `${API_ENDPOINTS.transactions}/employee-transfer`,
          {
            senderIBAN: this.senderIBAN,
            receiverIBAN: this.receiverIBAN,
            amount: this.amount,
            description: this.description,
          }
        );

        this.success = "Transfer completed successfully!";
        this.senderIBAN = "";
        this.receiverIBAN = "";
        this.amount = null;
        this.description = "";
      } catch (err) {
        this.error = err.response?.data?.error || "Transfer failed.";
      }
    },
  },
};
</script>

<style scoped>
.transfer-wrapper {
  max-width: 500px;
  margin: 40px auto;
  background: #fefefe;
  padding: 30px;
  border-radius: 16px;
  box-shadow: 0 0 12px rgba(0, 0, 0, 0.1);
}
.title {
  text-align: center;
  font-size: 24px;
  margin-bottom: 20px;
}
.card {
  display: flex;
  flex-direction: column;
}
.form-group {
  margin-bottom: 15px;
}
label {
  display: block;
  font-weight: bold;
  margin-bottom: 5px;
}
input[type="text"],
input[type="number"] {
  width: 100%;
  padding: 10px;
  border-radius: 6px;
  border: 1px solid #ccc;
  font-size: 16px;
}
.btn-transfer {
  width: 100%;
  background: #0e5ef7;
  color: white;
  padding: 12px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.3s;
}
.btn-transfer:hover {
  background: #083cb2;
}
.error-message {
  color: red;
  margin-top: 10px;
}
.success-message {
  color: green;
  margin-top: 10px;
}
</style>
