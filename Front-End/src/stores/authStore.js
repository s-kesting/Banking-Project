import { defineStore } from "pinia";
import axios from "axios";
import { API_ENDPOINTS } from "@/config";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    token: localStorage.getItem("token") || null,
    user: JSON.parse(localStorage.getItem("user")) || null,
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    userId: (state) => state.user?.id || null,
    userRole: (state) => state.user?.role || null,
  },

  actions: {
    async login(username, password) {
      try {
        const res = await axios.post("/api/user/auth/login", {

          username,
          password,
        });

        // Save token and user info
        this.token = res.data.token;
        this.user = {
          username: res.data.username,
          role: res.data.role,
          userId: res.data.userId,
        };

        // Persist in localStorage
        localStorage.setItem("token", this.token);
        localStorage.setItem("user", JSON.stringify(this.user));

        // Set axios header for future requests
        axios.defaults.headers.common["Authorization"] = `Bearer ${this.token}`;
      } catch (err) {
        this.logout();
        throw err;
      }
    },

    async register({ username, email, password, phoneNumber, bsn }) {
      await axios.post("/api/user/auth/register", {
        username,
        email,
        password,
        phoneNumber,
        bsn,
      });
    },

    async fetchMe() {
      try {
        // You could call your own endpoint if available, for now we just read the token
        const userData = JSON.parse(localStorage.getItem("user"));
        this.user = userData;
        return userData;
      } catch (err) {
        this.logout();
        throw err;
      }
    },

    logout() {
      this.token = null;
      this.user = null;
      localStorage.removeItem("token");
      localStorage.removeItem("user");
      delete axios.defaults.headers.common["Authorization"];
    },
  },
});
