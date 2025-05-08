<template>
  <div class="main-layout">
    <TopNav />

    <div class="layout-body">
      <aside class="sidebar">
        <slot name="sidebar" />
      </aside>

      <section class="content">
        <slot name="main" />
      </section>
    </div>

    <footer class="footer">
      <AppFooter />
    </footer>
  </div>
</template>

<script setup>
import TopNav from "@/components/layout/TopNav.vue";
import AppFooter from "@/components/footer/AppFooter.vue";
</script>

<style scoped>
.main-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f9fafb;
}

.layout-body {
  display: grid;
  grid-template-columns: 220px 1fr;
  flex: 1;
}

.sidebar {
  background-color: #e5e7eb;
  padding: 1.5rem;
  border-right: 1px solid #d1d5db;
}

.content {
  padding: 2rem;
}

.footer {
  background-color: #f3f4f6;
  padding: 1rem;
  text-align: center;
  border-top: 1px solid #d1d5db;
}
</style>
