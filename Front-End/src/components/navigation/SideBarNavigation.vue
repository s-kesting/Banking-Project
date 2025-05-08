<template>
  <nav class="sidebar-nav">
    <ul>
      <li
        v-for="item in items"
        :key="item"
        :class="{ active: item === activeItem }"
        @click="handleClick(item)"
      >
        {{ item }}
      </li>
    </ul>
  </nav>
</template>

<script setup>
const props = defineProps({
  activeItem: String,
});

const emit = defineEmits(["navigate"]);

const items = ["Overview", "Pay", "Save"];

function handleClick(item) {
  emit("navigate", item);
}
</script>

<style scoped>
.sidebar-nav ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.sidebar-nav li {
  padding: 0.75rem 1rem;
  cursor: pointer;
  border-radius: 6px;
  transition: background-color 0.2s;
}

.sidebar-nav li:hover {
  background-color: #d1fae5;
}

.sidebar-nav li.active {
  background-color: #10b981;
  color: white;
  font-weight: bold;
}
</style>
