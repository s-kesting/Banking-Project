// utils/apiClient.js
import axios from 'axios'
import useAuthStore from '@/stores/authStore'
import { API_BASE_URL } from '../config'

/*
 * creates an axios client preconfigured to handle the JWT token
 */

export class apiClient {


    constructor() {
        this.axiosSession = axios.create()

        this.axiosSession.interceptors.request.use(
            (config) => {
                const authStore = useAuthStore();
                if (authStore.token) {
                    config.headers.Authorization = `Bearer ${authStore.token}`
                }
                return config;
            },
            (error) => {
                return Promise.reject(error)
            }
        );
        this.get = this.axiosSession.get.bind(this.axiosSession);
        this.post = this.axiosSession.post.bind(this.axiosSession);
        this.put = this.axiosSession.put.bind(this.axiosSession);
        this.delete = this.axiosSession.delete.bind(this.axiosSession);
    }

}

export default new apiClient;

// Request interceptor to add token
//apiClient.interceptors.request.use(
//    (config) => {
//        const authStore = useAuthStore()
//
//        if (authStore.token) {
//            config.headers.Authorization = `Bearer ${authStore.token}`
//        }
//
//        return config
//    },
//    (error) => {
//        return Promise.reject(error)
//    }
//)
//
//// Response interceptor to handle token refresh
//apiClient.interceptors.response.use(
//    (response) => {
//        return response
//    },
//    async (error) => {
//        const authStore = useAuthStore()
//        const originalRequest = error.config
//
//        if (error.response?.status === 401 && !originalRequest._retry) {
//            originalRequest._retry = true
//
//            try {
//                const refreshSuccess = await authStore.refreshToken()
//
//                if (refreshSuccess) {
//                    // Retry the original request with new token
//                    originalRequest.headers.Authorization = `Bearer ${authStore.token}`
//                    return apiClient(originalRequest)
//                }
//            } catch (refreshError) {
//                // Refresh failed, redirect to login
//                authStore.logout()
//                window.location.href = '/login'
//            }
//        }
//
//        return Promise.reject(error)
//    }
//)

