<template>
    <div class="new-account-btn">
      <button @click="createNewAccount">
        + New current account
      </button>
    </div>
  </template>
  
  <script setup>
  function createNewAccount() {
    alert('Redirecting to account creation flow...')
  }
  </script>
  
  <style scoped>
  .new-account-btn {
    display: flex;
    justify-content: flex-start;
  }
  
  .new-account-btn button {
    background-color: #10b981;
    color: white;
    padding: 0.75rem 1.25rem;
    font-size: 1rem;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    transition: background-color 0.3s;
  }
  
  .new-account-btn button:hover {
    background-color: #059669;
  }
  </style>
  