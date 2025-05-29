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
import API_ENDPOINTS from "@/config";
import { useAuthStore } from '@/stores/authStore.js'
import { ref, onMounted } from 'vue'
import apiClient from "../utils/apiClient";

let data = ref(null)
let loading = ref(false)
let error = ref(null)
let accounts = ref(null)


onMounted(
    async () => {
        try {
            const authStore = useAuthStore()
            loading.value = true
            const response = await apiClient.get(`${API_ENDPOINTS.userAccounts}`)
            console.log(response.data)
            accounts.value = response.data
        } catch (err) {
            error.value = err.message
            console.log(err.message)
        } finally {
            loading.value = false
        }
    }
)



</script>
