<template>
    <div class="pagination-wrapper" v-if="totalPages > 1">
        <nav aria-label="Page navigation">
            <div class="d-flex justify-content-center align-items-center gap-3">
                <!-- Previous Button -->
                <button class="btn btn-outline-primary d-flex align-items-center" @click="goToPreviousPage"
                    :disabled="isFirstPage" aria-label="Previous page">
                    <i class="bi bi-chevron-left me-1"></i>
                    Previous
                </button>

                <!-- Page Indicator -->
                <div class="page-indicator">
                    <span class="fw-bold text-primary fs-5">
                        {{ currentPage + 1 }}/{{ totalPages }}
                    </span>
                </div>

                <!-- Next Button -->
                <button class="btn btn-outline-primary d-flex align-items-center" @click="goToNextPage"
                    :disabled="isLastPage" aria-label="Next page">
                    Next
                    <i class="bi bi-chevron-right ms-1"></i>
                </button>
            </div>
        </nav>

        <!-- Page Info -->
        <div class="pagination-info mt-3 text-center">
            <small class="text-muted">
                Showing {{ startItem }} to {{ endItem }} of {{ totalElements }} entries
            </small>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
    paginationData: {
        type: Object,
        required: true
    }
});

const emit = defineEmits(['page-changed']);

// Computed properties based on pagination data
const currentPage = computed(() => props.paginationData.number || 0);
const totalPages = computed(() => props.paginationData.totalPages || 1);
const totalElements = computed(() => props.paginationData.totalElements || 0);
const pageSize = computed(() => props.paginationData.size || 0);
const isFirstPage = computed(() => props.paginationData.first || currentPage.value === 0);
const isLastPage = computed(() => props.paginationData.last || currentPage.value === totalPages.value - 1);
const numberOfElements = computed(() => props.paginationData.numberOfElements || 0);

// Calculate item range display
const startItem = computed(() => {
    if (totalElements.value === 0) return 0;
    return currentPage.value * pageSize.value + 1;
});

const endItem = computed(() => {
    if (totalElements.value === 0) return 0;
    return currentPage.value * pageSize.value + numberOfElements.value;
});

// Methods
const goToPreviousPage = () => {
    if (!isFirstPage.value) {
        emit('page-changed', currentPage.value - 1);
    }
};

const goToNextPage = () => {
    if (!isLastPage.value) {
        emit('page-changed', currentPage.value + 1);
    }
};
</script>

<style scoped>
.pagination-wrapper {
    margin-top: 1.5rem;
    padding: 1rem 0;
}

.page-indicator {
    min-width: 80px;
    text-align: center;
}

.btn {
    border-radius: 0.375rem;
    padding: 0.5rem 1rem;
    font-weight: 500;
    transition: all 0.15s ease-in-out;
    min-width: 100px;
}

.btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.btn-outline-primary {
    border-color: #0d6efd;
    color: #0d6efd;
}

.btn-outline-primary:hover:not(:disabled) {
    background-color: #0d6efd;
    border-color: #0d6efd;
    color: #fff;
}

.btn-outline-primary:disabled {
    color: #6c757d;
    border-color: #dee2e6;
    background-color: transparent;
}

.pagination-info {
    font-size: 0.875rem;
}

.bi {
    font-size: 0.875rem;
}

/* Responsive adjustments */
@media (max-width: 576px) {
    .btn {
        padding: 0.375rem 0.75rem;
        font-size: 0.875rem;
        min-width: 80px;
    }

    .page-indicator {
        min-width: 60px;
    }

    .page-indicator .fs-5 {
        font-size: 1rem !important;
    }

    .pagination-info {
        font-size: 0.8rem;
    }

    .d-flex.gap-3 {
        gap: 1rem !important;
    }
}

@media (max-width: 480px) {
    .btn {
        padding: 0.25rem 0.5rem;
        font-size: 0.8rem;
        min-width: 70px;
    }

    /* Hide text on very small screens, keep only icons */
    .btn .me-1,
    .btn .ms-1 {
        margin: 0 !important;
    }

    .btn-text {
        display: none;
    }
}
</style>
