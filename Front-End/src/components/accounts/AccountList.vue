<template>
    <h2>My current accounts</h2>
    <div class="account-list">
        <AccountCard v-for="account in accounts" :key="account.iban" :account-name="account.accountType"
            :iban="account.iban" :balance="account.balance" :account-status="account.verifyAccount" />
    </div>
    <h4>Checking accounts</h4>
    <div class="account-list">
        <AccountCard v-for="account in checkingAccounts.content" :key="account.iban" :account-name="account.accountType"
            :iban="account.iban" :balance="account.balance" :account-status="account.verifyAccount" />
        <PaginationControlls :paginationData="checkingAccounts.metaData" :current-page="page"
            @page-changed="handlePageChange" />
    </div>
    <h4>Saving accounts</h4>
    <div class="account-list">
        <AccountCard v-for="account in savingAccounts.content" :key="account.iban" :account-name="account.accountType"
            :iban="account.iban" :balance="account.balance" :account-status="account.verifyAccount" />
        <PaginationControlls :paginationData="savingAccounts.metaData" :current-page="page"
            @page-changed="handlePageChange" />
    </div>
    <h4>pending accounts</h4>
    <div class="account-list">
        <AccountCard v-for="account in pendingAccounts.content" :key="account.iban" :account-name="account.accountType"
            :iban="account.iban" :balance="account.balance" :account-status="account.verifyAccount" />
        <PaginationControlls :paginationData="pendingAccounts.metaData" :current-page="page"
            @page-changed="handlePageChange" />
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
let checkingAccounts = ref({})
let savingAccounts = ref({})
let pendingAccounts = ref({})
let checkingPage = ref(0)
let savingPage = ref(0)
let pendingPage = ref(0)
let pageSize = ref(3)

onMounted(async () => {
    checkingAccounts.value = await getAccount(API_ENDPOINTS.userCheckingsAccounts, checkingPage.value, pageSize.value);
    savingAccounts.value = await getAccount(API_ENDPOINTS.userSavingsAccounts, savingPage.value, pageSize.value);
    pendingAccounts.value = await getAccount(API_ENDPOINTS.userPendingAccounts, pendingPage.value, pageSize.value);
    console.log(savingAccounts.value)
})

async function getAccount(apiEndpoint, pageType, pageSize) {
    try {
        loading.value = true
        const response = await apiClient.get(`${apiEndpoint}?page=${pageType}&pageSize=${pageSize}`).then(response => {
            const { content, ...metaData } = response.data
            return { content, metaData }

        })
        return response;
    } catch (err) {
        error.value = err.message
        console.log(err.message)
    } finally {
        loading.value = false
    }
}

const fetch = async () => {
    try {
        loading.value = true
        await apiClient.get(`${API_ENDPOINTS.userAccounts}`).then(response => {
        })
    } catch (err) {
        error.value = err.message

        console.log(err.message)
    } finally {
        loading.value = false
    }
}


</script>

<style scoped>
.account-list {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}
</style>
