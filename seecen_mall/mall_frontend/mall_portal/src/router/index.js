import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../store/user'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/home/index.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/category',
    name: 'CategoryPage',
    component: () => import('../views/category/index.vue'),
    meta: { title: '全部商品' }
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: () => import('../views/product/detail.vue'),
    meta: { title: '商品详情' }
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('../views/cart/index.vue'),
    meta: { title: '购物车', requiresAuth: true }
  },
  {
    path: '/checkout',
    name: 'Checkout',
    component: () => import('../views/checkout/index.vue'),
    meta: { title: '确认订单', requiresAuth: true }
  },
  {
    path: '/orders',
    name: 'OrderList',
    component: () => import('../views/order/list.vue'),
    meta: { title: '我的订单', requiresAuth: true }
  },
  {
    path: '/orders/:id',
    name: 'OrderDetail',
    component: () => import('../views/order/detail.vue'),
    meta: { title: '订单详情', requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/register/index.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/ai-chat',
    name: 'AiChat',
    component: () => import('../views/ai-chat/index.vue'),
    meta: { title: 'AI智能客服' }
  },
  {
    path: '/auth-check',
    name: 'AuthCheck',
    component: () => import('../views/auth-check/index.vue'),
    meta: { title: '权限验证', requiresAuth: true }
  }
]

// base 必须带上部署子路径（GitHub Pages 为 /seecen-mall/），
// 否则首次访问 /seecen-mall/ 时路由匹配不到任何页面，内容区空白
const router = createRouter({ history: createWebHistory(import.meta.env.BASE_URL), routes })

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token

  // 已登录用户访问登录/注册页：跳转到权限登录页
  if ((to.path === '/login' || to.path === '/register') && token) {
    return next('/auth-check')
  }

  // 需要登录的页面：未登录跳转到登录页
  if (to.meta.requiresAuth && !token) {
    return next({ path: '/login', query: { redirect: to.fullPath } })
  }

  next()
})

export default router
