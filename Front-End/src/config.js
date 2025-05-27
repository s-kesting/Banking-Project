// src/config.js
export const API_BASE_URL = "http://localhost:8080";

export const API_ENDPOINTS = {
  auth: `${API_BASE_URL}/api/user/auth`,
  users: `${API_BASE_URL}/user`,
  accounts: `${API_BASE_URL}/accounts`,
  transactions: `${API_BASE_URL}/transactions`,
  employee: `${API_BASE_URL}/employee`,
};
export default API_ENDPOINTS;
