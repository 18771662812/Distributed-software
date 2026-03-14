import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

import { getToken } from '@/utils/auth'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/home',
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/auth/LoginView.vue'),
    meta: {
      title: '登录',
      public: true,
    },
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/auth/RegisterView.vue'),
    meta: {
      title: '注册',
      public: true,
    },
  },
  {
    path: '/',
    component: () => import('@/layouts/MallLayout.vue'),
    children: [
      {
        path: 'home',
        name: 'home',
        component: () => import('@/views/mall/HomeView.vue'),
        meta: { title: '商城首页' },
      },
      {
        path: 'cart',
        name: 'cart',
        component: () => import('@/views/mall/CartView.vue'),
        meta: { title: '购物车' },
      },
      {
        path: 'order',
        name: 'order',
        component: () => import('@/views/mall/OrderView.vue'),
        meta: { title: '我的订单' },
      },
      {
        path: 'profile',
        name: 'profile',
        component: () => import('@/views/mall/ProfileView.vue'),
        meta: { title: '个人中心' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior: () => ({ top: 0 }),
})

router.beforeEach((to, _from, next) => {
  document.title = String(to.meta.title || '电商系统') + ' - Mall Shop'

  const token = getToken()
  const isPublic = Boolean(to.meta.public)

  if (!isPublic && !token) {
    next({
      path: '/login',
      query: { redirect: to.fullPath },
    })
    return
  }

  if (isPublic && token && (to.path === '/login' || to.path === '/register')) {
    next('/home')
    return
  }

  next()
})

export default router
