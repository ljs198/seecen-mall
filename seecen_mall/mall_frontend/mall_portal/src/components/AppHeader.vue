<template>
  <header class="app-header">
    <!-- 中秋装饰：月亮 + 灯笼 -->
    <div class="header-deco" aria-hidden="true">
      <svg class="moon-icon" viewBox="0 0 40 40" xmlns="http://www.w3.org/2000/svg">
        <defs>
          <radialGradient id="moonGrad" cx="35%" cy="35%">
            <stop offset="0%" stop-color="#ffffff"/>
            <stop offset="60%" stop-color="#fff8c4"/>
            <stop offset="100%" stop-color="#ffd700"/>
          </radialGradient>
        </defs>
        <circle cx="20" cy="20" r="14" fill="url(#moonGrad)" />
        <circle cx="14" cy="16" r="2" fill="#e6c200" opacity="0.5"/>
        <circle cx="24" cy="22" r="1.5" fill="#e6c200" opacity="0.5"/>
        <circle cx="18" cy="26" r="1" fill="#e6c200" opacity="0.4"/>
      </svg>
      <svg class="lantern-icon" viewBox="0 0 30 50" xmlns="http://www.w3.org/2000/svg">
        <line x1="15" y1="0" x2="15" y2="6" stroke="#c0392b" stroke-width="1"/>
        <ellipse cx="15" cy="22" rx="11" ry="14" fill="#c0392b"/>
        <rect x="9" y="8" width="12" height="3" fill="#ffd700"/>
        <rect x="9" y="34" width="12" height="3" fill="#ffd700"/>
        <text x="15" y="25" font-size="10" fill="#ffd700" text-anchor="middle" font-weight="bold">中</text>
        <line x1="15" y1="37" x2="15" y2="48" stroke="#ffd700" stroke-width="1"/>
        <line x1="12" y1="48" x2="18" y2="48" stroke="#ffd700" stroke-width="1"/>
      </svg>
    </div>

    <div class="header-content">
      <div class="logo" @click="goHome">
        <span class="logo-text">智能商城</span>
        <span class="logo-sub">·中秋礼遇</span>
      </div>

      <nav class="nav-links">
        <a class="nav-link" :class="{ active: isHome }" @click="goHome">首页</a>
        <a class="nav-link" :class="{ active: isCategory }" @click="goCategory">全部商品</a>
        <a
          v-if="userStore.isLogin"
          class="nav-link"
          :class="{ active: isOrders }"
          @click="goOrders"
        >我的订单</a>
      </nav>

      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索好物"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button :icon="SearchIcon" @click="handleSearch" />
          </template>
        </el-input>
      </div>

      <div class="user-actions">
        <el-badge
          :value="cartStore.cartCount"
          :hidden="cartStore.cartCount === 0"
          class="cart-badge"
        >
          <el-button text @click="goCart">购物车</el-button>
        </el-badge>

        <template v-if="userStore.isLogin">
          <span class="nickname">{{ userStore.userInfo?.nickname || userStore.userInfo?.username || '用户' }}</span>
          <el-button text type="danger" @click="handleLogout">退出</el-button>
        </template>
        <el-button v-else type="primary" size="small" @click="goLogin">登录</el-button>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { useCartStore } from '../store/cart'
import { useUserStore } from '../store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const SearchIcon = Search
const searchKeyword = ref(route.query.keyword || '')

const isHome = computed(() => route.path === '/')
const isCategory = computed(() => route.path === '/category')
const isOrders = computed(() => route.path.startsWith('/orders'))

function goHome() {
  router.push('/')
}

function goCategory() {
  router.push('/category')
}

function goOrders() {
  router.push('/orders')
}

function goCart() {
  router.push('/cart')
}

function goLogin() {
  router.push({ path: '/login', query: { redirect: route.fullPath } })
}

/**
 * 头部搜索：跳到分类页并把关键字带过去
 */
function handleSearch() {
  const keyword = searchKeyword.value.trim()
  if (keyword) {
    router.push({ path: '/category', query: { keyword } })
  } else {
    router.push('/category')
  }
}

async function handleLogout() {
  await userStore.logout()
  cartStore.cartList = []
  cartStore.cartCount = 0
  localStorage.setItem('cartCount', '0')
  ElMessage.success('已退出')
  router.push('/')
}

onMounted(() => {
  if (userStore.isLogin) {
    cartStore.refreshCartCount()
  }
})
</script>

<style scoped>
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  height: 64px;
  background: linear-gradient(135deg, #0f1444 0%, #1a1b3a 50%, #2c1810 100%);
  border-bottom: 1px solid #c8901b;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
}

/* 中秋装饰：左上月亮 + 右上灯笼 */
.header-deco {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 100%;
  pointer-events: none;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  box-sizing: border-box;
}

.moon-icon {
  width: 36px;
  height: 36px;
  filter: drop-shadow(0 0 8px rgba(255, 215, 0, 0.6));
  animation: moon-float 4s ease-in-out infinite alternate;
}

.lantern-icon {
  width: 24px;
  height: 40px;
  transform-origin: top center;
  animation: lantern-swing 3s ease-in-out infinite alternate;
}

@keyframes moon-float {
  from { transform: translateY(0); }
  to { transform: translateY(-3px); }
}

@keyframes lantern-swing {
  from { transform: rotate(-5deg); }
  to { transform: rotate(5deg); }
}

.header-content {
  max-width: 1200px;
  height: 100%;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  gap: 20px;
  position: relative;
  z-index: 1;
}

.logo {
  font-size: 20px;
  font-weight: 700;
  color: #ffd700;
  cursor: pointer;
  white-space: nowrap;
  display: flex;
  align-items: baseline;
  gap: 6px;
  font-family: 'STKaiti', 'KaiTi', 'Microsoft YaHei', serif;
  text-shadow: 0 0 8px rgba(255, 215, 0, 0.4);
}

.logo-sub {
  font-size: 12px;
  color: #c8901b;
  font-weight: 400;
  letter-spacing: 2px;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 4px;
}

.nav-link {
  padding: 6px 14px;
  color: #fdf6e3;
  font-size: 15px;
  font-weight: 500;
  font-family: 'Microsoft YaHei', sans-serif;
  cursor: pointer;
  border-radius: 4px;
  transition: color 0.2s, background 0.2s;
  user-select: none;
  white-space: nowrap;
}

.nav-link:hover {
  color: #ffd700;
  background: transparent;
}

.nav-link.active {
  color: #ffd700;
  font-weight: 700;
  border-bottom: 2px solid #ffd700;
  border-radius: 0;
  background: transparent;
}

.search-box {
  flex: 1;
  max-width: 360px;
}

:deep(.search-box .el-input-group__append) {
  background: #c8901b;
  border: 1px solid #c8901b;
}

:deep(.search-box .el-input-group__append .el-button) {
  color: #fff !important;
}

:deep(.search-box .el-input-group__append .el-button:hover) {
  background: #b87f0a;
}

:deep(.search-box .el-input__wrapper) {
  background: #fffdf5;
  box-shadow: 0 0 0 1px #d4b996 inset;
}

:deep(.search-box .el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c8901b inset;
}

:deep(.search-box .el-input-group__append) {
  background: #c8901b;
  border: 1px solid #c8901b;
}

:deep(.search-box .el-input-group__append .el-button) {
  color: #fff !important;
}

:deep(.search-box .el-input-group__append .el-button:hover) {
  background: #b87f0a;
}

:deep(.search-box .el-input__wrapper) {
  background: #fffdf5;
  box-shadow: 0 0 0 1px #d4b996 inset;
}

:deep(.search-box .el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c8901b inset;
}

:deep(.search-box .el-input-group__append) {
  background: #c8901b;
  border: 1px solid #c8901b;
}

:deep(.search-box .el-input-group__append .el-button) {
  color: #fff !important;
}

:deep(.search-box .el-input-group__append .el-button:hover) {
  background: #b87f0a;
}

:deep(.search-box .el-input__wrapper) {
  background: #fffdf5;
  box-shadow: 0 0 0 1px #d4b996 inset;
}

:deep(.search-box .el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c8901b inset;
}

:deep(.search-box .el-input-group__append) {
  background: #c8901b;
  border: 1px solid #c8901b;
}

:deep(.search-box .el-input-group__append .el-button) {
  color: #fff !important;
}

:deep(.search-box .el-input-group__append .el-button:hover) {
  background: #b87f0a;
}

:deep(.search-box .el-input__wrapper) {
  background: #fffdf5;
  box-shadow: 0 0 0 1px #d4b996 inset;
}

:deep(.search-box .el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c8901b inset;
}

:deep(.search-box .el-input-group__append) {
  background: #c8901b;
  border: 1px solid #c8901b;
}

:deep(.search-box .el-input-group__append .el-button) {
  color: #fff !important;
}

:deep(.search-box .el-input-group__append .el-button:hover) {
  background: #b87f0a;
}

:deep(.search-box .el-input__wrapper) {
  background: #fffdf5;
  box-shadow: 0 0 0 1px #d4b996 inset;
}

:deep(.search-box .el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c8901b inset;
}

.user-actions {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 8px;
}

:deep(.user-actions .el-button),
:deep(.cart-badge .el-button) {
  color: #fdf6e3 !important;
  background: transparent !important;
}

:deep(.user-actions .el-button:hover),
:deep(.cart-badge .el-button:hover) {
  background: transparent !important;
  color: #ffd700 !important;
}

:deep(.user-actions .el-button--primary) {
  background: #c8901b !important;
  border-color: #c8901b !important;
  color: #fff !important;
}

:deep(.user-actions .el-button--danger) {
  color: #ff8888 !important;
  background: transparent !important;
}

:deep(.user-actions .el-button--danger:hover) {
  background: transparent !important;
  color: #ffaaaa !important;
}

.cart-badge {
  margin-right: 4px;
}

.nickname {
  color: #d4b996;
  font-size: 14px;
}

@media (max-width: 767px) {
  .header-content {
    padding: 0 12px;
    gap: 8px;
  }
  .logo {
    font-size: 16px;
  }
  .logo-sub {
    display: none;
  }
  .nav-links {
    display: none;
  }
  .search-box {
    max-width: none;
  }
  .nickname {
    display: none;
  }
  .moon-icon {
    width: 28px;
    height: 28px;
  }
}
</style>
