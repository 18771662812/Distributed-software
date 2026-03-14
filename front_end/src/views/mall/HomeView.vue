<script setup lang="ts">
import { ElMessage } from 'element-plus'

import CategoryPanel from '@/components/mall/CategoryPanel.vue'
import HomeBanner from '@/components/mall/HomeBanner.vue'
import ProductCard from '@/components/product/ProductCard.vue'

const categories = [
  { title: '数码潮品', desc: '手机、耳机、智能穿戴与办公设备。', icon: '📱' },
  { title: '品质家电', desc: '厨房、清洁、生活电器整合专区。', icon: '🏠' },
  { title: '家居日用', desc: '收纳、床品、装饰与功能家居。', icon: '🛋️' },
  { title: '美妆个护', desc: '护肤、个护与健康产品推荐。', icon: '🧴' },
]

const products = [
  {
    id: 1,
    name: 'ANC Pro 无线降噪耳机',
    subtitle: '40dB 主动降噪，双设备连接，续航 38 小时',
    price: 899,
    sales: 2981,
    image: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=900&q=80',
  },
  {
    id: 2,
    name: '智能空气净化器 X7',
    subtitle: '适用 45㎡，支持 APP 远程控制与滤芯提醒',
    price: 1599,
    sales: 1246,
    image: 'https://images.unsplash.com/photo-1585771724684-38269d6639fd?auto=format&fit=crop&w=900&q=80',
  },
  {
    id: 3,
    name: '人体工学办公椅',
    subtitle: '三区承托，久坐不累，适合居家办公场景',
    price: 1299,
    sales: 863,
    image: 'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=900&q=80',
  },
  {
    id: 4,
    name: '便携咖啡机 Lite',
    subtitle: '小体积高压萃取，支持胶囊与咖啡粉双模式',
    price: 499,
    sales: 2140,
    image: 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?auto=format&fit=crop&w=900&q=80',
  },
]

const viewDetail = (id: number | string) => {
  ElMessage.info('商品详情页路由已预留，可跳转到 /product/' + id)
}
</script>

<template>
  <div class="home-page page-container">
    <HomeBanner />

    <CategoryPanel :categories="categories" />

    <section class="product-section">
      <div class="section-header">
        <div>
          <h2 class="section-title">热门推荐</h2>
          <p class="section-subtitle">商品卡片组件已抽离，后续可直接接入真实商品列表接口。</p>
        </div>
        <el-button plain>查看更多</el-button>
      </div>

      <div class="product-grid">
        <ProductCard
          v-for="item in products"
          :key="item.id"
          :product="item"
          @detail="viewDetail"
        />
      </div>
    </section>
  </div>
</template>

<style scoped lang="scss">
.home-page {
  padding-top: 28px;
}

.section-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
}

.product-section {
  margin-top: 40px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-top: 22px;
}

@media (max-width: 1024px) {
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .section-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .product-grid {
    grid-template-columns: 1fr;
  }
}
</style>
