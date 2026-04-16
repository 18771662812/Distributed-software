<script setup lang="ts">
const highlights = ['全场包邮', '两年质保', '正品保障']
</script>

<template>
  <section class="banner-hero">
    <div class="banner-content">
      <div class="badge">2026 夏季限定系列</div>
      <h1 class="hero-title">
        重定义 <br />
        <span class="text-gradient">现代生活方式</span>
      </h1>
      <p class="hero-desc">
        为您精选全球高端电子产品与生活美学好物，让极简主义融入日常，打造独一无二的品质空间。
      </p>
      
      <div class="cta-group">
        <el-button type="primary" class="btn-primary">立即选购</el-button>
        <el-button class="btn-secondary">探索更多</el-button>
      </div>

      <div class="highlights">
        <div v-for="item in highlights" :key="item" class="highlight-item">
          <div class="dot"></div>
          {{ item }}
        </div>
      </div>
    </div>

    <div class="banner-visual">
      <div class="visual-card glass-panel hover-lift">
        <div class="card-header">
          <span class="label">今日爆款</span>
          <div class="live-dot"></div>
        </div>
        <div class="card-body">
          <h3 class="item-name">ANC Pro 降噪耳机</h3>
          <p class="item-price">起售价 ¥899</p>
        </div>
      </div>
      <div class="visual-deco"></div>
    </div>
  </section>
</template>

<style scoped lang="scss">
.banner-hero {
  display: grid;
  grid-template-columns: 1.2fr 0.8fr;
  align-items: center;
  gap: 60px;
  padding: 80px 0;
  min-height: 600px;
}

.banner-content {
  .badge {
    display: inline-block;
    padding: 6px 16px;
    background: var(--color-primary-soft);
    color: var(--color-primary);
    border-radius: var(--radius-sm);
    font-size: 12px;
    font-weight: 700;
    letter-spacing: 0.05em;
    margin-bottom: 24px;
  }

  .hero-title {
    font-size: 72px;
    font-weight: 800;
    line-height: 1.2;
    margin-bottom: 24px;
    color: var(--slate-900);
    letter-spacing: -0.02em;
    
    .text-gradient {
      color: var(--color-primary);
      background: linear-gradient(135deg, var(--color-primary), #4f46e5);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }
  }

  .hero-desc {
    font-size: 18px;
    color: var(--slate-500);
    max-width: 500px;
    margin-bottom: 40px;
    line-height: 1.6;
  }
}

.cta-group {
  display: flex;
  gap: 16px;
  margin-bottom: 60px;

  .btn-primary {
    height: 56px;
    padding: 0 32px;
    font-size: 16px;
    font-weight: 600;
    box-shadow: 0 10px 20px -5px rgba(0, 47, 167, 0.3);
  }

  .btn-secondary {
    height: 56px;
    padding: 0 32px;
    font-size: 16px;
    font-weight: 600;
    border-radius: var(--radius-md);
    border: 1px solid var(--slate-200);
    background: transparent;
    
    &:hover {
      background: var(--slate-50);
      border-color: var(--slate-300);
    }
  }
}

.highlights {
  display: flex;
  gap: 32px;
  
  .highlight-item {
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 14px;
    font-weight: 600;
    color: var(--slate-400);
    
    .dot {
      width: 6px;
      height: 6px;
      background: var(--color-primary);
      border-radius: 50%;
    }
  }
}

.banner-visual {
  position: relative;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;

  .visual-card {
    width: 280px;
    padding: 24px;
    border-radius: var(--radius-lg);
    z-index: 2;
    position: relative;
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 40px;
      
      .label {
        font-size: 12px;
        font-weight: 800;
        color: var(--slate-400);
        letter-spacing: 0.05em;
      }
      
      .live-dot {
        width: 8px;
        height: 8px;
        background: #10b981;
        border-radius: 50%;
        box-shadow: 0 0 0 4px rgba(16, 185, 129, 0.2);
        animation: pulse 2s infinite;
      }
    }
    
    .item-name {
      font-size: 20px;
      margin-bottom: 8px;
      font-weight: 600;
    }
    
    .item-price {
      font-size: 14px;
      color: var(--color-primary);
      font-weight: 600;
    }
  }

  .visual-deco {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 400px;
    height: 400px;
    background: radial-gradient(circle, var(--color-primary-soft) 0%, transparent 70%);
    z-index: 1;
    border-radius: 50%;
  }
}

@keyframes pulse {
  0% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.4); }
  70% { transform: scale(1); box-shadow: 0 0 0 10px rgba(16, 185, 129, 0); }
  100% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(16, 185, 129, 0); }
}

@media (max-width: 1024px) {
  .banner-hero {
    grid-template-columns: 1fr;
    text-align: center;
    padding: 40px 0;
    
    .hero-desc {
      margin: 0 auto 40px;
    }
    
    .cta-group, .highlights {
      justify-content: center;
    }
    
    .banner-visual {
      display: none;
    }
  }
  
  .banner-content .hero-title {
    font-size: 48px;
  }
}
</style>
