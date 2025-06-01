// src/config.js
export const API_BASE_URL = "http://localhost:8080/api/";

export const API_ENDPOINTS = {
    //autherisation and login
    auth: `${API_BASE_URL}user/auth`,
    register: `${API_BASE_URL}user/auth/register`,
    login: `${API_BASE_URL}user/auth/login`,

    // accounts
    account: `${API_BASE_URL}account`,
    userAccounts: `${API_BASE_URL}account/user`,
    userSavingsAccounts: `${API_BASE_URL}account/user/savings`,
    userCheckingsAccounts: `${API_BASE_URL}account/user/checkings`,
    userNewAccount: `${API_BASE_URL}account/user/newAccount`,

    transactions: `${API_BASE_URL}transactions`,
    employee: `${API_BASE_URL}employee`,

    //users
    users: `${API_BASE_URL}user`,

};
export default API_ENDPOINTS;
