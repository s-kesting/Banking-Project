<template>
    <h2>My current savings accounts</h2>
    <div class="account-list">
        <AccountCard v-for="account in accounts" :key="account.iban" :account-name="account.accountType"
            :iban="account.iban" :balance="account.balance" :load-transactions="true" />

    </div>
</template>

<script setup>
import AccountCard from './AccountCard.vue'
import NewAccountButton from './NewAccountButton.vue'
import { onMounted, ref } from 'vue'
import apiClient from '../../utils/apiClient'
import API_ENDPOINTS from '../../config'

let accounts = ref([])
let loading = ref(false)
let error = ref(null)
const fetch = async () => {
    try {
        loading.value = true
        const response = await apiClient.get(`${API_ENDPOINTS.userSavingsAccounts}`)
        accounts.value = response.data
    } catch (err) {
        error.value = err.message
    } finally {
        loading.value = false
    }
}



onMounted(() => { fetch() }
)
</script>

<style scoped>
.account-list {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}
</style>
