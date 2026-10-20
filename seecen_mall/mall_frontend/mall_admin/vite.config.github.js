import vue from '@vitejs/plugin-vue'
import { defineConfig } from 'vite'

// GitHub Pages 部署配置
// 管理端挂在商城站点下：https://ljs198.github.io/seecen-mall/admin/
export default defineConfig({
  plugins: [vue()],
  base: '/seecen-mall/admin/'
})
