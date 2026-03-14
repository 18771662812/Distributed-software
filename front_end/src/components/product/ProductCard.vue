<script setup lang="ts">
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
      <img :src="product.image" :alt="product.name" />
    </div>
    <div class="product-content">
      <h3>{{ product.name }}</h3>
      <p>{{ product.subtitle }}</p>
      <div class="product-footer">
        <div>
          <strong>¥{{ product.price.toFixed(2) }}</strong>
          <span>月销 {{ product.sales }}</span>
        </div>
        <el-button type="primary" plain>查看详情</el-button>
      </div>
    </div>
  </article>
</template>

<style scoped lang="scss">
.product-card {
  overflow: hidden;
  border-radius: 24px;
  background: #fff;
  box-shadow: 0 18px 44px rgba(15, 23, 42, 0.08);
  cursor: pointer;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
}

.product-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 26px 54px rgba(15, 23, 42, 0.12);
}

.product-image {
  aspect-ratio: 1 / 1;
  background: linear-gradient(135deg, #f7f8fa, #edf2ff);
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-content {
  padding: 20px;
}

.product-content h3 {
  margin: 0;
  font-size: 18px;
}

.product-content p {
  min-height: 42px;
  margin: 10px 0 18px;
  color: #6b7280;
  font-size: 14px;
}

.product-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.product-footer strong {
  display: block;
  color: #d94f2b;
  font-size: 24px;
}

.product-footer span {
  color: #9ca3af;
  font-size: 13px;
}

@media (max-width: 768px) {
  .product-footer {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
