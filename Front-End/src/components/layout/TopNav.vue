<template>
  <header class="top-nav">
    <div class="left">
      <h1>Products</h1>
    </div>
    <div class="right">
      <button class="nav-btn">Transfer</button>
      <router-link to="/atm">
        <button class="nav-btn">Deposit/Withdraw</button>
      </router-link>
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

.top-nav .nav-btn {
  background-color: #facc15;
  border: none;
  padding: 0.5rem 1rem;
  font-weight: bold;
  border-radius: 4px;
  cursor: pointer;
}
</style>
