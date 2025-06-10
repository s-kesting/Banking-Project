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
let metaData = ref([])
let loading = ref(false)
let iban = ref([])
let error = ref(null)
let page = ref(0)
let pageSize = ref(1)
const fetchAccounts = async () => {
    try {
        loading.value = true
        const response = await apiClient.get(`${API_ENDPOINTS.userCheckingsAccounts}?page=${page.value}&pageSize=${pageSize.value}`).then(response => {
            const { content, ...metaData } = response.data
            return { content, metaData }
        })
        console.log(response)
        accounts.value = response.content
        metaData.value = response.metData
    } catch (err) {
        console.log(err)
        error.value = err.message
    } finally {
        loading.value = false
    }
}
onMounted(async () => {
    await fetchAccounts()

}
)
</script>

<style scoped>
.account-list {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}
</style>
