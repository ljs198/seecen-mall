<template>
  <div class="product-list">
    <el-card>
      <div class="filter-bar">
        <el-input
          v-model="query.keyword"
          placeholder="搜索商品名称"
          clearable
          class="filter-item"
          @clear="loadData"
          @keyup.enter="loadData"
        />
        <el-select v-model="query.categoryId" placeholder="选择分类" clearable class="filter-item" @change="loadData">
          <el-option v-for="category in categories" :key="category.id" :label="category.name" :value="category.id" />
        </el-select>
        <el-select v-model="query.status" placeholder="状态" clearable class="status-filter" @change="loadData">
          <el-option label="上架" :value="1" />
          <el-option label="下架" :value="0" />
        </el-select>
        <el-button type="primary" @click="loadData">搜索</el-button>
        <el-button type="success" @click="router.push('/products/edit')">新增商品</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column label="封面" width="90">
          <template #default="{ row }">
            <el-image :src="row.coverImage" class="cover" fit="cover" />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" min-width="220" show-overflow-tooltip />
        <el-table-column prop="description" label="商品简介" min-width="220" show-overflow-tooltip />
        <el-table-column label="售价" width="110">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="90" />
        <el-table-column prop="salesCount" label="销量" width="90" />
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleToggleStatus(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="router.push(`/products/detail/${row.id}`)">详情</el-button>
            <el-button type="success" text size="small" @click="router.push(`/products/edit/${row.id}`)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        class="pagination"
        @current-change="loadData"
        @size-change="loadData"
      />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCategories } from '../../api/category'
import { getProducts, toggleProductStatus } from '../../api/product'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const categories = ref([])
const total = ref(0)
const query = reactive({
  page: 1,
  size: 10,
  keyword: '',
  categoryId: '',
  status: ''
})

onMounted(() => {
  loadData()
  loadCategories()
})

async function loadData() {
  loading.value = true
  try {
    const result = await getProducts(query)
    tableData.value = result.data.records || []
    total.value = result.data.total || 0
  } finally {
    loading.value = false
  }
}

async function loadCategories() {
  const result = await getCategories()
  categories.value = flattenTree(result.data || [])
}

function flattenTree(tree, result = []) {
  tree.forEach((item) => {
    result.push({ id: item.id, name: item.name })
    if (item.children && item.children.length > 0) {
      flattenTree(item.children, result)
    }
  })
  return result
}

async function handleToggleStatus(row) {
  try {
    await toggleProductStatus(row.id)
    ElMessage.success('状态更新成功')
  } catch (e) {
    row.status = row.status === 1 ? 0 : 1
  }
}
</script>

<style scoped>
.filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
}
.filter-item { width: 200px; }
.status-filter { width: 140px; }
.cover { width: 56px; height: 56px; border-radius: 4px; }
.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
