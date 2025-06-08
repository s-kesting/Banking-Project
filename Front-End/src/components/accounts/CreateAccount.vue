<template>
    <div class="container mt-4">
        <h2>Request a new banking account</h2>
        <form @submit.prevent="handleSubmit()" class="needs-validation" novalidate>

            <div class="mb-3">
                <label for="account_type" class="form-label">Account Type</label>
                <select v-model="form.account_type" class="form-select" id="account_type" required>
                    <option disabled value="">Select type</option>
                    <option value="Saving">Savings</option>
                    <option value="Checking">Checking</option>
                </select>
            </div>

            <button class="btn btn-primary">Request Account</button>
        </form>
        <div v-if="isVisable">please select an account type</div>
    </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import apiClient from '../../utils/apiClient'
import API_ENDPOINTS from '../../config'

const form = reactive({
    account_type: '',
})

const isVisable = ref(false)

const handleSubmit = async () => {
    // Normally, this would be submitted via API.
    console.log('Form submitted:', form)
    if (form.account_type !== "") {
        try {
            console.log(form.account_type)
            await apiClient.post(API_ENDPOINTS.userNewAccount, form.account_type);
        } catch {
            //TODO: error handeling
        } finally {
        }
    }
    else { isVisable.value = true; }
}
</script>

<style scoped>
/* Optional styling */
.container {
    max-width: 600px;
}
</style>
