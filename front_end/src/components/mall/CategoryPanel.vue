<script setup lang="ts">
import { Right } from '@element-plus/icons-vue'

interface CategoryItem {
  title: string
  desc: string
  icon: string
}

defineProps<{
  categories: CategoryItem[]
}>()
</script>

<template>
  <div class="category-grid">
    <div v-for="item in categories" :key="item.title" class="category-card hover-lift">
      <div class="icon-wrapper">
        <span class="category-icon">{{ item.icon }}</span>
      </div>
      <div class="content">
        <h3 class="category-title">{{ item.title }}</h3>
        <p class="category-desc">{{ item.desc }}</p>
      </div>
      <div class="arrow">
        <el-icon><Right /></el-icon>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.category-card {
  background: var(--bg-card);
  padding: 32px;
  border-radius: var(--radius-lg);
  border: 1px solid var(--slate-100);
  cursor: pointer;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  
  &:hover {
    border-color: var(--color-primary-soft);
    
    .icon-wrapper {
      transform: scale(1.1);
      background: var(--color-primary-soft);
    }
    
    .arrow {
      opacity: 1;
      transform: translateX(0);
    }
  }
}

.icon-wrapper {
  width: 64px;
  height: 64px;
  background: var(--slate-50);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
  transition: all 0.3s ease;
  
  .category-icon {
    font-size: 32px;
  }
}

.category-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 12px;
  color: var(--slate-900);
}

.category-desc {
  font-size: 14px;
  color: var(--text-muted);
  line-height: 1.5;
}

.arrow {
  position: absolute;
  right: 20px;
  bottom: 20px;
  color: var(--color-primary);
  opacity: 0;
  transform: translateX(-10px);
  transition: all 0.3s ease;
}

@media (max-width: 1024px) {
  .category-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .category-grid {
    grid-template-columns: 1fr;
  }
}
</style>
