<template>
    <div class="bg-blue-500 text-white rounded-lg p-6 w-64 relative">
        <!-- Header with icon and title -->
        <div class="flex items-center gap-2 mb-4">
            <div class="w-4 h-4 bg-white rounded-sm flex items-center justify-center">
                <div class="w-2 h-2 bg-blue-500 rounded-sm"></div>
            </div>
            <span class="text-sm font-medium">New Orders</span>
        </div>

        <!-- Main number -->
        <div class="text-4xl font-bold mb-6">
            {{ orderCount }}
        </div>

        <!-- More info link -->
        <div class="flex items-center gap-1 text-sm">
            <span>More info</span>
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <circle cx="12" cy="12" r="10"></circle>
                <path d="m6 9 6 6 6-6"></path>
            </svg>
        </div>
    </div>
</template>

<script>
export default {
    name: 'NewOrdersCard',
    props: {
        orderCount: {
            type: [Number, String],
            default: 150
        }
    }
}
</script>
