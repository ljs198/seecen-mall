<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '../../store/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const onSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await userStore.login(form)
    // 后台仅允许管理员账号进入，普通用户登录成功也立即拦截
    if (userStore.userInfo?.role !== 'ADMIN') {
      await userStore.logout()
      ElMessage.error('该账号无后台访问权限，请使用管理员账号')
      return
    }
    ElMessage.success('登录成功')
    router.push('/')
  } catch (e) {
    // request 拦截器已统一提示
  } finally {
    loading.value = false
  }
}

const goRegister = () => router.push('/register')
</script>

<template>
  <div class="login-page">
    <el-card class="login-card">
      <template #header>
        <div class="header">
          <el-icon><User /></el-icon>
          <span>智能商城管理端</span>
        </div>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" @submit.prevent="onSubmit">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :prefix-icon="User" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" show-password @keyup.enter="onSubmit" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading" style="width: 100%">登录</el-button>
        </el-form-item>
        <el-form-item>
          <el-button link style="width: 100%" @click="goRegister">没有账号？立即注册</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
}
.login-card { width: 420px; border-radius: 8px; }
.header {
  display: flex; align-items: center; gap: 8px;
  font-size: 18px; font-weight: 600;
}
.tip { margin-top: 8px; color: #909399; font-size: 12px; text-align: center; }
</style>