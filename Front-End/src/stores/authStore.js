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
                    userId: res.data.userId,
                    username: res.data.username,
                    role: res.data.role,
                };

                // Persist in localStorage
                localStorage.setItem("token", this.token);
                localStorage.setItem("user", JSON.stringify(this.user));

                // Set axios header for future requests
            } catch (err) {
                this.logout();
                throw err;
            }
        },

        checkTokenValidity() {
            if (!this.token) {
                console.log('JWT token not present')
                return false;
            }

            try {
                const parts = this.token.split('.');
                console.log(parts)
                if (parts.length !== 3) {
                    console.log('JWT token in wrong format')
                    return false;
                }

                const payload = JSON.parse(atob(parts[1]));


                const currentTime = Math.floor(Date.now() / 1000);

                if (payload.exp < currentTime) {
                    console.log(payload.exp)
                    console.log(currentTime)
                    console.log('JWT token expired')
                    return false
                } else {
                    return true
                }

            } catch (error) {
                return false;
            }
        },
        async getUserData() {
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
            console.log("user logged out")
            this.token = null;
            this.user = null;
            localStorage.removeItem("token");
            localStorage.removeItem("user");
            console.log("user logged out")
        },
    },
});

export default useAuthStore;
