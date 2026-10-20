import { createRouter, createWebHistory } from 'vue-router'

/**
 * 管理后台路由
 *
 * 路由结构：
 *   /login          登录页（独立页，不挂载到 Layout）
 *   /               Layout 容器（侧边栏 + 顶部栏 + 内容区）
 *     ├─ /dashboard     仪表盘
 *     ├─ /products       商品列表
 *     ├─ /products/edit  新增商品
 *     ├─ /products/edit/:id  编辑商品
 *     ├─ /categories     分类管理
 *     ├─ /orders         订单管理
 *     └─ /users          用户管理
 */
const routes = [
  { path: '/', redirect: '/dashboard' },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('../layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/dashboard/index.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: 'products',
        name: 'ProductList',
        component: () => import('../views/product/list.vue'),
        meta: { title: '商品管理' }
      },
      {
        path: 'products/edit',
        name: 'ProductAdd',
        component: () => import('../views/product/edit.vue'),
        meta: { title: '新增商品' }
      },
      {
        path: 'products/detail/:id',
        name: 'ProductDetail',
        component: () => import('../views/product/detail.vue'),
        meta: { title: '商品详情', mode: 'view' }
      },
      {
        path: 'products/edit/:id',
        name: 'ProductEdit',
        component: () => import('../views/product/detail.vue'),
        meta: { title: '编辑商品', mode: 'edit' }
      },
      {
        path: 'auth-check',
        name: 'AuthCheck',
        component: () => import('../views/auth-check/index.vue'),
        meta: { title: '权限验证', requiresAuth: true }
      },
      {
        path: 'categories',
        name: 'Categories',
        component: () => import('../views/category/index.vue'),
        meta: { title: '分类管理' }
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('../views/order/index.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('../views/user/index.vue'),
        meta: { title: '用户管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

/**
 * 路由守卫：
 *   - 未登录访问业务页 → 跳 /login
 *   - 已登录访问 /login → 跳 /dashboard
 *   - 其它 → 放行
 */
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) return next('/login')
  if (to.path === '/login' && token) return next('/dashboard')
  next()
})

export default router
