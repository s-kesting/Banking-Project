<!-- src/components/IbanSearch.vue -->
<template>
  <div class="iban-search">
    <label for="usernameSearch">Search user by username:</label>
    <input
      id="usernameSearch"
      v-model="query"
      type="text"
      placeholder="Start typing a username..."
      @focus="openDropdown = !!suggestions.length"
    />

    <ul v-if="openDropdown" class="suggestions">
      <li
        v-for="iban in suggestions"
        :key="iban"
        @mousedown.prevent="selectIban(iban)"
      >
        {{ iban }}
      </li>
      <li v-if="!loading && !suggestions.length" class="no-results">
        No matches
      </li>
    </ul>

    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
  </div>
</template>

<script setup>
import { ref, watch, onBeforeUnmount } from "vue";
import apiClient from "@/utils/apiClient";
import { API_ENDPOINTS } from "@/config";

// emit the picked IBAN
const emit = defineEmits(["select"]);

const query         = ref("");
const suggestions   = ref([]);
const loading       = ref(false);
const errorMessage  = ref("");
const openDropdown  = ref(false);

let debounceTimer;

// watch the query & debounce
watch(query, (val) => {
  clearTimeout(debounceTimer);
  suggestions.value = [];
  errorMessage.value = "";
  openDropdown.value = false;

  if (!val.trim()) return;

  debounceTimer = setTimeout(fetchIbans, 300);
});

onBeforeUnmount(() => clearTimeout(debounceTimer));

async function fetchIbans() {
  loading.value = true;
  try {
    const { data } = await apiClient.get(API_ENDPOINTS.searchIban, {
    params: { name: query.value.trim().toLowerCase() }
    });
    suggestions.value = data;
    openDropdown.value = true;
  } catch (e) {
    console.error(e);
    errorMessage.value = "Could not search IBANs.";
  } finally {
    loading.value = false;
  }
}

function selectIban(iban) {
  query.value       = iban;
  openDropdown.value = false;
  emit("select", iban);
}
</script>

<style scoped>
.iban-search {
  position: relative;
  margin-bottom: 1rem;
}
.suggestions {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #ccc;
  max-height: 150px;
  overflow-y: auto;
  list-style: none;
  margin: 0;
  padding: 0;
  z-index: 10;
}
.suggestions li {
  padding: 0.5rem;
  cursor: pointer;
}
.suggestions li:hover {
  background: #f5f5f5;
}
.no-results {
  padding: 0.5rem;
  color: #777;
}
.error {
  margin-top: 0.5rem;
  color: red;
}
</style>