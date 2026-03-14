<script setup lang="ts">
const highlights = ['新人礼券', '爆款直降', '品牌严选']
</script>

<template>
  <section class="banner">
    <div class="banner-copy">
      <span class="eyebrow">SPRING COLLECTION 2026</span>
      <h1>精选好物，一站式打造高品质生活方式</h1>
      <p>
        覆盖数码、家电、家居与个护品类，配合企业级订单、购物车和会员体系，适合作为电商商城首页的完整落地骨架。
      </p>
      <div class="banner-actions">
        <el-button type="primary" size="large">立即抢购</el-button>
        <el-button size="large" plain>查看活动</el-button>
      </div>
      <ul class="banner-tags">
        <li v-for="item in highlights" :key="item">{{ item }}</li>
      </ul>
    </div>

    <div class="banner-card">
      <div class="stat-card">
        <span>今日热卖</span>
        <strong>13,824</strong>
        <small>实时成交订单</small>
      </div>
      <div class="stat-card accent">
        <span>推荐品类</span>
        <strong>智能家电</strong>
        <small>限时满减进行中</small>
      </div>
    </div>
  </section>
</template>

<style scoped lang="scss">
.banner {
  display: grid;
  grid-template-columns: 1.4fr 0.9fr;
  gap: 24px;
  padding: 42px;
  border-radius: 32px;
  color: #fff;
  background:
    radial-gradient(circle at top left, rgba(255, 255, 255, 0.2), transparent 24%),
    linear-gradient(135deg, #122b7a, #2043b4 46%, #d94f2b);
  box-shadow: 0 24px 60px rgba(18, 43, 122, 0.25);
}

.eyebrow {
  display: inline-flex;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.16);
  font-size: 12px;
  letter-spacing: 0.12em;
}

.banner h1 {
  margin: 18px 0 16px;
  font-size: clamp(32px, 4vw, 46px);
  line-height: 1.16;
}

.banner p {
  max-width: 620px;
  margin: 0;
  color: rgba(255, 255, 255, 0.84);
  font-size: 15px;
}

.banner-actions {
  display: flex;
  gap: 12px;
  margin-top: 28px;
}

.banner-tags {
  display: flex;
  gap: 12px;
  padding: 0;
  margin: 28px 0 0;
  list-style: none;
}

.banner-tags li {
  padding: 8px 14px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.14);
}

.banner-card {
  display: grid;
  align-content: end;
  gap: 16px;
}

.stat-card {
  padding: 24px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.14);
}

.stat-card span,
.stat-card small {
  color: rgba(255, 255, 255, 0.8);
}

.stat-card strong {
  display: block;
  margin: 8px 0;
  font-size: 28px;
}

.accent {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.18), rgba(255, 255, 255, 0.08));
}

@media (max-width: 960px) {
  .banner {
    grid-template-columns: 1fr;
    padding: 24px;
  }

  .banner-tags,
  .banner-actions {
    flex-wrap: wrap;
  }
}
</style>
