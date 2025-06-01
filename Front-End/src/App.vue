<template>
  <div>
    <!-- Hide Navbar on /auth -->
    <nav v-if="!isAuthPage" class="navbar">
      <!-- Left Section -->
      <div class="navbar-left">
        <div class="logo">
          <i class="fas fa-building-columns"></i>
          Banking Application
        </div>
        <ul class="nav-links">
          <!-- Show employee-specific links -->
          <template v-if="user?.role === 'EMPLOYEE'">
            <li>
              <router-link to="/employee_overview">
                <i class="fas fa-chart-line"></i> Dashboard
              </router-link>
            </li>
            <li>
              <router-link to="/admindashboard">
                <i class="fas fa-users-cog"></i> User & Account Management
              </router-link>
            </li>
            <li>
              <router-link to="/employee_transaction">
                <i class="fas fa-exchange-alt"></i> Transaction Management
              </router-link>
            </li>
            <li>
              <router-link to="/employee_transfering">
                <i class="fas fa-exchange-alt"></i> Transfering Fund
              </router-link>
            </li>
          </template>

          <!-- Show regular customer links -->
          <template v-else>
            <li>
              <router-link to="/test">
                <i class="fas fa-boxes-stacked"></i> Products
              </router-link>
            </li>
            <li>
              <router-link to="/self-service">
                <i class="fas fa-tools"></i> Self Service
              </router-link>
            </li>
            <li>
              <router-link to="/tasklist">
                <i class="fas fa-clipboard-check"></i> Tasklist
              </router-link>
            </li>
          </template>
        </ul>
      </div>

      <!-- Right Section -->
      <div class="navbar-right">
        <i class="fas fa-magnifying-glass icon"></i>
        <div class="notifications">
          <i class="fas fa-bell icon"></i>
          <span class="badge">4</span>
        </div>
        <span class="username">{{ user?.name || "Guest" }}</span>
        <div class="avatar">RL</div>
        <i class="fas fa-chevron-down icon" @click="logout"></i>
      </div>
    </nav>

    <!-- Main Content -->
    <router-view />
  </div>
</template>

<script>
import { computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";
import { useAuthStore } from "@/stores/authStore";

export default {
  name: "App",
  setup() {
    const router = useRouter();
    const route = useRoute();
    const authStore = useAuthStore();

    const isLoggedIn = computed(() => authStore.isLoggedIn);
    const user = computed(() => authStore.user);

    const isAuthPage = computed(() => route.path === "/login");

    // Handle login state and token persistence
    onMounted(async () => {
      const token = localStorage.getItem("token");
      if (token) {
        axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
        try {
          await authStore.getUserData();
        } catch (err) {
          authStore.logout();
          router.push("/");
        }
      }
    });

    const logout = () => {
      authStore.logout();
      router.push("/");
    };

    return {
      isLoggedIn,
      user,
      logout,
      isAuthPage,
    };
  },
};
</script>

<style scoped>
/* ... your CSS remains unchanged ... */
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 12px 24px;
  border-bottom: 2px solid #00695c;
  font-family: "Segoe UI", sans-serif;
  flex-wrap: wrap;
}

.logo {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1f2937;
  display: flex;
  align-items: center;
  gap: 8px;
}

.logo i {
  color: #047857;
}

.navbar-left {
  display: flex;
  align-items: center;
  gap: 32px;
  flex-wrap: wrap;
}

.nav-links {
  list-style: none;
  display: flex;
  gap: 20px;
  padding: 0;
  margin: 0;
  flex-wrap: wrap;
}

.nav-links a {
  text-decoration: none;
  color: #065f46;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 1rem;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.icon {
  font-size: 1.1rem;
  color: #065f46;
  cursor: pointer;
}

.notifications {
  position: relative;
}

.badge {
  position: absolute;
  top: -8px;
  right: -10px;
  background-color: #dc2626;
  color: white;
  font-size: 0.75rem;
  padding: 2px 6px;
  border-radius: 9999px;
}

.username {
  font-weight: 500;
  color: #374151;
  white-space: nowrap;
}

.avatar {
  background-color: #f3e8ff;
  color: #6b21a8;
  font-weight: bold;
  padding: 6px 12px;
  border-radius: 9999px;
  white-space: nowrap;
}

@media (max-width: 768px) {
  .navbar {
    flex-direction: column;
    align-items: flex-start;
  }

  .navbar-left,
  .navbar-right {
    width: 100%;
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .nav-links {
    flex-direction: column;
    gap: 10px;
  }

  .avatar {
    padding: 6px 10px;
  }
}
</style>
