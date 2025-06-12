<template>
  <div class="transaction card">
    <div class="card-body p-3">
      <div class="row align-items-center">
        <!-- Transaction Direction Icon -->
        <div class="col-1">
          <div class="transaction-icon">
            <i :class="ammount >= 0 ? 'bi bi-arrow-down-circle text-success' : 'bi bi-arrow-up-circle text-danger'"></i>
          </div>
        </div>
        
        <!-- Transaction Details -->
        <div class="col-8">
          <div class="transaction-main">
            <div class="description fw-semibold">{{ description }}</div>
            <div class="accounts text-muted small">
              <span v-if="ammount >= 0">From: {{ senderAccount }}</span>
              <span v-else>To: {{ recieverAccount }}</span>
            </div>
          </div>
        </div>
        
        <!-- Amount and Time -->
        <div class="col-3 text-end">
          <div class="amount" :class="ammount >= 0 ? 'text-success' : 'text-danger'">
            {{ formatAmount(ammount) }}
          </div>
          <div class="timestamp text-muted small">{{ formatTime(timeStamp) }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  senderAccount: String,
  recieverAccount: String,
  ammount: Number,
  description: String,
  timeStamp: String,
})

const formatAmount = (amount) => {
  const sign = amount >= 0 ? '+' : '';
  return `${sign}€${Math.abs(amount).toFixed(2)}`;
}

const formatTime = (timestamp) => {
  // Simple time formatting - you can enhance this based on your needs
  if (timestamp.includes('-')) {
    const date = new Date(timestamp);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});
  }
  return timestamp;
}
</script>

<style scoped>
.transaction {
  border: 1px solid #e9ecef;
  border-radius: 8px;
  transition: all 0.2s ease;
  background: #fff;
}

.transaction:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

.transaction-icon {
  font-size: 1.25rem;
  display: flex;
  justify-content: center;
  align-items: center;
}

.description {
  color: #212529;
  font-size: 0.95rem;
}

.accounts {
  font-size: 0.8rem;
  font-family: monospace;
  margin-top: 0.25rem;
}

.amount {
  font-weight: 600;
  font-size: 1rem;
}

.timestamp {
  font-size: 0.75rem;
  margin-top: 0.25rem;
}

/* Mobile responsive */
@media (max-width: 576px) {
  .col-1, .col-8, .col-3 {
    flex: none;
  }
  
  .col-1 {
    width: 15%;
  }
  
  .col-8 {
    width: 60%;
  }
  
  .col-3 {
    width: 25%;
  }
  
  .accounts {
    display: none; /* Hide account numbers on mobile to save space */
  }
}
</style>
