<template>
  <MainLayout>
    <template #sidebar>
      <SidebarNavigation activeItem="Pay" />
    </template>

    <template #main>
      <h2>My current accounts</h2>
      <AccountList :accounts="accounts" />
    </template>
  </MainLayout>
</template>

<script setup>
import MainLayout from "@/components/layout/MainLayout.vue";
import SidebarNavigation from "@/components/navigation/SideBarNavigation.vue";
import AccountList from "@/components/accounts/AccountList.vue";

const accounts = [
  {
    accountName: "Student account",
    iban: "NL24 ABNA 0126 6816 43",
    balance: 67.52,
  },
];
</script>
