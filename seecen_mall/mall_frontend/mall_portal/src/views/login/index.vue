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
  username: 'user',
  password: 'user123'
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
    <!-- 中秋装饰：月亮 + 桂花 -->
    <div class="login-deco" aria-hidden="true">
      <div class="login-moon"></div>
      <svg class="login-cloud" viewBox="0 0 100 40"><path d="M10,30 Q15,15 30,20 Q40,5 55,20 Q70,15 75,30 Q60,38 40,35 Q20,38 10,30 Z" fill="rgba(255,215,0,0.12)"/></svg>
    </div>

    <el-card class="login-card">
      <template #header>
        <div class="header">
          <el-icon><User /></el-icon>
          <span>智能商城用户端</span>
        </div>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="76px" @submit.prevent="onSubmit">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :prefix-icon="User" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" show-password @keyup.enter="onSubmit" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading" style="width: 100%">登录</el-button>
        </el-form-item>
      </el-form>

      <div class="register-link" @click="goRegister">没有账号？立即注册</div>
      <div class="tip">测试账号：user / user123</div>
    </el-card>
  </div>
</template>

<style scoped>
.login-page {
  position: relative;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #0f1444 0%, #1a1b3a 60%, #2c1810 100%);
  overflow: hidden;
  font-family: 'Microsoft YaHei', 'PingFang SC', sans-serif;
}

.login-deco {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.login-moon {
  position: absolute;
  top: 12%;
  right: 14%;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: radial-gradient(circle at 35% 35%, #fff 0%, #fff8c4 55%, #ffd700 100%);
  box-shadow:
    0 0 60px rgba(255, 215, 0, 0.45),
    0 0 120px rgba(255, 215, 0, 0.25);
  animation: moon-float 5s ease-in-out infinite alternate;
}

@keyframes moon-float {
  from { transform: translateY(0); }
  to { transform: translateY(-8px); }
}

.login-cloud {
  position: absolute;
  bottom: 18%;
  left: 8%;
  width: 200px;
  opacity: 0.7;
  animation: cloud-drift 12s linear infinite;
}

@keyframes cloud-drift {
  from { transform: translateX(-30px); }
  to { transform: translateX(40px); }
}

.login-card {
  position: relative;
  z-index: 1;
  width: 420px;
  border-radius: 12px;
  border: 1.5px solid #c8901b;
  background: linear-gradient(135deg, #fffdf5 0%, #fdf6e3 100%);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.4);
}

.header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 700;
  color: #1a1b3a;
  font-family: 'Microsoft YaHei', sans-serif;
}

.register-link {
  margin-top: 4px;
  text-align: center;
  color: #c8901b;
  font-size: 14px;
  cursor: pointer;
  transition: color 0.2s;
}

.register-link:hover {
  color: #b87f0a;
  text-decoration: underline;
}

.tip {
  margin-top: 10px;
  color: #8b6f47;
  font-size: 12px;
  text-align: center;
}

:deep(.login-card .el-card__header) {
  background: linear-gradient(135deg, #fff8e7 0%, #fdf6e3 100%);
  border-bottom: 1px solid #d4b996;
}

:deep(.login-card .el-form-item__label) {
  color: #3e2723;
  font-weight: 600;
  font-family: 'Microsoft YaHei', sans-serif;
}

:deep(.login-card .el-button--primary) {
  background: #c8901b;
  border-color: #c8901b;
  font-weight: 600;
}

:deep(.login-card .el-button--primary:hover) {
  background: #b87f0a;
  border-color: #b87f0a;
}
</style>