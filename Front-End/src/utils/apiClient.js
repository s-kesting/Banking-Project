// utils/apiClient.js
import axios from "axios";
import useAuthStore from "@/stores/authStore";

/*
 * creates an axios client preconfigured to handle the JWT token
 */

export class apiClient {
    constructor() {
        this.axiosSession = axios.create();

        this.axiosSession.interceptors.request.use(
            (config) => {
                const authStore = useAuthStore();
                if (authStore.token) {
                    config.headers.Authorization = `Bearer ${authStore.token}`;
                }
                return config;
            },
            (error) => {
                return Promise.reject(error);
            }
        );

        this.axiosSession.interceptors.response.use(
            (response) => response,
            (error) => {
                const authStore = useAuthStore();

                if (error.response?.status === 401) {
                    console.warn("❌ Token is expired or invalid. Logging out.");
                    authStore.logout(); // Clear store
                    localStorage.removeItem("token"); // Remove token
                    window.location.href = "/login"; // Redirect to login
                }

                return Promise.reject(error);
            }
        );

        this.get = this.axiosSession.get.bind(this.axiosSession);
        this.post = this.axiosSession.post.bind(this.axiosSession);
        this.put = this.axiosSession.put.bind(this.axiosSession);
        this.delete = this.axiosSession.delete.bind(this.axiosSession);
    }
}

export default new apiClient();


