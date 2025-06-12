<template>
  <div class="main-layout">
    <TopNav />

    <div class="layout-body">
      <aside class="sidebar" v-if="!isPending">
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
import PendingWelcome from "@/components/PendingWelcome.vue"; // ✅ Imported welcome component

const router = useRouter();
const authStore = useAuthStore();

const content = shallowRef(AccountList);
const activeItem = ref("Overview");

const items = {
  Overview: AccountList,
  Checkings: CheckingsAccount,
  Savings: SavingsAccount,
  Transfer: UserTransaction,
  "New account": CreateAccount,
  Logout: null,
};

const user = computed(() => authStore.user);
const isPending = computed(() => user.value?.verifyUser === "PENDING");

function setComponent(component) {
  if (component === null) {
    logout();
  } else {
    content.value = component;
  }
}

function logout() {
  authStore.logout();
  router.push("/login");
}

function setActive(item) {
  activeItem.value = item;
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
  padding: 1.5rem;
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
</style>
