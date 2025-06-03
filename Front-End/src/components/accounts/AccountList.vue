<template>
    <h2>My current accounts</h2>
    <div class="account-list">
        <AccountCard v-for="account in accounts" :key="account.iban" :account-name="account.accountType"
            :iban="account.iban" :balance="account.balance" @pay="onPay"/>
    </div>
</template>

<script setup>
import AccountCard from './AccountCard.vue'
import NewAccountButton from './NewAccountButton.vue'
import { onBeforeMount, onMounted, ref } from 'vue'
import apiClient from '../../utils/apiClient'
import API_ENDPOINTS from '../../config'


let loading = ref(false)
let error = ref(null)
let accounts = ref([])


const fetch = async () => {
    try {
        console.log("calling")
        loading.value = true
        const response = await apiClient.get(`${API_ENDPOINTS.userAccounts}`)
        accounts.value = response.data
    } catch (err) {
        console.log("failed")
        error.value = err.message

        console.log(err.message)
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
