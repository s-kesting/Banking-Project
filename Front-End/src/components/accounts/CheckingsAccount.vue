<template>
    <h2>My current checkings accounts</h2>
    <div class="account-list">
        <AccountCard v-for="account in accounts" :key="account.iban" :account-name="account.accountType"
            :iban="account.iban" :balance="account.balance" :load-transactions="true" />
    </div>
</template>

<script setup>
import AccountCard from './AccountCard.vue'
import { onMounted, ref } from 'vue'
import apiClient from '../../utils/apiClient'
import API_ENDPOINTS from '../../config'

let accounts = ref([])
let loading = ref(false)
let iban = ref([])
let error = ref(null)
const fetchAccounts = async () => {
    try {
        loading.value = true
        const response = await apiClient.get(`${API_ENDPOINTS.userCheckingsAccounts}`)
        accounts.value = response.data
    } catch (err) {
        error.value = err.message
    } finally {
        loading.value = false
    }
}
onMounted(() => { fetchAccounts() }
)
</script>

<style scoped>
.account-list {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}
</style>
