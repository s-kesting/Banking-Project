<template>
  <MainLayout>
    <template #sidebar>
      <SideBarNavigation
        :activeItem="activeItem"
        @navigate="onNavigate"
      />
    </template>

    <template #main>
      <!-- show account list only when activeItem is Overview -->
      <h2 v-if="activeItem === 'Overview'">My current accounts</h2>
      <AccountList
        v-if="activeItem === 'Overview'"
        :accounts="accounts"
      />

      <!-- when pay is clicked, this view is replaced by the router -->
      <router-view v-if="activeItem === 'Pay'" />
      
      <!-- If/when you add “Save” later: -->
      <div v-if="activeItem === 'Save'">
        <p>Save functionality coming soon.</p>
      </div>
    </template>
  </MainLayout>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import MainLayout from "@/components/layout/MainLayout.vue";
import SideBarNavigation from "./navigation/SideBarNavigation.vue";
import AccountList from "@/components/accounts/AccountList.vue";
import API_ENDPOINTS from "@/config";
import { useAuthStore } from "@/stores/authStore.js";
import apiClient from "@/utils/apiClient";

const router = useRouter();
const activeItem = ref("Overview");
const accounts = ref(null);
const loading = ref(false);
const error = ref(null);

// Called when the click on sidebar
function onNavigate(item) {
  activeItem.value = item;

  if (item === "Pay") {

    router.push({ name: "UserTransaction" });
  } else if (item === "Overview") {

    router.push({ name: "Dashboard" });
  }
  
}

// Fetch accounts 
onMounted(async () => {
  try {
    loading.value = true;
    const authStore = useAuthStore();
    const response = await apiClient.get(`${API_ENDPOINTS.userAccounts}`);
    accounts.value = response.data;
  } catch (err) {
    error.value = err.message;
    console.log(err.message);
  } finally {
    loading.value = false;
  }
});
</script>
