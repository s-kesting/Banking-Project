<template>
  <header class="top-nav">
    <div class="left">
      <h1>Products</h1>
    </div>
    <div class="right">
      <button class="transfer-btn">Transfer</button>
    </div>
  </header>
</template>

<style scoped>
.top-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #007a5e;
  color: white;
  padding: 1rem 2rem;
}

.top-nav .transfer-btn {
  background-color: #facc15;
  border: none;
  padding: 0.5rem 1rem;
  font-weight: bold;
  border-radius: 4px;
  cursor: pointer;
}
</style>
