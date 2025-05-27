export const API_BASE_URL = "http://localhost:8080/api/";

export const API_ENDPOINTS = {
  auth: `${API_BASE_URL}user/auth`,
  users: `${API_BASE_URL}user`,
    register: `${API_BASE_URL}user/auth/register`,
    login: `${API_BASE_URL}user/auth/login`,
  accounts: `${API_BASE_URL}accounts`,
  transactions: `${API_BASE_URL}transactions`,

};
export default API_ENDPOINTS;