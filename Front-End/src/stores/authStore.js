import { defineStore } from "pinia";
import axios from "axios";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    token: localStorage.getItem("token") || null,
    user: JSON.parse(localStorage.getItem("user")) || null,
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.user?.role === "admin",
    isUser: (state) => state.user?.role === "user",
    userId: (state) => state.user?.id || null, // ACCESS USER ID GLOBALLY
  },

  actions: {
    async login(email, password) {
      try {
        const res = await axios.post("/auth/login", { email, password });

        this.token = res.data.token;
        localStorage.setItem("token", this.token);

        axios.defaults.headers.common["Authorization"] = `Bearer ${this.token}`;
        await this.fetchMe();
      } catch (err) {
        this.logout();
        throw err;
      }
    },

    async register(name, email, password) {
      await axios.post("/auth/register", { name, email, password });
    },

    async fetchMe() {
      try {
        const res = await axios.get("/auth/me");
        this.user = res.data;
        localStorage.setItem("user", JSON.stringify(this.user)); // Save to localStorage
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
