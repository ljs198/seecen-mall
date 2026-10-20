import vue from '@vitejs/plugin-vue'
import { defineConfig } from 'vite'

// GitHub Pages 部署配置
// base 路径 = GitHub 仓库名 /seecen-mall/
export default defineConfig({
  plugins: [vue()],
  base: '/seecen-mall/',
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
