<script setup lang="ts">
import AppHeader from '@/components/layout/AppHeader.vue'
</script>

<template>
  <div class="mall-layout">
    <AppHeader />
    <main class="mall-main">
      <router-view v-slot="{ Component }">
        <transition name="fade-slide" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
    <footer class="mall-footer">
      <div class="container footer-content">
        <p>© 2026 极简品质商城. 版权所有.</p>
      </div>
    </footer>
  </div>
</template>

<style scoped lang="scss">
.mall-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.mall-main {
  flex: 1;
  padding: 40px 0 80px;
}

.mall-footer {
  padding: 40px 0;
  border-top: 1px solid var(--slate-200);
  color: var(--text-muted);
  font-size: 14px;
  text-align: center;
}

// Page Transition
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}
</style>
