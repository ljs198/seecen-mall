# Seecen Mall 部署指南

## 方案一：GitHub Pages（仅前端展示，API不可用）

### 1. 创建 GitHub 仓库
- 打开 https://github.com/new
- 仓库名: `seecen-mall`，选 Public
- 不要勾选任何初始化选项

### 2. 推送代码
```bash
cd D:\seecen_mall
git init
git add -A
git commit -m "Initial commit: AI智能商城系统"
git branch -M main
git remote add origin https://github.com/你的用户名/seecen-mall.git
git push -u origin main
```

### 3. 开启 GitHub Pages
- 仓库页面 → Settings → Pages
- Source 选 GitHub Actions
- 推送后自动构建部署
- 访问: https://你的用户名.github.io/seecen-mall/

---

## 方案二：免费平台组合部署（完整功能）

### 架构
| 层 | 平台 | 免费额度 |
|----|------|---------|
| 前端 | Vercel | 100GB/月流量 |
| 后端 | Railway | $5/月免费额度 |
| 数据库 | Supabase | 500MB存储 |

### 1. 部署数据库到 Supabase
1. 打开 https://supabase.com 注册
2. New Project → 填项目名 → Region 选 Singapore
3. 等2分钟创建完成
4. 左侧 SQL Editor → 粘贴 seecen_mall.sql 内容 → Run
5. Project Settings → Database → 记下 Connection String

### 2. 部署后端到 Railway
1. 打开 https://railway.app 用 GitHub 登录
2. New Project → Deploy from GitHub repo → 选 seecen-mall
3. Settings → Root Directory 设为 mall_server
4. 添加环境变量：
   - SPRING_DATASOURCE_URL = Supabase的Connection String
   - SPRING_DATASOURCE_USERNAME = postgres
   - SPRING_DATASOURCE_PASSWORD = 你的Supabase密码
   - SPRING_AI_OPENAI_API_KEY = 你的通义千问API Key
5. Railway自动构建部署
6. 拿到公网地址: https://xxx.up.railway.app

### 3. 部署前端到 Vercel
1. 打开 https://vercel.com 用 GitHub 登录
2. Add New Project → 选 seecen-mall 仓库
3. 配置：
   - Root Directory: mall_frontend/mall_portal
   - Build Command: npm install && npm run build
   - Output Directory: dist
4. 环境变量：
   - VITE_API_BASE_URL = https://xxx.up.railway.app
5. Deploy

### 4. 修改前端环境变量
修改 mall_frontend/mall_portal/.env.production 中的地址为你的 Railway 后端地址
