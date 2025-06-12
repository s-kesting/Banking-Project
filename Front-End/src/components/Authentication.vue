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
          <label for="name">Full Name</label>
          <input
            id="name"
            v-model="name"
            type="text"
            placeholder="Your full name"
            required
          />
        </div>

        <!--LOGIN MODE: show username -->
        <div v-if="isLogin" class="form-group">
          <label for="username">Username</label>
          <input
            id="username"
            v-model="username"
            type="text"
            placeholder="Your username"
            required
          />
        </div>

        <!--REGISTER MODE: show email -->
        <div v-else class="form-group">
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
            placeholder="****"
            required
          />
        </div>

        <!-- Phone Number (only in register mode) -->
        <div v-if="!isLogin" class="form-group">
          <label for="phone">Phone Number</label>
          <input
            id="phone"
            v-model="phoneNumber"
            type="text"
            placeholder="0612345678"
            required
          />
        </div>

        <!-- BSN Number (only in register mode) -->
        <div v-if="!isLogin" class="form-group">
          <label for="bsn">BSN Number</label>
          <input
            id="bsn"
            v-model="bsn"
            type="text"
            placeholder="123456789"
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
import { useRouter, useRoute } from "vue-router";
import { useAuthStore } from "@/stores/authStore";
import API_ENDPOINTS from "@/config.js";
import axios from "axios";
import apiClient from "@/utils/apiClient";

export default {
  name: "Auth",
  setup() {
    const router = useRouter();
    const route = useRoute();
    const name = ref("");
    const isLogin = ref(true);
    const email = ref("");
    const username = ref("");
    const password = ref("");
    const phoneNumber = ref(""); // FIXED
    const bsn = ref(""); //  FIXED
    const error = ref("");
    const successMessage = ref("");

    const authStore = useAuthStore();

    const handleLogin = async () => {
      error.value = "";
      successMessage.value = "";

      try {
        await authStore.login(username.value, password.value);
        console.log("Successful login");

        const verifyStatus = authStore.userStatus;

        if (verifyStatus === "ACTIVE") {
          router.push("/dashboard");
        } else if (verifyStatus === "PENDING") {
          router.push("/pending-welcome");
        } else {
          // Fallback in case of unexpected status
          error.value = "Unexpected account status: " + verifyStatus;
        }
      } catch (err) {
        if (err.response?.status === 403) {
          error.value =
            err.response.data?.error ||
            "Login failed - Account rejected or not allowed";
        } else if (err.response?.status === 401) {
          error.value = "Login failed - Invalid username or password";
        } else {
          error.value =
            err.response?.data?.error || "Login failed. Please try again.";
        }
      }
    };

    const handleRegister = async () => {
      error.value = "";

      if (!name.value.trim()) {
        error.value = "Username cannot be empty.";
        return;
      }

      const phonePattern = /^06\d{8}$/;
      if (!phonePattern.test(phoneNumber.value)) {
        error.value = "Phone number must start with 06 and be 10 digits.";
        return;
      }

      const bsnPattern = /^\d{9}$/;
      if (!bsnPattern.test(bsn.value)) {
        error.value = "BSN must be exactly 9 digits.";
        return;
      }

      try {
        //Username check
        const usernameCheck = await apiClient.get(API_ENDPOINTS.checkUsername, {
          params: { username: name.value },
        });
        if (usernameCheck.data.exists) {
          error.value = "Username is already taken.";
          return;
        }

        // Email check
        const emailCheck = await apiClient.get(API_ENDPOINTS.checkEmail, {
          params: { email: email.value },
        });
        if (emailCheck.data.exists) {
          error.value = "Email is already registered.";
          return;
        }

        // BSN check
        const bsnCheck = await apiClient.get(API_ENDPOINTS.checkBsn, {
          params: { bsn: bsn.value },
        });
        if (bsnCheck.data.exists) {
          error.value = "BSN is already registered.";
          return;
        }

        // All checks passed — continue to register
        await apiClient.post(API_ENDPOINTS.register, {
          username: name.value,
          email: email.value,
          password: password.value,
          phoneNumber: phoneNumber.value,
          bsn: bsn.value,
        });

        isLogin.value = true;
      } catch (err) {
        error.value = err.response?.data?.error || err.message;
      }
    };

    return {
      isLogin,
      name,
      username,
      email,
      password,
      phoneNumber,
      bsn,
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
