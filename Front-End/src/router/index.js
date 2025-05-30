// router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import App from '@/App.vue'
import AdminDashboard from '@/components/AdminDashboard/EmployeeDashboard.vue'
import DashBoard from '@/components/DashBoard.vue'
import Authentication from '@/components/Authentication.vue'
import EmployeeOverview from '@/components/AdminDashboard/EmployeeOverview.vue'

let employeeRoutes = []

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: Authentication,
        meta: { requiresGuest: true }
    },
    {
        path: '/employee_overview',
        name: 'EmployeeOverview',
        component: EmployeeOverview,
        meta: {
            requiresAuth: true,
            requiresRole: "EMPLOYEE"
        }
    },
    {
        path: '/dashboard',
        name: 'Dashboard',
        component: DashBoard,
        meta: {
            requiresAuth: true,
            requiresRole: "CUSTOMER"
        }
    },
    {
        path: "/admindashboard",
        name: "AdminDashboard",
        component: AdminDashboard,
        meta: {
            requiresAuth: true,
            requiresRole: "EMPLOYEE"
        }
    },
    {
        //TODO: setup user profile
        path: '/profile',
        name: 'Profile',
        meta: { requiresAuth: true }
    },
    {
        path: '/',
        redirect: '/dashboard',
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// Navigation guard
router.beforeEach((to, from, next) => {
    const authStore = useAuthStore()

    // Check if route requires authentication
    if (to.meta.requiresAuth) {

        if (!authStore.checkTokenValidity()) {
            authStore.logout()
            next('/login')
            return
        }
        else {
            if (to.meta.requiresRole) {
                let status = authStore.userRole;
                if (authStore.userRole === "EMPLOYEE") {
                    if (to.path !== '/employee_overview' && to.meta.requiresRole !== "EMPLOYEE") {
                        next('/employee_overview')
                        return
                    }
                }
                if (authStore.userRole === "CUSTOMER") {
                    if (to.path !== '/dashboard') {
                        next('/dashboard')
                        return
                    }
                }

            }
        }
    }

    // Redirect authenticated users away from guest-only pages
    if (to.meta.requiresGuest && authStore.isLoggedIn) {
        next('/dashboard')
        return
    }

    next()
})

export default router
