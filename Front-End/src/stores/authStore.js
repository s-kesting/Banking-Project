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
        userId: (state) => state.user?.userId || null,
        userRole: (state) => state.user?.role || null,
    },
    actions: {
        async login(username, password) {
            try {
                const res = await axios.post(API_ENDPOINTS.login, {
                    username,
                    password,
                });



                // Save token and user info
                this.token = res.data.token;
                this.user = {
                    id: res.data.id,
                    username: res.data.username,
                    role: res.data.role,
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

        //FIXME: fix this
        checkTokenValidity() {
            if (!this.token) { return false }

            try {
                // Decode JWT payload (basic check - you might want to use a JWT library)
                const payload = JSON.parse(atob(token.value.split('.')[1]))
                const currentTime = Date.now() / 1000

                if (payload.exp < currentTime) {
                    console.log("logout called3")
                    this.logout()
                    return false
                }

                return true
            } catch (error) {
                console.error('Token validation error:', error)
                this.logout()
                return false
            }
        },
        //FIXME:: fixthis
        async initializeAuth() {
            if (this.token && !this.checkTokenValidity()) {
                this.logout()
                console.log("logout called9")
            }
        },
        async register({ username, email, password, phoneNumber, bsn }) {
            await axios.post(API_ENDPOINTS.register, {
                username,
                email,
                password,
                phoneNumber,
                bsn,
            });
        },

        async getUserData() {
            try {
                // You could call your own endpoint if available, for now we just read the token
                const userData = JSON.parse(localStorage.getItem("user"));
                this.user = userData;
                return userData;
            } catch (err) {
                console.log("logout called9")
                this.logout();
                throw err;
            }
        },

        logout() {
            console.log("user logged out")
            this.token = null;
            this.user = null;
            localStorage.removeItem("token");
            localStorage.removeItem("user");
            delete axios.defaults.headers.common["Authorization"];
            console.log("user logged out")
        },
    },
});

export default useAuthStore;
