<template>
  <div class="order-page">
    <el-card>
      <div class="filter-bar">
        <el-input
          v-model="query.orderNo"
          placeholder="搜索订单号"
          clearable
          class="filter-item"
          @clear="loadData"
          @keyup.enter="loadData"
        />
        <el-select v-model="query.payStatus" placeholder="订单状态" clearable class="filter-item" @change="loadData">
          <el-option label="待支付" :value="0" />
          <el-option label="已支付" :value="1" />
          <el-option label="已取消" :value="2" />
        </el-select>
        <el-button type="primary" @click="loadData">搜索</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="订单号" min-width="220" />
        <el-table-column prop="userId" label="用户ID" width="90" />
        <el-table-column label="金额" width="120">
          <template #default="{ row }">¥{{ formatPrice(row.totalAmount) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.payStatus)">{{ statusText(row.payStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="下单时间" width="180">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="支付时间" width="180">
          <template #default="{ row }">{{ formatTime(row.payTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="showDetail(row)">详情</el-button>
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

    <el-dialog v-model="detailVisible" title="订单详情" width="640px">
      <div v-if="currentOrder">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusType(currentOrder.payStatus)">{{ statusText(currentOrder.payStatus) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="金额">¥{{ formatPrice(currentOrder.totalAmount) }}</el-descriptions-item>
          <el-descriptions-item label="用户ID">{{ currentOrder.userId }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ formatTime(currentOrder.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="支付时间">{{ formatTime(currentOrder.payTime) }}</el-descriptions-item>
        </el-descriptions>

        <h4 style="margin: 16px 0 8px;">商品明细</h4>
        <el-table :data="currentOrder.items || []" border>
          <el-table-column label="封面" width="80">
            <template #default="{ row }">
              <el-image :src="row.productImage || '/placeholder.svg'" class="cover" fit="cover" />
            </template>
          </el-table-column>
          <el-table-column prop="productName" label="商品" min-width="200" />
          <el-table-column label="单价" width="100">
            <template #default="{ row }">¥{{ formatPrice(row.productPrice) }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column label="小计" width="100">
            <template #default="{ row }">¥{{ formatPrice(Number(row.productPrice || 0) * Number(row.quantity || 0)) }}</template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getOrderDetail, getOrders } from '../../api/order'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const detailVisible = ref(false)
const currentOrder = ref(null)
const query = reactive({ page: 1, size: 10, orderNo: '', payStatus: '' })

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const params = {
      page: query.page,
      size: query.size,
      orderNo: query.orderNo || undefined,
      payStatus: query.payStatus === '' ? undefined : query.payStatus
    }
    const res = await getOrders(params)
    const data = (res && res.data) || {}
    tableData.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  query.orderNo = ''
  query.payStatus = ''
  query.page = 1
  loadData()
}

async function showDetail(row) {
  try {
    const res = await getOrderDetail(row.id)
    currentOrder.value = (res && res.data) || null
    detailVisible.value = true
  } catch (e) {
    ElMessage.error('订单详情加载失败')
  }
}

function statusText(s) {
  return ['待支付', '已支付', '已取消'][s] || '未知'
}

function statusType(s) {
  return ['warning', 'success', 'info'][s] || 'info'
}

function formatPrice(value) {
  return Number(value || 0).toFixed(2)
}

function formatTime(time) {
  if (!time) return '-'
  return String(time).replace('T', ' ').substring(0, 19)
}
</script>

<style scoped>
.order-page {
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
  width: 200px;
}

.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}

.cover {
  width: 48px;
  height: 48px;
  border-radius: 4px;
  background: #f5f7fa;
}
</style>
