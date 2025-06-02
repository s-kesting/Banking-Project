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
import { setInitialSenderIban } from '@/stores/contentPropsStore'
import UserTransaction from '@/components/transaction/UserTransaction.vue'


let loading = ref(false)
let error = ref(null)
let accounts = ref([])

// 3) Declare that this component will emit "active-item" and "navigate":
const emit = defineEmits([ 'active-item', 'navigate' ])

function onPay(iban) {
  // a) Save the clicked IBAN into our shared ref
  setInitialSenderIban(iban)

  // b) Tell MainLayout to highlight "Transfer" (sidebar event name: "active-item")
  emit('active-item', 'Transfer')

  // c) Tell MainLayout to swap its <component> over to UserTransaction.vue
  emit('navigate', UserTransaction)
}

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
