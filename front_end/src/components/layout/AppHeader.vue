<script setup lang="ts">
import { Search, ShoppingCart, Goods } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/modules/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const navItems = [
  { label: '首页', path: '/home' },
  { label: '商品分类', path: '/home#category' },
  { label: '精选专题', path: '/about' },
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
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
  userStore.logout()
  router.replace('/login')
}
</script>

<template>
  <header class="app-header glass-panel">
    <div class="container header-content">
      <div class="brand" @click="router.push('/home')">
        <div class="brand-logo">
          <div class="logo-inner"></div>
        </div>
        <div class="brand-text">
          <h1 class="brand-name">极简商城</h1>
          <span class="brand-tagline">极致品质体验</span>
        </div>
      </div>

      <nav class="nav-menu">
        <a
          v-for="item in navItems"
          :key="item.path"
          class="nav-link"
          :class="{ active: route.path === item.path }"
          @click.prevent="goPage(item.path)"
          href="#"
        >
          {{ item.label }}
        </a>
      </nav>

      <div class="header-right">
        <div class="search-wrapper">
          <el-input
            placeholder="寻找灵感..."
            class="header-search"
            clearable
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>

        <div class="action-icons">
          <el-tooltip content="购物车" placement="bottom">
            <button class="icon-btn" @click="router.push('/cart')">
              <el-icon size="20"><ShoppingCart /></el-icon>
            </button>
          </el-tooltip>
          
          <el-tooltip content="我的订单" placement="bottom">
            <button class="icon-btn" @click="router.push('/order')">
              <el-icon size="20"><Goods /></el-icon>
            </button>
          </el-tooltip>
        </div>

        <el-dropdown trigger="click">
          <div class="user-profile">
            <el-avatar 
              :size="36" 
              class="user-avatar"
              src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" 
            />
          </div>
          <template #dropdown>
            <el-dropdown-menu class="user-dropdown">
              <div class="dropdown-header">
                <strong>{{ userStore.userInfo?.nickname || userStore.userInfo?.username || '访客' }}</strong>
                <span>{{ userStore.userInfo ? '欢迎回来' : '请先登录' }}</span>
              </div>
              <el-dropdown-item divided @click="router.push('/profile')">个人资料</el-dropdown-item>
              <el-dropdown-item @click="router.push('/order')">我的订单</el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout" class="logout-item">退出登录</el-dropdown-item>
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
  z-index: 100;
  height: 80px;
  display: flex;
  align-items: center;
  border-bottom: 1px solid var(--glass-border);
  transition: all 0.3s ease;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  
  .brand-logo {
    width: 40px;
    height: 40px;
    background: var(--color-primary);
    border-radius: var(--radius-md);
    display: flex;
    align-items: center;
    justify-content: center;
    transform: rotate(-10deg);
    transition: transform 0.3s ease;
    
    .logo-inner {
      width: 16px;
      height: 16px;
      border: 3px solid white;
      border-radius: 4px;
    }
  }
  
  &:hover .brand-logo {
    transform: rotate(0deg) scale(1.05);
  }

  .brand-text {
    display: flex;
    flex-direction: column;
    
    .brand-name {
      font-size: 20px;
      font-weight: 800;
      letter-spacing: 0.05em;
      line-height: 1;
      margin: 0;
    }
    
    .brand-tagline {
      font-size: 10px;
      color: var(--text-muted);
      text-transform: uppercase;
      letter-spacing: 0.05em;
      margin-top: 4px;
    }
  }
}

.nav-menu {
  display: flex;
  gap: 32px;
  
  .nav-link {
    font-size: 15px;
    font-weight: 500;
    color: var(--slate-600);
    position: relative;
    padding: 8px 0;
    
    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 0;
      width: 0;
      height: 2px;
      background: var(--color-primary);
      transition: width 0.3s ease;
    }
    
    &:hover {
      color: var(--color-primary);
      &::after {
        width: 100%;
      }
    }
    
    &.active {
      color: var(--color-primary);
      &::after {
        width: 100%;
      }
    }
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.search-wrapper {
  :deep(.header-search) {
    .el-input__wrapper {
      background-color: var(--slate-100);
      border: none !important;
      box-shadow: none !important;
      border-radius: var(--radius-md);
      padding: 8px 16px;
      transition: all 0.3s ease;
      width: 180px;
      
      &:hover, &.is-focus {
        background-color: var(--slate-200);
        width: 240px;
      }
    }
  }
}

.action-icons {
  display: flex;
  gap: 8px;
}

.icon-btn {
  background: transparent;
  border: none;
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: var(--slate-600);
  transition: all 0.2s ease;
  
  &:hover {
    background: var(--slate-100);
    color: var(--color-primary);
  }
}

.user-profile {
  cursor: pointer;
  padding: 2px;
  border: 2px solid transparent;
  border-radius: 50%;
  transition: all 0.3s ease;
  
  &:hover {
    border-color: var(--color-primary-soft);
    transform: scale(1.05);
  }
}

.user-avatar {
  background: var(--slate-200);
}

// Dropdown Styling
:deep(.user-dropdown) {
  padding: 12px;
  border-radius: var(--radius-lg);
  border: 1px solid var(--slate-200);
  box-shadow: var(--shadow-xl);
  
  .dropdown-header {
    padding: 8px 16px 16px;
    display: flex;
    flex-direction: column;
    
    strong {
      font-size: 16px;
      color: var(--slate-900);
    }
    
    span {
      font-size: 12px;
      color: var(--text-muted);
    }
  }
  
  .el-dropdown-menu__item {
    border-radius: var(--radius-md);
    padding: 10px 16px;
    margin: 2px 0;
    
    &:hover {
      background-color: var(--slate-50);
      color: var(--color-primary);
    }
    
    &.logout-item {
      color: var(--el-color-danger);
      &:hover {
        background-color: var(--el-color-danger-light-9);
      }
    }
  }
}

@media (max-width: 1024px) {
  .nav-menu, .search-wrapper {
    display: none;
  }
}
</style>
