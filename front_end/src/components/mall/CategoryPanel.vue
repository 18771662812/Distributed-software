<script setup lang="ts">
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
  <section id="category" class="category-panel">
    <div class="section-header">
      <div>
        <h2 class="section-title">商品分类</h2>
        <p class="section-subtitle">按主营业务板块组织商品，便于后续接入真实分类接口与搜索筛选。</p>
      </div>
    </div>

    <div class="category-grid">
      <article v-for="item in categories" :key="item.title" class="category-item">
        <div class="icon-box">{{ item.icon }}</div>
        <h3>{{ item.title }}</h3>
        <p>{{ item.desc }}</p>
      </article>
    </div>
  </section>
</template>

<style scoped lang="scss">
.category-panel {
  margin-top: 34px;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 18px;
  margin-top: 20px;
}

.category-item {
  padding: 24px;
  border-radius: 22px;
  background: #fff;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.06);
}

.icon-box {
  display: grid;
  place-items: center;
  width: 56px;
  height: 56px;
  border-radius: 18px;
  background: linear-gradient(135deg, rgba(31, 78, 216, 0.12), rgba(217, 79, 43, 0.14));
  font-size: 28px;
}

.category-item h3 {
  margin: 18px 0 10px;
  font-size: 18px;
}

.category-item p {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

@media (max-width: 960px) {
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
