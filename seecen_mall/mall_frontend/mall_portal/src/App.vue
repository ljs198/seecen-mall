<template>
  <div class="app-wrapper">
    <!-- 中秋飘落桂花装饰层 -->
    <div class="midautumn-petals" aria-hidden="true">
      <span v-for="n in 8" :key="n" class="petal" :style="petalStyle(n)">✿</span>
    </div>

    <AppHeader v-if="!isAuthPage" />
    <main class="app-main" :class="{ 'auth-main': isAuthPage }">
      <router-view />
    </main>
    <AppFooter v-if="!isAuthPage" />
    <AiChatWidget v-if="!isAuthPage" />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AppHeader from './components/AppHeader.vue'
import AppFooter from './components/AppFooter.vue'
import AiChatWidget from './components/AiChatWidget.vue'

const route = useRoute()
// 登录、注册、权限验证页不显示公共头部底部
const isAuthPage = computed(() =>
  ['/login', '/register', '/auth-check', '/ai-chat'].includes(route.path)
)

// 桂花花瓣的随机位置和动画延迟
function petalStyle(n) {
  const left = (n * 12 + 5) % 100
  const delay = (n * 1.7) % 12
  const duration = 10 + (n % 4) * 2
  const size = 12 + (n % 3) * 4
  const colors = ['#ffd700', '#c8901b', '#d4a017', '#fff8c4']
  return {
    left: left + '%',
    animationDelay: delay + 's',
    animationDuration: duration + 's',
    fontSize: size + 'px',
    color: colors[n % colors.length]
  }
}
</script>

<style>
html, body, #app {
  margin: 0;
  padding: 0;
  height: 100%;
  font-family: 'Microsoft YaHei', 'PingFang SC', -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
  background: #fdf6e3;
}

.app-wrapper {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
}

.app-main {
  flex: 1;
  padding-top: 64px;
}

.auth-main {
  padding-top: 0;
}

/* 中秋飘落桂花动画 */
.midautumn-petals {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 99;
  overflow: hidden;
}

.midautumn-petals .petal {
  position: absolute;
  top: -20px;
  text-shadow: 0 0 6px rgba(255, 215, 0, 0.6);
  animation: petal-fall linear infinite;
  user-select: none;
}

@keyframes petal-fall {
  0% {
    transform: translateY(-20px) rotate(0deg);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 0.8;
  }
  100% {
    transform: translateY(100vh) rotate(360deg);
    opacity: 0;
  }
}
</style>
