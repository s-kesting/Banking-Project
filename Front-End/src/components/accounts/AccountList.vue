<template>
    <div class="account-list">
        <AccountCard v-for="account in accounts" :key="account.iban" :account-name="account.accountType"
            :iban="account.iban" :balance="account.balance" />
        <NewAccountButton />
    </div>
</template>

<script setup>
import AccountCard from './AccountCard.vue'
import NewAccountButton from './NewAccountButton.vue'

const props = defineProps({
    accounts: Array
})
</script>

<style scoped>
.account-list {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}
</style>
