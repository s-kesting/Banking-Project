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
            <TransactionTable :transactions="transactions" :current-iban="iban" />
            <PaginationControlls :paginationData="pageableMetaData" :current-page="page"
                @page-changed="handlePageChange" />
        </div>

    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import apiClient from '../../utils/apiClient';
import API_ENDPOINTS from '../../config';
import TransactionTable from '../accounts/TransactionTabel.vue';
import PaginationControlls from '../navigation/PaginationControlls.vue';

const props = defineProps({
    accountName: String,
    iban: String,
    balance: Number,
    loadTransactions: Boolean,
});
let transactions = ref([])
let page = ref(0)
let pageableMetaData = ref([])


async function fetchTransactions(iban, page) {
    try {
        await apiClient.get(`${API_ENDPOINTS.transactions}/Iban?page=${page}&Iban=${iban}`).then(response => {
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
    console.log(page.value)
    console.log(newPage)
    fetchTransactions(props.iban, page.value)
}

onMounted(() => {
    fetchTransactions(props.iban, page.value)
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
