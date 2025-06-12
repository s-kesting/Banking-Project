<template>
  <div class="pending-welcome">
    <h1>👋 Welcome to Inholland Banking!</h1>
    <p>
      Your account is currently under verification. Once approved, you’ll be
      able to access all banking features.
    </p>

    <button class="logout-button" @click="handleLogout">Logout</button>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/authStore";

const router = useRouter();
const authStore = useAuthStore();

const handleLogout = () => {
  authStore.logout();
  router.push("/");
};
</script>

<style scoped>
.pending-welcome {
  padding: 2rem;
  text-align: center;
}

.logout-button {
  margin-top: 1.5rem;
  padding: 0.6rem 1.2rem;
  background-color: #dc3545;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s;
}

.logout-button:hover {
  background-color: #c82333;
}
</style>
