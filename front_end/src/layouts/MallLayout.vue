<script setup lang="ts">
import AppHeader from '@/components/layout/AppHeader.vue'
</script>

<template>
  <div class="mall-layout">
    <AppHeader />
    <main class="mall-main">
      <router-view />
    </main>
  </div>
</template>

<style scoped lang="scss">
.mall-layout {
  min-height: 100vh;
}

.mall-main {
  padding-bottom: 48px;
}
</style>
