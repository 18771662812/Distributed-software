<script setup lang="ts">
import { ArrowRight } from '@element-plus/icons-vue'

interface ProductItem {
  id: number | string
  name: string
  subtitle: string
  price: number
  sales: number
  image: string
}

defineProps<{
  product: ProductItem
}>()

const emit = defineEmits<{
  detail: [id: number | string]
}>()
</script>

<template>
  <article class="product-card" @click="emit('detail', product.id)">
    <div class="product-image">
      <img :src="product.image" :alt="product.name" loading="lazy" />
      <div class="image-overlay">
        <span class="view-tag">查看详情</span>
      </div>
    </div>
    
    <div class="product-info">
      <div class="header">
        <h3 class="product-name">{{ product.name }}</h3>
        <p class="product-subtitle">{{ product.subtitle }}</p>
      </div>
      
      <div class="footer">
        <div class="price-group">
          <span class="currency">¥</span>
          <span class="amount">{{ product.price.toLocaleString(undefined, { minimumFractionDigits: 2 }) }}</span>
        </div>
        <div class="sales-tag">
          已售 {{ product.sales }}+
        </div>
      </div>
    </div>
  </article>
</template>

<style scoped lang="scss">
.product-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  overflow: hidden;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid var(--slate-100);
  display: flex;
  flex-direction: column;
  position: relative;
  
  &:hover {
    transform: translateY(-8px);
    box-shadow: var(--shadow-xl);
    border-color: var(--color-primary-soft);
    
    .product-image img {
      transform: scale(1.1);
    }
    
    .image-overlay {
      opacity: 1;
    }
  }
}

.product-image {
  position: relative;
  aspect-ratio: 1 / 1;
  background: var(--slate-50);
  overflow: hidden;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  }
  
  .image-overlay {
    position: absolute;
    inset: 0;
    background: rgba(0, 47, 167, 0.1);
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: opacity 0.3s ease;
    backdrop-filter: blur(4px);
  }
  
  .view-tag {
    background: white;
    color: var(--color-primary);
    padding: 8px 16px;
    border-radius: var(--radius-md);
    font-size: 12px;
    font-weight: 600;
    box-shadow: var(--shadow-md);
  }
}

.product-info {
  padding: 24px;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.header {
  margin-bottom: 20px;
}

.product-name {
  font-size: 18px;
  color: var(--slate-900);
  margin-bottom: 8px;
  line-height: 1.3;
  font-weight: 600;
}

.product-subtitle {
  font-size: 14px;
  color: var(--text-muted);
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 42px;
}

.footer {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
}

.price-group {
  display: flex;
  align-items: baseline;
  gap: 2px;
  color: var(--color-primary);
  
  .currency {
    font-size: 14px;
    font-weight: 600;
  }
  
  .amount {
    font-size: 24px;
    font-weight: 700;
    letter-spacing: -0.02em;
  }
}

.sales-tag {
  font-size: 12px;
  color: var(--slate-400);
  background: var(--slate-50);
  padding: 4px 10px;
  border-radius: var(--radius-sm);
  font-weight: 500;
}
</style>
