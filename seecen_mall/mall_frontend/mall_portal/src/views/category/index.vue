<template>
  <div class="category-page container">
    <aside class="category-sidebar">
      <el-menu :default-active="String(activeId || 0)" @select="handleSelect">
        <el-menu-item index="0">全部商品</el-menu-item>
        <template v-for="category in categories" :key="category.id">
          <el-sub-menu v-if="category.children && category.children.length" :index="'parent-' + category.id">
            <template #title>{{ category.name }}</template>
            <el-menu-item :index="String(category.id)">全部{{ category.name }}</el-menu-item>
            <el-menu-item
              v-for="child in category.children"
              :key="child.id"
              :index="String(child.id)"
            >
              {{ child.name }}
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="String(category.id)">{{ category.name }}</el-menu-item>
        </template>
      </el-menu>
    </aside>

    <main class="category-main">
      <div class="toolbar">
        <el-input
          v-model="keyword"
          placeholder="搜索商品"
          clearable
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>

      <div class="mobile-categories">
        <el-tag :type="!activeId ? 'primary' : 'info'" @click="handleSelect('0')">全部</el-tag>
        <template v-for="category in categories" :key="category.id">
          <el-tag
            :type="activeId === category.id ? 'primary' : 'info'"
            @click="handleSelect(String(category.id))"
          >
            {{ category.name }}
          </el-tag>
          <el-tag
            v-for="child in category.children || []"
            :key="child.id"
            :type="activeId === child.id ? 'primary' : 'info'"
            @click="handleSelect(String(child.id))"
          >
            {{ child.name }}
          </el-tag>
        </template>
      </div>

      <div class="product-grid" v-loading="loading">
        <ProductCard v-for="product in products" :key="product.id" :product="product" />
      </div>
      <el-empty v-if="!loading && products.length === 0" description="暂无商品" />
      <el-pagination
        v-if="total > 0"
        v-model:current-page="page"
        :total="total"
        :page-size="12"
        layout="prev, pager, next"
        class="pagination"
        @current-change="handlePageChange"
      />
    </main>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import ProductCard from '../../components/ProductCard.vue'
import { getCategories, getProducts } from '../../api/product'

const route = useRoute()
const router = useRouter()
const categories = ref([])
const products = ref([])
const activeId = ref(0)
const keyword = ref('')
const page = ref(1)
const total = ref(0)
const loading = ref(false)

onMounted(async () => {
  await loadCategories()
  syncQuery()
  await loadProducts()
})

// 监听 URL query 变化重新加载商品
watch(
  () => route.query,
  async () => {
    syncQuery()
    await loadProducts()
  }
)

// 加载分类树
async function loadCategories() {
  const res = await getCategories()
  categories.value = Array.isArray(res) ? res : (res && res.data) || []
}

// 把 URL 查询参数同步到组件状态
function syncQuery() {
  activeId.value = route.query.id ? Number(route.query.id) : 0
  keyword.value = route.query.keyword ? String(route.query.keyword) : ''
  page.value = route.query.page ? Number(route.query.page) : 1
}

// 选择分类：更新 URL query
function handleSelect(id) {
  const nextQuery = Object.assign({}, route.query, { page: 1 })
  if (Number(id) === 0) {
    delete nextQuery.id
  } else {
    nextQuery.id = id
  }
  router.push({ path: '/category', query: nextQuery })
}

// 关键字搜索：更新 URL query
function handleSearch() {
  const nextQuery = Object.assign({}, route.query, { page: 1 })
  const kw = keyword.value.trim()
  if (kw) {
    nextQuery.keyword = kw
  } else {
    delete nextQuery.keyword
  }
  router.push({ path: '/category', query: nextQuery })
}

// 分页变化：更新 URL query
function handlePageChange(newPage) {
  const nextQuery = Object.assign({}, route.query, { page: newPage })
  router.push({ path: '/category', query: nextQuery })
}

// 按当前 URL 条件查询商品
async function loadProducts() {
  loading.value = true
  try {
    const params = { page: page.value, size: 12 }
    if (activeId.value) {
      params.categoryId = activeId.value
    }
    if (keyword.value.trim()) {
      params.keyword = keyword.value.trim()
    }
    const res = await getProducts(params)
    products.value = (res && res.data && res.data.records) || []
    total.value = (res && res.data && res.data.total) || 0
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 20px 40px;
  min-height: calc(100vh - 64px);
  background: linear-gradient(180deg, #fdf6e3 0%, #fffdf5 100%);
}

.category-page {
  display: flex;
  gap: 20px;
}

.category-sidebar {
  width: 220px;
  flex-shrink: 0;
  background: linear-gradient(135deg, #fffdf5 0%, #fff8e7 100%);
  border: 1.5px solid #d4b996;
  border-radius: 10px;
  padding: 10px 8px;
}

.category-main {
  flex: 1;
  min-width: 0;
}

.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 18px;
}

.mobile-categories {
  display: none;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.mobile-categories .el-tag {
  cursor: pointer;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
  min-height: 120px;
}

.pagination {
  margin-top: 20px;
  justify-content: center;
}

/* 中秋风格：侧边栏菜单 */
:deep(.category-sidebar .el-menu) {
  background: transparent;
  border-right: none;
}

:deep(.category-sidebar .el-menu-item),
:deep(.category-sidebar .el-sub-menu__title) {
  color: #5d4e37;
  border-radius: 6px;
  margin: 2px 0;
}

:deep(.category-sidebar .el-menu-item:hover),
:deep(.category-sidebar .el-sub-menu__title:hover) {
  background: rgba(200, 144, 27, 0.12);
  color: #c8901b;
}

:deep(.category-sidebar .el-menu-item.is-active) {
  color: #c8901b;
  font-weight: 700;
  background: rgba(200, 144, 27, 0.15);
}

/* 中秋风格：搜索框 + 按钮 */
:deep(.toolbar .el-input__wrapper) {
  background: #fffdf5;
  box-shadow: 0 0 0 1px #d4b996 inset;
}

:deep(.toolbar .el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c8901b inset;
}

:deep(.toolbar .el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #c8901b inset;
}

:deep(.toolbar .el-button--primary) {
  background: #c8901b;
  border-color: #c8901b;
}

:deep(.toolbar .el-button--primary:hover) {
  background: #b87f0a;
  border-color: #b87f0a;
}

/* 中秋风格：移动端分类标签 */
:deep(.mobile-categories .el-tag--primary) {
  background: #c8901b;
  border-color: #c8901b;
  color: #fff;
}

@media (max-width: 767px) {
  .container {
    padding: 14px;
  }

  .category-page {
    display: block;
  }

  .category-sidebar {
    display: none;
  }

  .mobile-categories {
    display: flex;
  }

  .product-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 12px;
  }
}
</style>
