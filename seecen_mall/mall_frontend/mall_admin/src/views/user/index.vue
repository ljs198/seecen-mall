<template>
  <div class="user-page">
    <el-card>
      <div class="filter-bar">
        <el-input
          v-model="query.keyword"
          placeholder="搜索用户名/昵称"
          clearable
          class="filter-item"
          @clear="loadData"
          @keyup.enter="loadData"
        />
        <el-button type="primary" @click="loadData">搜索</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="nickname" label="昵称" min-width="120" />
        <el-table-column label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : ''">
              {{ row.role === 'ADMIN' ? '管理员' : '用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="toggleStatus(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="注册时间" width="180">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
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
import { ElMessage } from 'element-plus'
import { getUsers, toggleUserStatus } from '../../api/user'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ page: 1, size: 10, keyword: '' })

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const params = {
      page: query.page,
      size: query.size,
      keyword: query.keyword || undefined
    }
    const res = await getUsers(params)
    const data = (res && res.data) || {}
    tableData.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  query.keyword = ''
  query.page = 1
  loadData()
}

async function toggleStatus(row) {
  try {
    await toggleUserStatus(row.id)
    ElMessage.success('状态已更新')
  } catch (e) {
    // 失败回滚 UI
    row.status = row.status === 1 ? 0 : 1
  }
}

function formatTime(time) {
  if (!time) return '-'
  return String(time).replace('T', ' ').substring(0, 19)
}
</script>

<style scoped>
.user-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
}

.filter-item {
  width: 240px;
}

.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
