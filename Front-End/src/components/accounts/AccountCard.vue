<template>
    <div class="account-card-wrapper">
        <div class="account-card">
            <div class="details">
                <div class="account-name">{{ accountName }}</div>
                <div class="iban">{{ iban }}</div>
            </div>
            <div class="balance">€ {{ balance.toFixed(2) }}</div>
        </div>

        <div v-if="loadTransactions">
    <TransactionFilterControlls  @filtersApplied="handleFiltersApplied" @filtersCleared="handleFiltersCleared"/>
            <TransactionTable :transactions="transactions" :current-iban="iban" />
            <PaginationControlls :paginationData="pageableMetaData" :current-page="page"
                @page-changed="handlePageChange" />
        </div>

    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import apiClient from '../../utils/apiClient';
import TransactionFilterControlls from '../TransactionFilterControlls.vue';
import API_ENDPOINTS from '../../config';
import TransactionTable from '../accounts/TransactionTabel.vue';
import PaginationControlls from '../navigation/PaginationControlls.vue';
import { isBreakStatement } from 'typescript';

const props = defineProps({
    accountName: String,
    iban: String,
    balance: Number,
    loadTransactions: Boolean,
    accountStatus: String
});
let transactions = ref([])
let page = ref(0)
let pageableMetaData = ref([])
let filterBool= ref(false)
let filterQuery = ref("")

function handleFiltersCleared(){
    filterQuery.value = ''; 
        fetchTransactions(props.iban,page.value)
}
function handleFiltersApplied(queryString){
    if (queryString !== ""){
    filterQuery.value = queryString
        console.log(queryString)
        fetchTransactions(props.iban,page.value)
    }

}
async function fetchTransactions(iban, page) {
    try {
        const url = `${API_ENDPOINTS.transactions}/Iban?page=${page}&Iban=${iban}${filterQuery.value}`
        await apiClient.get(url)
            .then(response => {
                const { content, ...metaData } = response.data
                transactions.value = content
                pageableMetaData.value = metaData
            });
    }
    catch (err) {
        console.log(err)
    }
}

function handlePageChange(newPage) {
    page.value = newPage
    fetchTransactions(props.iban, page.value)
}

onMounted(() => {
    if (props.loadTransactions) {
        fetchTransactions(props.iban, page.value)
    }
})
</script>

<style scoped>
.account-card-wrapper {
    margin-bottom: 0rem;
}

.account-card {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: white;
    padding: 1rem 1.5rem;
    border: 1px solid #d1d5db;
    border-radius: 8px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
    margin-bottom: 1rem;
}

.details .account-name {
    font-weight: 600;
    font-size: 1rem;
}

.details .iban {
    font-size: 0.85rem;
    color: #6b7280;
}

.balance {
    font-weight: bold;
    color: #10b981;
    font-size: 1.1rem;
}
</style>
