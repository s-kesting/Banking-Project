// src/main.js
import { createApp } from "vue";
import App from "./App.vue";
import { createPinia } from "pinia";
import { createRouter, createWebHistory } from "vue-router";
import axios from "axios";

import Login from "@/components/Authentication.vue";
import Dashboard from "@/components/DashBoard.vue"; // new route view

const routes = [
  { path: "/", name: "Login", component: Login },
  { path: "/Dashboard", name: "Dashboard", component: Dashboard },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// Axios config (optional)
axios.defaults.baseURL = "http://localhost";

const app = createApp(App);
app.use(router);
app.use(createPinia());
app.mount("#app");
