<template>
    <div class="auth-wrapper">
      <div class="auth-tabs">
        <button :class="{ active: isLogin }" @click="isLogin = true">
          Login
        </button>
        <button :class="{ active: !isLogin }" @click="isLogin = false">
          Register
        </button>
      </div>
  
      <div class="auth-card">
        <h2 class="auth-title">{{ isLogin ? "Login" : "Register" }}</h2>
        <form @submit.prevent="isLogin ? handleLogin() : handleRegister()">
          <!-- Name field only in register mode -->
          <div v-if="!isLogin" class="form-group">
            <label for="name">Name</label>
            <input
              id="name"
              v-model="name"
              type="text"
              placeholder="Your full name"
              required
            />
          </div>
          <div class="form-group">
            <label for="email">Email</label>
            <input
              id="email"
              v-model="email"
              type="email"
              placeholder="your@email.com"
              required
            />
          </div>
          <div class="form-group">
            <label for="password">Password</label>
            <input
              id="password"
              v-model="password"
              type="password"
              placeholder="******"
              required
            />
          </div>
          <button type="submit" class="auth-button">
            {{ isLogin ? "Login" : "Register" }}
          </button>
          <p v-if="error" class="error-message">{{ error }}</p>
        </form>
      </div>
    </div>
  </template>
  
  <script>
  import { ref } from "vue";
  import { useAuthStore } from "@/stores/authStore";
  import { useRouter } from "vue-router";
  
  export default {
    name: "Auth",
    setup() {
      const isLogin = ref(true);
      const name = ref("");
      const email = ref("");
      const password = ref("");
      const error = ref("");
  
      const authStore = useAuthStore();
      const router = useRouter();
  
      const handleLogin = async () => {
        error.value = "";
        try {
          await authStore.login(email.value, password.value);
          router.push("/"); // redirect to home or dashboard
        } catch (err) {
          error.value = err.response?.data?.error || err.message;
        }
      };
  
      const handleRegister = async () => {
        error.value = "";
        try {
          await authStore.register(name.value, email.value, password.value);
          isLogin.value = true;
        } catch (err) {
          error.value = err.response?.data?.error || err.message;
        }
      };
  
      return {
        isLogin,
        name,
        email,
        password,
        error,
        handleLogin,
        handleRegister,
      };
    },
  };
  </script>
  
  <style scoped>
  .auth-wrapper {
    max-width: 400px;
    margin: 60px auto;
    font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
  }
  
  .auth-tabs {
    display: flex;
    border: 1px solid #ccc;
    border-bottom: none;
    border-radius: 8px 8px 0 0;
    overflow: hidden;
  }
  
  .auth-tabs button {
    flex: 1;
    padding: 12px 0;
    background: #f0f0f0;
    border: none;
    cursor: pointer;
    font-size: 1.05rem;
    font-weight: 500;
    transition: background 0.3s ease;
  }
  
  .auth-tabs button.active {
    background-color: #007bff;
    color: white;
    font-weight: bold;
  }
  
  .auth-card {
    background: #ffffff;
    border: 1px solid #ccc;
    border-top: none;
    padding: 30px 25px;
    border-radius: 0 0 8px 8px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  }
  
  .auth-title {
    text-align: center;
    margin-bottom: 25px;
    font-size: 1.7rem;
    font-weight: bold;
    color: #333;
  }
  
  .form-group {
    margin-bottom: 18px;
  }
  
  .form-group label {
    display: block;
    font-weight: 600;
    margin-bottom: 6px;
    color: #444;
  }
  
  .form-group input {
    width: 100%;
    padding: 10px 12px;
    font-size: 1rem;
    border-radius: 6px;
    border: 1px solid #ccc;
    transition: border-color 0.3s ease;
  }
  
  .form-group input:focus {
    outline: none;
    border-color: #007bff;
  }
  
  .auth-button {
    width: 100%;
    padding: 12px;
    border: none;
    background-color: #007bff;
    color: white;
    font-size: 1.1rem;
    font-weight: bold;
    border-radius: 6px;
    cursor: pointer;
    transition: background-color 0.3s ease;
    margin-top: 10px;
  }
  
  .auth-button:hover {
    background-color: #0056b3;
  }
  
  .error-message {
    color: red;
    margin-top: 15px;
    text-align: center;
    font-weight: bold;
  }
  </style>
  