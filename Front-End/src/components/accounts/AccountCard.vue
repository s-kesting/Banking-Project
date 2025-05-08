<template>
    <div class="account-card">
      <div class="details">
        <div class="account-name">{{ accountName }}</div>
        <div class="iban">{{ iban }}</div>
      </div>
      <div class="balance">€ {{ balance.toFixed(2) }}</div>
    </div>
  </template>
  
  <script setup>
  const props = defineProps({
    accountName: String,
    iban: String,
    balance: Number
  })
  </script>
  
  <style scoped>
  .account-card {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: white;
    padding: 1rem 1.5rem;
    border: 1px solid #d1d5db;
    border-radius: 8px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  }
  
  .details .account-name {
    font-weight: 600;
    font-size: 1rem;
  }
  
  .details .iban {
    font-size: 0.85rem;
    color: #6b7280;
  }
  
  .balance {
    font-weight: bold;
    color: #10b981;
    font-size: 1.1rem;
  }
  </style>
  