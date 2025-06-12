<template>
  <div class="welcome-box">
    <h2>👋 Welcome, {{ username }}!</h2>
    <p>Your account is currently under review. Please wait for approval from one of our employees.</p>
    <p>We'll notify you as soon as your account becomes active.</p>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useAuthStore } from "@/stores/authStore";

const authStore = useAuthStore();
const username = computed(() => authStore.user?.username || "User");
</script>

<style scoped>
.welcome-box {
  padding: 2rem;
  background-color: #fef9c3;
  border: 1px solid #fde68a;
  border-radius: 12px;
  font-size: 1.2rem;
  color: #92400e;
}
</style>
