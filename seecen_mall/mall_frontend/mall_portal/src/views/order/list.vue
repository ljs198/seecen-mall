<template>
  <div class="order-list-page">
    <div class="page-header">
      <el-button text @click="router.back()">返回</el-button>
      <h2>我的订单</h2>
    </div>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane label="待支付" name="pending" />
      <el-tab-pane label="已支付" name="paid" />
      <el-tab-pane label="已取消" name="cancelled" />
    </el-tabs>

    <div v-loading="loading" class="order-list">
      <article v-for="order in orderList" :key="order.id" class="order-card">
        <header class="card-header">
          <span class="order-no">订单号：{{ order.orderNo }}</span>
          <el-tag :type="statusTagType(order.payStatus)" effect="light">
            {{ statusLabel(order.payStatus) }}
          </el-tag>
        </header>

        <div class="card-body" @click="goDetail(order.id)">
          <div class="product-thumbs">
            <el-image
              v-for="item in (order.items || []).slice(0, 3)"
              :key="item.id"
              :src="resolveProductImage(item)"
              class="thumb"
              fit="cover"
            >
              <template #error>
                <div class="thumb-fallback">图</div>
              </template>
            </el-image>
            <span v-if="order.items && order.items.length > 3" class="more-count">
              +{{ order.items.length - 3 }}
            </span>
          </div>
          <div class="order-meta">
            <span class="order-amount">¥{{ formatPrice(order.totalAmount) }}</span>
            <span class="order-time">{{ formatTime(order.createTime) }}</span>
          </div>
        </div>

        <footer class="card-footer">
          <template v-if="order.payStatus === 0">
            <el-button size="small" @click.stop="handleCancel(order)">取消订单</el-button>
            <el-button type="primary" size="small" @click.stop="handlePay(order)">立即支付</el-button>
          </template>
          <el-button v-else type="primary" size="small" text @click.stop="goDetail(order.id)">
            查看详情
          </el-button>
        </footer>
      </article>

      <el-empty v-if="!loading && orderList.length === 0" description="暂无订单">
        <el-button type="primary" @click="router.push('/')">去购物</el-button>
      </el-empty>
    </div>

    <div v-if="total > 0" class="pagination-wrap">
      <el-pagination
        v-model:current-page="currentPage"
        :total="total"
        :page-size="pageSize"
        layout="total, prev, pager, next"
        background
        @current-change="fetchOrders"
      />
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { cancelOrder, getOrderList, payOrder } from '../../api/order'

const router = useRouter()
const loading = ref(false)
const orderList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const activeTab = ref('all')

onMounted(() => {
  fetchOrders()
})

// 将标签页名称转换为后端订单状态筛选参数
function tabToStatus(tab) {
  if (tab === 'pending') return 0
  if (tab === 'paid') return 1
  if (tab === 'cancelled') return 2
  return null
}

function statusLabel(status) {
  if (status === 0) return '待支付'
  if (status === 1) return '已支付'
  if (status === 2) return '已取消'
  return '未知'
}

function statusTagType(status) {
  if (status === 0) return 'warning'
  if (status === 1) return 'success'
  return 'info'
}

function formatPrice(price) {
  return Number(price || 0).toFixed(2)
}

// 解析订单项商品图片：/api/ 相对路径拼上后端地址，缺图时按 productId 兜底
function resolveProductImage(item) {
  const API_BASE = import.meta.env.VITE_API_BASE_URL || ''
  const toFull = (p) => (/^https?:\/\//.test(p) ? p : API_BASE + p)
  if (item.productImage) return toFull(item.productImage)
  if (item.productId) return toFull('/api/uploads/images/2026/09/02/product-' + item.productId + '.jpg')
  return '/placeholder.svg'
}

function formatTime(time) {
  if (!time) return ''
  return String(time).replace('T', ' ').substring(0, 16)
}

// 查询当前标签页下的订单分页数据
async function fetchOrders() {
  loading.value = true
  try {
    const payStatus = tabToStatus(activeTab.value)
    const params = { page: currentPage.value, size: pageSize.value }
    if (payStatus !== null) {
      params.payStatus = payStatus
    }
    const res = await getOrderList(params)
    // 拦截器返回的是完整 Result 对象，从 res.data 取分页结果
    const data = (res && res.data) || res
    orderList.value = (data && data.records) || []
    total.value = (data && data.total) || 0
  } finally {
    loading.value = false
  }
}

// 切换订单状态标签后重新从第一页查询
function handleTabChange() {
  currentPage.value = 1
  fetchOrders()
}

function goDetail(orderId) {
  router.push(`/orders/${orderId}`)
}

// 在列表页模拟支付待支付订单
async function handlePay(order) {
  await payOrder(order.id)
  ElMessage.success('支付成功')
  fetchOrders()
}

// 取消待支付订单
async function handleCancel(order) {
  await ElMessageBox.confirm(`确定要取消订单 ${order.orderNo} 吗？`, '取消订单', {
    confirmButtonText: '确定取消',
    cancelButtonText: '再想想',
    type: 'warning'
  })
  await cancelOrder(order.id)
  ElMessage.success('订单已取消')
  fetchOrders()
}
</script>

<style scoped>
.order-list-page {
  max-width: 960px;
  margin: 0 auto;
  padding: 24px 16px 40px;
  min-height: calc(100vh - 64px);
  background: linear-gradient(180deg, #fdf6e3 0%, #fffdf5 100%);
}

.page-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 18px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #1a1b3a;
  font-family: 'STKaiti', 'KaiTi', serif;
  font-weight: 700;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
  min-height: 200px;
}

.order-card {
  background: linear-gradient(135deg, #fffdf5 0%, #fff8e7 100%);
  border: 1.5px solid #d4b996;
  border-radius: 10px;
  transition: border-color 0.25s, box-shadow 0.25s;
}

.order-card:hover {
  border-color: #c8901b;
  box-shadow: 0 6px 20px rgba(200, 144, 27, 0.15);
}

.card-header,
.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 18px;
}

.card-header {
  background: linear-gradient(90deg, #fff8e7 0%, #fdf6e3 100%);
  border-bottom: 1px dashed #d4b996;
  border-radius: 10px 10px 0 0;
}

.order-no {
  color: #5d4e37;
  font-family: monospace;
}

.card-body {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 16px 18px;
  cursor: pointer;
}

.product-thumbs {
  display: flex;
  align-items: center;
  gap: 8px;
}

.thumb,
.thumb-fallback {
  width: 48px;
  height: 48px;
  border-radius: 6px;
  border: 1px solid #d4b996;
}

.thumb-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fdf6e3;
  color: #c8901b;
}

.more-count,
.order-time {
  color: #8b6f47;
  font-size: 12px;
}

.order-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.order-amount {
  color: #c0392b;
  font-weight: 700;
  font-family: 'STKaiti', 'KaiTi', serif;
  font-size: 17px;
}

.card-footer {
  justify-content: flex-end;
  gap: 8px;
  border-top: 1px dashed #d4b996;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

:deep(.el-button--primary) {
  background: #c8901b;
  border-color: #c8901b;
}

:deep(.el-button--primary:hover) {
  background: #b87f0a;
  border-color: #b87f0a;
}

@media (max-width: 767px) {
  .card-body {
    align-items: flex-start;
    flex-direction: column;
  }

  .order-meta {
    width: 100%;
    flex-direction: row;
    justify-content: space-between;
  }
}
</style>
