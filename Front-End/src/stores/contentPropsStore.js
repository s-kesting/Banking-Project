// src/stores/contentPropsStore.js
import { ref } from 'vue';

// Holds the currently selected sender IBAN:
export const initialSenderIban = ref("");

// Setter to update it:
export function setInitialSenderIban(iban) {
  initialSenderIban.value = iban;
}