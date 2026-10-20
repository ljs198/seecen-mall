<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="layout-aside">
      <div class="logo">智能商城后台</div>
      <el-menu
        :default-active="currentRoute"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataLine /></el-icon>
          <template #title>仪表盘</template>
        </el-menu-item>
        <el-menu-item index="/products">
          <el-icon><Goods /></el-icon>
          <template #title>商品管理</template>
        </el-menu-item>
        <el-menu-item index="/categories">
          <el-icon><Menu /></el-icon>
          <template #title>分类管理</template>
        </el-menu-item>
        <el-menu-item index="/orders">
          <el-icon><List /></el-icon>
          <template #title>订单管理</template>
        </el-menu-item>
        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="layout-header">
        <div class="header-title">{{ route.meta.title || '后台管理' }}</div>
        <div class="header-actions">
          <span>{{ userStore.userInfo?.nickname || '管理员' }}</span>
          <el-button type="danger" text @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { DataLine, Goods, List, Menu, User } from '@element-plus/icons-vue'
import { useUserStore } from '../store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const currentRoute = computed(() => route.path)

/**
 * 退出管理后台登录并回到登录页
 */
async function handleLogout() {
  await userStore.logout()
  ElMessage.success('已退出')
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.layout-aside {
  background: #304156;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  font-size: 18px;
  font-weight: 700;
  background: #263445;
}
.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e5e7eb;
  background: #ffffff;
}
.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #606266;
}
.layout-main {
  background: #f0f2f5;
  padding: 20px;
}
.el-menu {
  border-right: none;
}
</style>
