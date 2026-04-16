<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { ArrowRight } from '@element-plus/icons-vue'

import CategoryPanel from '@/components/mall/CategoryPanel.vue'
import HomeBanner from '@/components/mall/HomeBanner.vue'
import ProductCard from '@/components/product/ProductCard.vue'

const categories = [
  { title: '数码潮品', desc: '新一代高效办公与娱乐设备。', icon: '📱' },
  { title: '品质家电', desc: '为您打造现代化的智能家居。', icon: '🏠' },
  { title: '家居美学', desc: '极简主义家具与装饰艺术。', icon: '🛋️' },
  { title: '美妆个护', desc: '呵护健康的精选个人护理。', icon: '🧴' },
]

const products = [
  {
    id: 1,
    name: 'ANC Pro 降噪耳机',
    subtitle: '主动降噪技术，38小时超长续航。',
    price: 899,
    sales: 2981,
    image: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=900&q=80',
  },
  {
    id: 2,
    name: '智能净化器 X7',
    subtitle: '覆盖45㎡，支持手机App远程操控。',
    price: 1599,
    sales: 1246,
    image: 'https://images.unsplash.com/photo-1585771724684-38269d6639fd?auto=format&fit=crop&w=900&q=80',
  },
  {
    id: 3,
    name: '人体工学椅 Pro',
    subtitle: '全方位腰椎支撑，开启高效办公。',
    price: 1299,
    sales: 863,
    image: 'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=900&q=80',
  },
  {
    id: 4,
    name: '便携手冲壶 Lite',
    subtitle: '精准控温萃取，唤醒每个清晨。',
    price: 499,
    sales: 2140,
    image: 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?auto=format&fit=crop&w=900&q=80',
  },
]

const viewDetail = (id: number | string) => {
  ElMessage.info('正在跳转至商品详情: ' + id)
}
</script>

<template>
  <div class="home-page">
    <div class="container">
      <HomeBanner />
      
      <section class="category-section">
        <div class="section-intro">
          <span class="eyebrow">探索分类</span>
          <h2 class="section-title">精选品类</h2>
        </div>
        <CategoryPanel :categories="categories" id="category" />
      </section>

      <section class="product-section">
        <div class="section-header">
          <div class="header-text">
            <span class="eyebrow">热门趋势</span>
            <h2 class="section-title">必选好物</h2>
            <p class="section-subtitle">为您甄选全球最受欢迎的尖货好物。</p>
          </div>
          <el-button link class="view-all-btn">
            查看全部 <el-icon class="el-icon--right"><ArrowRight /></el-icon>
          </el-button>
        </div>

        <div class="product-grid">
          <ProductCard
            v-for="(item, index) in products"
            :key="item.id"
            :product="item"
            @detail="viewDetail"
            class="reveal-item"
            :style="{ animationDelay: `${index * 0.1}s` }"
          />
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped lang="scss">
.home-page {
  padding-bottom: 100px;
}

.eyebrow {
  display: block;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.15em;
  color: var(--color-primary);
  margin-bottom: 8px;
  text-transform: uppercase;
}

.section-intro {
  margin: 100px 0 40px;
  text-align: center;
}

.product-section {
  margin-top: 120px;
}

.section-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 48px;
  
  .section-subtitle {
    margin-top: 12px;
    color: var(--text-muted);
    font-size: 16px;
  }
}

.view-all-btn {
  font-weight: 600;
  font-size: 14px;
  color: var(--slate-900);
  padding: 0;
  
  &:hover {
    color: var(--color-primary);
  }
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 32px;
}

.reveal-item {
  opacity: 0;
  animation: reveal 0.8s cubic-bezier(0.4, 0, 0.2, 1) forwards;
}

@keyframes reveal {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 1200px) {
  .product-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;
  }
}

@media (max-width: 900px) {
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
  }
}

@media (max-width: 600px) {
  .product-grid {
    grid-template-columns: 1fr;
  }
}
</style>
