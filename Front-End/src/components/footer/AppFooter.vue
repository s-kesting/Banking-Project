<template>
  <div class="footer-content">
    <span>© 2025 ABN AMRO</span>
    <span class="divider">|</span>
    <a href="#">Privacy</a>
    <span class="divider">|</span>
    <a href="#">Disclaimer</a>
    <span class="divider">|</span>
    <a href="#">Language</a>
  </div>
</template>

<style scoped>
.footer-content {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 0.5rem;
  font-size: 0.85rem;
  color: #6b7280;
}

.footer-content a {
  color: #007a5e;
  text-decoration: none;
}

.footer-content a:hover {
  text-decoration: underline;
}

.divider {
  color: #9ca3af;
}
</style>
