<template>
  <header class="top-nav">
    <div class="left">
      <h1>Products</h1>
    </div>
    <div class="right">
      <button class="transfer-btn" @click="initiateATM">Transfer</button>
    </div>
  </header>
</template>

<script>
import axios from "axios";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/authStore";
import API_ENDPOINTS from "@/config";

export default {
  setup() {
    const router = useRouter();
    const authStore = useAuthStore();

    const initiateATM = async () => {
      try {
        const accountId = 24; // TEST VALUE, HARDCODED
        if (!accountId) {
          alert("No account ID found.");
          return;
        }

        const response = await axios.post(
          `${API_ENDPOINTS.ATM}/start-session`,
          { accountId }
        );

        const sessionId = response.data.sessionId;
        router.push({ name: "ATMTransfer", query: { sessionId, accountId } });
      } catch (error) {
        console.error("ATM session error:", error);
        alert("Could not start ATM session.");
      }
    };

    return {
      initiateATM,
    };
  },
};
</script>

<style scoped>
.top-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #007a5e;
  color: white;
  padding: 1rem 2rem;
}

.top-nav .transfer-btn {
  background-color: #facc15;
  border: none;
  padding: 0.5rem 1rem;
  font-weight: bold;
  border-radius: 4px;
  cursor: pointer;
}
</style>
