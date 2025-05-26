<template>
  <MainLayout>
    <template #sidebar>
      <SidebarNavigation activeItem="Pay" />
    </template>

    <template #main>
      <h2>My current accounts</h2>
      <AccountList :accounts="accounts" />
    </template>
  </MainLayout>
</template>

<script setup>
import MainLayout from "@/components/layout/MainLayout.vue";
import SidebarNavigation from "@/components/navigation/SideBarNavigation.vue";
import AccountList from "@/components/accounts/AccountList.vue";
import axios from 'axios'
import  API_ENDPOINTS  from "@/config";
import { useAuthStore } from '@/stores/authStore.js'
import { ref, onMounted } from 'vue'

let data = ref(null)
let loading = ref(false)
let error = ref(null)

const authStore = useAuthStore()
const userId = authStore.userId

const getAccounts = async()=>{
  try {
    loading.value = true
    const response = await axios.get(API_ENDPOINTS.accounts+userId)
    console.log(response.data)
    data.value = response.data
  } catch (err) {
    error.value = err.message
  } finally {
    loading.value = false
  }
}

const accounts = [
  {
    accountName: "Student account",
    iban: "NL24 ABNA 0126 6816 43",
    balance: 67.52,
  },
];
</script>
