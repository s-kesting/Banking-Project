// router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import App from '@/App.vue'
import AdminDashboard from '@/components/AdminDashboard/EmployeeDashboard.vue'
import DashBoard from '@/components/DashBoard.vue'
import Authentication from '@/components/Authentication.vue'

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: Authentication,
        meta: { requiresGuest: true }
    },
    {
        path: '/dashboard',
        name: 'Dashboard',
        component: DashBoard,
        meta: {
            requiresAuth: true,
            requiresRole: "EMPLOYEE"
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


    //authStore.initializeAuth()

    // Check if route requires authentication
    if (to.meta.requiresAuth) {
        if (!authStore.isLoggedIn) {
            next('/login')
            return
        }

        if (to.meta.requiresRole) {
            console.log(authStore.userRole);
            let status = authStore.userRole;
            if (authStore.userRole === "EMPLOYEE") {
                if (to.path !== '/admindashboard') {
                    next('/admindashboard')
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
        // Check role-based access
        //        if (to.meta.requiresRole && authStore.userRole == "EMPLOYEE") {
        //            next('/admindashboard')
        //            return
        //        } else if (to.meta.requiresRole && authStore.userRole == "CUSTOMER") {
        //            next('/dashboard')
        //            return
        //        }
    }

    // Redirect authenticated users away from guest-only pages
    if (to.meta.requiresGuest && authStore.isLoggedIn) {
        next('/dashboard')
        return
    }

    next()
})

export default router
