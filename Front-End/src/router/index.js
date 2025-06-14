// router/index.js
import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "@/stores/authStore";
import App from "@/App.vue";
import AdminDashboard from "@/components/AdminDashboard/EmployeeDashboard.vue";
import DashBoard from "@/components/DashBoard.vue";
import Authentication from "@/components/Authentication.vue";
import EmployeeOverview from "@/components/AdminDashboard/EmployeeOverview.vue";
import EmployeeTransaction from "@/components/AdminDashboard/EmployeeTransaction.vue";
import UserTransaction from "@/components/transaction/UserTransaction.vue";
import EmployeeTransfering from "@/components/AdminDashboard/EmployeeTransfering.vue";
import ATMView from "@/components/ATM/ATMView.vue";
import PendingWelcome from "@/components/PendingWelcome.vue";

let employeeRoutes = [];

const routes = [
  {
    path: "/login",
    name: "Login",
    component: Authentication,
    meta: { requiresGuest: true },
  },
  {
    path: "/employee_overview",
    name: "EmployeeOverview",
    component: EmployeeOverview,
    meta: {
      requiresAuth: true,
      requiresRole: "EMPLOYEE",
    },
  },
  {
    path: "/dashboard",
    name: "Dashboard",
    component: DashBoard,
    meta: {
      requiresAuth: true,
      requiresRole: "CUSTOMER",
    },
  },
  {
    path: "/admindashboard",
    name: "AdminDashboard",
    component: AdminDashboard,
    meta: {
      requiresAuth: true,
      requiresRole: "EMPLOYEE",
    },
  },
  {
    //TODO: setup user profile
    path: "/profile",
    name: "Profile",
    meta: { requiresAuth: true },
  },
  {
    path: "/",
    redirect: "/dashboard",
  },
  {
    path: "/employee_transaction",
    name: "EmployeeTransaction",
    component: EmployeeTransaction,
    meta: {
      requiresAuth: true,
      requiresRole: "EMPLOYEE",
    },
  },
  {
    path: "/user_transaction",
    name: "UserTransaction",
    component: UserTransaction,
    meta: {
      requiresAuth: true,
      requiresRole: "CUSTOMER",
    },
    path: "/employee_transfering",
    name: "EmployeeTransfering",
    component: EmployeeTransfering,
    meta: {
      requiresAuth: true,
      requiresRole: "EMPLOYEE",
    },
  },
  {
    path: "/atm",
    name: "ATM",
    component: ATMView,
    meta: {
      requiresAuth: true,
      requiresRole: "CUSTOMER",
    },
  },
  {
    path: "/pending-welcome",
    name: "PendingWelcome",
    component: PendingWelcome,
    meta: {
      requiresAuth: true,
      requiresRole: "CUSTOMER",
    },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// Navigation guard
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();

  // Check if route requires authentication
  if (to.meta.requiresAuth) {
    if (!authStore.checkTokenValidity()) {
      authStore.logout();
      next("/login");
      return;
    } else {
      if (to.meta.requiresRole) {
        let status = authStore.userRole;
        if (authStore.userRole === "EMPLOYEE") {
          if (
            to.path !== "/employee_overview" &&
            to.meta.requiresRole !== "EMPLOYEE"
          ) {
            next("/employee_overview");
            return;
          }
        }
        if (
          authStore.userRole === "CUSTOMER" &&
          to.meta.requiresRole !== "CUSTOMER"
        ) {
          next("/dashboard");
          return;
        }
      }
    }
  }

  // Redirect authenticated users away from guest-only pages
  if (to.meta.requiresGuest && authStore.isLoggedIn) {
    next("/dashboard");
    return;
  }

  next();
});

export default router;
