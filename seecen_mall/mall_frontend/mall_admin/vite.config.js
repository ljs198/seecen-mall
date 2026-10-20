import vue from '@vitejs/plugin-vue'
import { defineConfig } from 'vite'

// 本地开发配置：管理端端口 4000
// 本地没有后端进程，/api 代理到 Railway 线上后端（共用 TiDB 云数据库）
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 4000,
    proxy: {
      '/api': {
        target: 'https://seecen-mall-production.up.railway.app',
        changeOrigin: true
      }
    }
  }
})
