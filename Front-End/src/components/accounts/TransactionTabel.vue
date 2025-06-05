<template>
    <div class="transaction-table-container">
        <h5 class="mb-3">Recent Transactions</h5>
        <div class="table-responsive">
            <table class="table table-hover">
                <thead class="table-light">
                    <tr>
                        <th scope="col">Date</th>
                        <th scope="col">Description</th>
                        <th scope="col">From/To</th>
                        <th scope="col">IBAN</th>
                        <th scope="col" class="text-end">Amount</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="transaction in transactions" :key="transaction.transactionId">
                        <td>
                            <small class="text-muted">{{ formatDate(transaction.createdAt) }}</small>
                        </td>
                        <td>
                            <div class="fw-medium">{{ transaction.description }}</div>
                        </td>
                        <td>
                            <div class="d-flex align-items-center">
                                <span class="small">{{ getOtherParty(transaction) }}</span>
                            </div>
                        </td>
                        <td>
                            <code class="small">{{ getOtherIban(transaction) }}</code>
                        </td>
                        <td class="text-end">
                            <span :class="getAmountClass(transaction)" class="fw-bold">
                                {{ getAmountDisplay(transaction) }}
                            </span>
                        </td>
                    </tr>
                    <tr v-if="transactions.length === 0">
                        <td colspan="5" class="text-center text-muted py-4">
                            <i class="bi bi-inbox me-2"></i>
                            No transactions found
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</template>

<script setup>

const props = defineProps({
    transactions: {
        type: Array,
        default: () => []
    },
    currentIban: {
        type: String,
        required: true
    }
});

const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-GB', {
        day: '2-digit',
        month: 'short',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
};

const isIncoming = (transaction) => {
    return transaction.receiverIban === props.currentIban;
};

const getOtherParty = (transaction) => {
    if (isIncoming(transaction)) {
        return transaction.senderUsername
    } else {
        return transaction.receiverUsername;
    }
};

const getOtherIban = (transaction) => {
    if (isIncoming(transaction)) {
        return transaction.senderIban;
    } else {
        return transaction.receiverIban
    }
};



const getAmountDisplay = (transaction) => {
    const sign = isIncoming(transaction) ? '+' : '-';
    return `${sign}€${transaction.amount.toFixed(2)}`;
};

const getAmountClass = (transaction) => {
    return isIncoming(transaction) ? 'text-success' : 'text-danger';
};
</script>

<style scoped>
.transaction-table-container {
    margin-top: 1.5rem;
}

.table {
    font-size: 0.9rem;
}

.table th {
    border-top: none;
    font-weight: 600;
    font-size: 0.85rem;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

.table td {
    vertical-align: middle;
    border-color: #e9ecef;
}

.table-hover tbody tr:hover {
    background-color: #f8f9fa;
}

code {
    background-color: #f1f3f4;
    padding: 0.25rem 0.5rem;
    border-radius: 4px;
    font-size: 0.8rem;
}

.bi {
    font-size: 1.1rem;
}

@media (max-width: 768px) {
    .table {
        font-size: 0.8rem;
    }

    .table th,
    .table td {
        padding: 0.5rem 0.25rem;
    }

    code {
        font-size: 0.7rem;
    }
}
</style>
