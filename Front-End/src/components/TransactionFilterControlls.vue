<template>
  <div class="transaction-filter">
    <!-- Toggle Filter Panel -->
    <button class="toggle-btn" @click="showFilters = !showFilters">
      {{ showFilters ? 'Hide Filters ▲' : 'Show Filters ▼' }}
    </button>

    <!-- Filter Panel -->
    <transition name="fade">
      <div v-if="showFilters" class="filter-panel">
        <div class="filter-row">
          <input v-model="filters.filterIban" type="text" placeholder="Filter IBAN" />
        </div>

        <div class="filter-row">
          <input v-model="filters.startDate" type="datetime-local" />
          <input v-model="filters.endDate" type="datetime-local" />
        </div>

        <div class="filter-row">
          <input v-model.number="filters.minAmount" type="number" step="0.01" placeholder="Min Amount" />
          <input v-model.number="filters.maxAmount" type="number" step="0.01" placeholder="Max Amount" />
          <input v-model.number="filters.exactAmount" type="number" step="0.01" placeholder="Exact Amount" />
        </div>

        <div class="action-buttons">
          <button @click="applyFilters" class="btn primary">Apply</button>
          <button @click="clearFilters" class="btn">Clear</button>
        </div>

      </div>
    </transition>

  </div>
</template>

<script setup>
import { ref, watch} from 'vue';
import { defineEmits } from 'vue';

// Emits
const emit = defineEmits(['filtersApplied', 'filtersCleared']);

// Reactive filter state
const showFilters = ref(false);

const filters = ref({
  Iban: '',
  startDate: '',
  endDate: '',
  minAmount: null,
  maxAmount: null,
  exactAmount: null,
  filterIban: ''
});

// Computed query string (or update it manually if you prefer)
const queryString = ref('');

function generateQueryString() {
  const params = Object.entries(filters.value)
    .filter(([_, val]) => val !== null && val !== '')
    .map(([key, val]) => `${key}=${encodeURIComponent(val)}`);
  return params.length ? '&' + params.join('&') : '';
}

function applyFilters() {
  queryString.value = generateQueryString();
  emit('filtersApplied', queryString.value);
}

function clearFilters() {
  filters.value = {
    Iban: '',
    startDate: '',
    endDate: '',
    minAmount: null,
    maxAmount: null,
    exactAmount: null,
    filterIban: ''
  };
  queryString.value = '';
  emit('filtersCleared');
}
</script>


<style scoped>
.transaction-filter {
  padding: 1rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #fdfdfd;
  font-family: sans-serif;
  max-width: 800px;
  margin: auto;
}

.toggle-btn {
  background: none;
  border: none;
  font-weight: bold;
  font-size: 1rem;
  cursor: pointer;
  margin-bottom: 0.5rem;
  color: #007bff;
}

.filter-panel {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 6px;
  box-shadow: 0 0 0 1px #ccc;
}

.filter-row {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.filter-row input {
  flex: 1;
  min-width: 140px;
  padding: 6px 10px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
  margin-top: 0.5rem;
}

.btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  background: #6c757d;
  color: #fff;
  cursor: pointer;
}

.btn.primary {
  background: #007bff;
}

.query-preview {
  margin-top: 0.5rem;
  font-family: monospace;
  background: #e2e6ea;
  padding: 0.5rem;
  border-radius: 4px;
  word-break: break-word;
  font-size: 13px;
}

/* Animation */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>

