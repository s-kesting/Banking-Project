// src/config.js
export const API_BASE_URL = "http://localhost:8080/api/";

export const API_ENDPOINTS = {
  auth: `${API_BASE_URL}user/auth`,
  users: `${API_BASE_URL}user`,
  account: `${API_BASE_URL}account`,
  userAccounts: `${API_BASE_URL}account/user`,
  transactions: `${API_BASE_URL}transactions`,
  employee: `${API_BASE_URL}employee`,

  //Authentication
  register: `${API_BASE_URL}user/auth/register`,
  login: `${API_BASE_URL}user/auth/login`,
  checkUsername: `${API_BASE_URL}user/auth/check-username`,
  checkEmail: `${API_BASE_URL}user/auth/check-email`,
  checkBsn: `${API_BASE_URL}user/auth/check-bsn`,
};
export default API_ENDPOINTS;
