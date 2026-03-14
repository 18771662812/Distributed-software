<script setup lang="ts">
import { Search, ShoppingCart, User, GoodsFilled } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'

import { useUserStore } from '@/stores/modules/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const navItems = [
  { label: '首页', path: '/home' },
  { label: '商品分类', path: '/home#category' },
  { label: '购物车', path: '/cart' },
  { label: '我的订单', path: '/order' },
  { label: '个人中心', path: '/profile' },
]

const goPage = (path: string) => {
  if (path.startsWith('/home#')) {
    router.push('/home').then(() => {
      const id = path.split('#')[1]
      if (id) {
        document.getElementById(id)?.scrollIntoView({ behavior: 'smooth', block: 'start' })
      }
    })
    return
  }
  router.push(path)
}

const handleLogout = async () => {
  await ElMessageBox.confirm('退出后需要重新登录，是否继续？', '退出登录', {
    type: 'warning',
  })

  userStore.logout()
  router.replace('/login')
}
</script>

<template>
  <header class="app-header">
    <div class="page-container header-content">
      <div class="brand" @click="router.push('/home')">
        <div class="brand-mark">M</div>
        <div>
          <div class="brand-name">Mall Shop</div>
          <div class="brand-slogan">品质电商 · 企业级前端方案</div>
        </div>
      </div>

      <nav class="nav-list">
        <button
          v-for="item in navItems"
          :key="item.path"
          class="nav-item"
          :class="{ active: route.path === item.path }"
          @click="goPage(item.path)"
        >
          {{ item.label }}
        </button>
      </nav>

      <div class="header-actions">
        <el-input placeholder="搜索耳机、家电、数码配件" class="search-box">
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>

        <el-button circle :icon="ShoppingCart" @click="router.push('/cart')" />
        <el-button circle :icon="GoodsFilled" @click="router.push('/order')" />

        <el-dropdown>
          <div class="user-entry">
            <el-avatar :icon="User" />
            <div class="user-meta">
              <strong>{{ userStore.userInfo?.nickname || userStore.userInfo?.username || '用户' }}</strong>
              <span>欢迎回来</span>
            </div>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="router.push('/profile')">个人中心</el-dropdown-item>
              <el-dropdown-item @click="router.push('/order')">我的订单</el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </header>
</template>

<style scoped lang="scss">
.app-header {
  position: sticky;
  top: 0;
  z-index: 20;
  backdrop-filter: blur(12px);
  background: rgba(255, 255, 255, 0.88);
  border-bottom: 1px solid rgba(229, 231, 235, 0.9);
}

.header-content {
  display: grid;
  grid-template-columns: 220px 1fr 420px;
  align-items: center;
  gap: 20px;
  min-height: 76px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.brand-mark {
  display: grid;
  place-items: center;
  width: 42px;
  height: 42px;
  border-radius: 14px;
  color: #fff;
  font-size: 22px;
  font-weight: 800;
  background: linear-gradient(135deg, #1f4ed8, #d94f2b);
  box-shadow: 0 14px 28px rgba(217, 79, 43, 0.22);
}

.brand-name {
  font-size: 18px;
  font-weight: 700;
}

.brand-slogan,
.user-meta span {
  color: #6b7280;
  font-size: 12px;
}

.nav-list {
  display: flex;
  justify-content: center;
  gap: 10px;
}

.nav-item {
  padding: 10px 16px;
  border: 0;
  border-radius: 999px;
  background: transparent;
  color: #374151;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.nav-item.active,
.nav-item:hover {
  color: #d94f2b;
  background: rgba(217, 79, 43, 0.1);
}

.header-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
}

.search-box {
  width: 240px;
}

.user-entry {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.user-meta {
  display: flex;
  flex-direction: column;
}

@media (max-width: 1024px) {
  .header-content {
    grid-template-columns: 1fr;
    padding: 12px 0;
  }

  .nav-list {
    justify-content: flex-start;
    overflow-x: auto;
    padding-bottom: 4px;
  }

  .header-actions {
    justify-content: flex-start;
    flex-wrap: wrap;
  }

  .search-box {
    width: 100%;
  }
}
</style>
