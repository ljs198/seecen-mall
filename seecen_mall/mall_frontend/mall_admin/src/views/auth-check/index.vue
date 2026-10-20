<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../store/user'
import { getUserInfo } from '../../api/auth'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const infoFromServer = ref(null)

onMounted(async () => {
  loading.value = true
  try {
    const res = await getUserInfo()
    infoFromServer.value = res.data
  } catch (e) {
    // request 拦截器已处理 401
  } finally {
    loading.value = false
  }
})

const onLogout = async () => {
  await userStore.logout()
  ElMessage.success('已退出')
  router.push('/login')
}
</script>

<template>
  <div class="page">
    <el-card class="box">
      <template #header>
        <div class="header">
          <span>用户端登录验证</span>
          <el-button type="danger" link @click="onLogout">退出登录</el-button>
        </div>
      </template>

      <el-alert v-if="!userStore.isLogin" type="warning" :closable="false" show-icon>
        当前未登录，请先 <router-link to="/login">登录</router-link>
      </el-alert>

      <template v-else>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="用户名">{{ userStore.userInfo.username }}</el-descriptions-item>
          <el-descriptions-item label="昵称">{{ userStore.userInfo.nickname }}</el-descriptions-item>
          <el-descriptions-item label="角色">
            <el-tag>{{ userStore.userInfo.role }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="用户 ID">{{ userStore.userInfo.userId }}</el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <h4>从后端拉的最新信息（GET /api/auth/info）</h4>
        <el-alert v-if="loading" type="info" :closable="false">请求中…</el-alert>
        <el-alert v-else-if="infoFromServer" type="success" :closable="false" show-icon>
          <pre>{{ infoFromServer }}</pre>
        </el-alert>
      </template>
    </el-card>
  </div>
</template>

<style scoped>
.page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 32px 16px;
  display: flex;
  justify-content: center;
}
.box { width: 640px; }
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
pre {
  margin: 8px 0 0 0;
  font-family: monospace;
  white-space: pre-wrap;
}
</style>