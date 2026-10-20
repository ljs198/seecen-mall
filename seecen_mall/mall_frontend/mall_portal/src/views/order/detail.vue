<template>
  <div class="order-detail-page">
    <div v-loading="loading">
      <template v-if="order">
        <div class="page-header">
          <el-button text @click="router.push('/orders')">返回订单列表</el-button>
        </div>

        <section class="panel">
          <div class="panel-title">
            <h2>订单信息</h2>
            <el-tag :type="statusTagType(order.payStatus)" effect="light">
              {{ statusLabel(order.payStatus) }}
            </el-tag>
          </div>
          <div class="info-grid">
            <div>
              <span class="label">订单编号</span>
              <span class="value">{{ order.orderNo }}</span>
            </div>
            <div>
              <span class="label">下单时间</span>
              <span class="value">{{ formatTime(order.createTime) }}</span>
            </div>
            <div v-if="order.payStatus === 1">
              <span class="label">支付时间</span>
              <span class="value">{{ formatTime(order.payTime) }}</span>
            </div>
            <div v-if="order.payStatus === 2">
              <span class="label">取消时间</span>
              <span class="value">{{ formatTime(order.updateTime) }}</span>
            </div>
          </div>
        </section>

        <section class="panel">
          <h3>商品明细</h3>
          <div v-for="item in order.items || []" :key="item.id" class="product-item">
            <el-image
              :src="resolveProductImage(item)"
              class="product-image"
              fit="cover"
            >
              <template #error>
                <div class="image-fallback">图片</div>
              </template>
            </el-image>
            <div class="product-info">
              <p class="product-name">{{ item.productName }}</p>
              <p class="product-price">¥{{ formatPrice(item.productPrice) }}</p>
            </div>
            <div class="product-quantity">x{{ item.quantity }}</div>
            <div class="product-subtotal">¥{{ formatPrice(Number(item.productPrice || 0) * Number(item.quantity || 0)) }}</div>
          </div>
        </section>

        <section class="panel">
          <div class="total-row">
            <span>商品总额</span>
            <span>¥{{ formatPrice(order.totalAmount) }}</span>
          </div>
          <div class="total-row">
            <span>运费</span>
            <span class="free-shipping">免运费</span>
          </div>
          <div class="total-row final-row">
            <span>实付金额</span>
            <span class="final-amount">¥{{ formatPrice(order.totalAmount) }}</span>
          </div>
        </section>

        <div class="action-bar">
          <template v-if="order.payStatus === 0">
            <el-button size="large" @click="handleCancel">取消订单</el-button>
            <el-button type="primary" size="large" :loading="paying" @click="handlePay">立即支付</el-button>
          </template>
          <el-button v-else size="large" @click="router.push('/orders')">返回订单列表</el-button>
        </div>
      </template>

      <el-empty v-if="!loading && !order" description="订单不存在">
        <el-button type="primary" @click="router.push('/orders')">返回订单列表</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { cancelOrder, getOrderDetail, payOrder } from '../../api/order'

const route = useRoute()
const router = useRouter()
const loading = ref(true)
const paying = ref(false)
const order = ref(null)

onMounted(() => {
  fetchOrder()
})

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

/**
 * 订单明细表 oms_order_item 没有图片字段（只有 product_id/product_name/product_price/quantity）
 * 这里用 product_id 推算商品图路径，回退到 placeholder
 */
function resolveProductImage(item) {
  const API_BASE = import.meta.env.VITE_API_BASE_URL || ''
  // 占位图带站点 base 前缀（GitHub Pages 部署在 /seecen-mall/ 子路径下）
  const SITE_BASE = import.meta.env.BASE_URL || '/'
  const toFull = (p) => (/^https?:\/\//.test(p) ? p : API_BASE + p)
  if (item.productImage) return toFull(item.productImage)
  if (item.productId) {
    return toFull(`/api/uploads/images/2026/09/02/product-${item.productId}.jpg`)
  }
  return SITE_BASE + 'placeholder.svg'
}

function formatTime(time) {
  if (!time) return ''
  return String(time).replace('T', ' ').substring(0, 16)
}

// 根据路由参数查询订单详情
async function fetchOrder() {
  loading.value = true
  try {
    const res = await getOrderDetail(route.params.id)
    // 拦截器返回的是完整 Result 对象，从 res.data 取订单
    order.value = (res && res.data) || res
  } finally {
    loading.value = false
  }
}

// 模拟支付当前订单，成功后刷新详情
async function handlePay() {
  if (!order.value) return
  paying.value = true
  try {
    await payOrder(order.value.id)
    ElMessage.success('支付成功')
    await fetchOrder()
  } finally {
    paying.value = false
  }
}

// 取消当前待支付订单，成功后刷新详情
async function handleCancel() {
  if (!order.value) return
  await ElMessageBox.confirm(`确定要取消订单 ${order.value.orderNo} 吗？`, '取消订单', {
    confirmButtonText: '确定取消',
    cancelButtonText: '再想想',
    type: 'warning'
  })
  await cancelOrder(order.value.id)
  ElMessage.success('订单已取消')
  await fetchOrder()
}
</script>

<style scoped>
.order-detail-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px 16px 60px;
  min-height: calc(100vh - 64px);
  background: linear-gradient(180deg, #fdf6e3 0%, #fffdf5 100%);
}

.page-header {
  margin-bottom: 16px;
}

.panel {
  margin-bottom: 16px;
  padding: 22px;
  background: linear-gradient(135deg, #fffdf5 0%, #fff8e7 100%);
  border: 1.5px solid #d4b996;
  border-radius: 10px;
}

.panel-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.panel-title h2,
.panel h3 {
  margin: 0 0 12px;
  font-size: 18px;
  color: #1a1b3a;
  font-family: 'STKaiti', 'KaiTi', serif;
  font-weight: 700;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.label,
.value {
  display: block;
}

.label {
  margin-bottom: 4px;
  color: #8b6f47;
  font-size: 13px;
}

.value {
  color: #3e2723;
}

.product-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 14px 0;
  border-bottom: 1px dashed #d4b996;
}

.product-item:last-child {
  border-bottom: none;
}

.product-image,
.image-fallback {
  width: 64px;
  height: 64px;
  border-radius: 6px;
  border: 1px solid #d4b996;
}

.image-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fdf6e3;
  color: #c8901b;
}

.product-info {
  flex: 1;
}

.product-name,
.product-price {
  margin: 0;
}

.product-price {
  margin-top: 6px;
  color: #c0392b;
}

.product-quantity {
  color: #5d4e37;
}

.product-subtotal {
  min-width: 100px;
  color: #c0392b;
  font-weight: 700;
  text-align: right;
  font-family: 'STKaiti', 'KaiTi', serif;
}

.total-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  color: #5d4e37;
}

.free-shipping {
  color: #c8901b;
}

.final-row {
  margin-top: 8px;
  border-top: 1px dashed #d4b996;
  padding-top: 16px;
}

.final-amount {
  color: #c0392b;
  font-size: 24px;
  font-weight: 700;
  font-family: 'STKaiti', 'KaiTi', serif;
}

.action-bar {
  margin-top: 24px;
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

/* 中秋风格：立即支付按钮 */
:deep(.action-bar .el-button--primary) {
  background: #c8901b;
  border-color: #c8901b;
}

:deep(.action-bar .el-button--primary:hover) {
  background: #b87f0a;
  border-color: #b87f0a;
}

@media (max-width: 767px) {
  .info-grid {
    grid-template-columns: 1fr;
  }

  .action-bar {
    flex-direction: column;
  }
}
</style>
