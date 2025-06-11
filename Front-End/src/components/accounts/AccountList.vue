<template>
    <h2>My current accounts</h2>
    <div class="account-list">
        <AccountCard v-for="account in accounts" :key="account.iban" :account-name="account.accountType"
            :iban="account.iban" :balance="account.balance" :account-status="account.verifyAccount" />
    </div>
    <h4>Checking accounts</h4>
    <div class="account-list">
        <div v-if="!checkingAccounts.loading">
        <AccountCard v-for="account in checkingAccounts.data.content" :key="account.iban" :account-name="account.accountType"
            :iban="account.iban" :balance="account.balance" :account-status="account.verifyAccount" />
        <PaginationControlls :paginationData="checkingAccounts.data.metaData" :current-page="checkingAccounts.page"
            @page-changed="(newPage) => handlePageChange(newPage,checkingAccounts)" />
        </div>
    </div>
    <h4>Saving accounts</h4>
    <div class="account-list">
        <div v-if="!savingAccounts.loading">
        <AccountCard v-for="account in savingAccounts.data.content" :key="account.iban" :account-name="account.accountType"
            :iban="account.iban" :balance="account.balance" :account-status="account.verifyAccount" />
        <PaginationControlls :paginationData="savingAccounts.data.metaData" :current-page="checkingAccounts.page"
            @page-changed="(newPage) => handlePageChange(newPage,savingAccounts)" />
    </div>
    </div>
    <h4>pending accounts</h4>
    <div class="account-list">
        <div v-if="!pendingAccounts.loading">
        <AccountCard v-for="account in pendingAccounts.data.content" :key="account.iban" :account-name="account.accountType"
            :iban="account.iban" :balance="account.balance" :account-status="account.verifyAccount" />
        <PaginationControlls :paginationData="pendingAccounts.data.metaData" :current-page="checkingAccounts.page"
                @page-changed="(newPage) => handlePageChange(newPage,pendingAccounts)" />
    </div>
    </div>
</template>

<script setup>
import AccountCard from './AccountCard.vue'
import PaginationControlls from '../navigation/PaginationControlls.vue'
import {  onMounted, ref } from 'vue'
import apiClient from '../../utils/apiClient'
import API_ENDPOINTS from '../../config'

const accountDataStruct = () => {
    return {data : {content : {}, metaData : {}},
    page : 0,
        url : ""}
}

let loading = ref(false)
let error = ref(null)
let checkingAccounts = ref(accountDataStruct())
let savingAccounts = ref(accountDataStruct())
let pendingAccounts = ref(accountDataStruct())
let pageSize = ref(2)

onMounted(async () => {

    checkingAccounts.value.page = 0;
    checkingAccounts.value.url =  API_ENDPOINTS.userCheckingsAccounts;
    checkingAccounts.value.loading = false;

    savingAccounts.value.url =  API_ENDPOINTS.userSavingsAccounts
    savingAccounts.value.page =  0;
    savingAccounts.value.loading = false;

    pendingAccounts.value.url = API_ENDPOINTS.userPendingAccounts
    pendingAccounts.value.page = 0;
    pendingAccounts.value.loading = false;

    checkingAccounts.value.data =  await getAccount(API_ENDPOINTS.userCheckingsAccounts, checkingAccounts.value.page, pageSize.value);
    savingAccounts.value.data = await getAccount(API_ENDPOINTS.userSavingsAccounts, savingAccounts.value.page, pageSize.value);
    pendingAccounts.value.data = await getAccount(API_ENDPOINTS.userPendingAccounts, pendingAccounts.value.page, pageSize.value);
    
    console.log(checkingAccounts.value)
})

async function handlePageChange(newPage,account){
    account.loading = true
    account.page = newPage
    account.data = await getAccount(account.url,account.page,pageSize.value)
    account.loading = false 
}

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

</script>

<style scoped>
.account-list {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}
</style>
