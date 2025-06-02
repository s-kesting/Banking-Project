// src/config.js
export const API_BASE_URL = "http://localhost:8080/api/";

export const API_ENDPOINTS = {
    auth: `${API_BASE_URL}user/auth`,
    users: `${API_BASE_URL}user`,
    register: `${API_BASE_URL}user/auth/register`,
    login: `${API_BASE_URL}user/auth/login`,
    account: `${API_BASE_URL}account`,
    userAccounts: `${API_BASE_URL}account/user`,
    transactions: `${API_BASE_URL}transactions`,
    employee: `${API_BASE_URL}employee`,
    usertransaction: `${API_BASE_URL}transactions/user`,
};
export default API_ENDPOINTS;
