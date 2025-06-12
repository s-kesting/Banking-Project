<template>
    <div class="container mt-4">
        <h2>Request a new banking account</h2>

        <div class="mb-3">
            <label for="account_type" class="form-label">Account Type</label>
            <select v-model="form.account_type" class="form-select" id="account_type" required>
                <option disabled value="">Select type</option>
                <option value="Saving">Savings</option>
                <option value="Checking">Checking</option>
            </select>
        </div>

        <button :onclick="handleSubmit" class="btn btn-primary">Request Account</button>
        <div v-if="isVisable">{{ message }}</div>
    </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import apiClient from '../../utils/apiClient'
import API_ENDPOINTS from '../../config'

const form = reactive({
    account_type: '',
})
let message = ref("please select a account type")
const isVisable = ref(false)

const handleSubmit = async () => {
    // Normally, this would be submitted via API.
    console.log('Form submitted:', form)
    if (form.account_type !== "") {
        try {
            const response = await apiClient.post(API_ENDPOINTS.userAccounts, { accountType: form.account_type });
            message.value = response.data
        } catch (error) {
            const status = error.status
            if (status) {
                message.value = error.response.data.message
            } else {
                console.log(error.message)
            }
        } finally {
            isVisable.value = true
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
