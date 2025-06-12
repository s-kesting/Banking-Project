<template>
  <div class="main-layout">
    <TopNav />

    <div class="layout-body">
      <aside class="sidebar" v-if="!isPending">
        <button class="logout-button" @click="logout">Logout</button>

        <SidebarNavigation
          :items="items"
          :activeItem="activeItem"
          @active-item="setActive"
          @navigate="setComponent"
        />
      </aside>

      <section class="content">
        <component :is="isPending ? PendingWelcome : content" />
      </section>
    </div>

    <footer class="footer">
      <AppFooter />
    </footer>
  </div>
</template>

<script setup>
import { ref, shallowRef, computed } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/authStore";

import TopNav from "./layout/TopNav.vue";
import AppFooter from "./footer/AppFooter.vue";
import SidebarNavigation from "@/components/navigation/SideBarNavigation.vue";
import AccountList from "@/components/accounts/AccountList.vue";
import SavingsAccount from "./accounts/SavingsAccount.vue";
import CheckingsAccount from "./accounts/CheckingsAccount.vue";
import CreateAccount from "./accounts/CreateAccount.vue";
import UserTransaction from "./transaction/UserTransaction.vue";

// auth & router
const router = useRouter();
const authStore = useAuthStore();

// check verify status
const isPending = computed(() => authStore.user?.verifyUser === "PENDING");

// sidebar content
let content = shallowRef(AccountList);
let activeItem = ref("Overview");

const items = {
  Overview: AccountList,
  Checkings: CheckingsAccount,
  Savings: SavingsAccount,
  Transfer: UserTransaction,
  "New account": CreateAccount,
};

function setComponent(component) {
  content.value = component;
}

function setActive(item) {
  activeItem.value = item;
}

function logout() {
  authStore.logout();
  router.push("/login");
}
</script>

<style scoped>
.main-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f9fafb;
}

.layout-body {
  display: grid;
  grid-template-columns: 220px 1fr;
  flex: 1;
}

.sidebar {
  background-color: #e5e7eb;
  padding: 1.5rem 1rem;
  border-right: 1px solid #d1d5db;
}

.content {
  padding: 2rem;
}

.footer {
  background-color: #f3f4f6;
  padding: 1rem;
  text-align: center;
  border-top: 1px solid #d1d5db;
}

.logout-button {
  display: block;
  width: 100%;
  padding: 10px;
  margin-bottom: 1.5rem;
  background-color: #ef4444;
  color: white;
  font-weight: bold;
  font-size: 1rem;
  text-align: left;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.2s ease;
}

.logout-button:hover {
  background-color: #dc2626;
  transform: translateY(-1px);
}
</style>
