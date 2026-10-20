<template>
  <div class="checkout-page">
    <div class="page-header">
      <el-button text @click="router.back()">返回</el-button>
      <h2>确认订单</h2>
    </div>

    <el-empty
      v-if="!loading && selectedItems.length === 0"
      description="没有选中的商品，请先去购物车选择商品"
    >
      <el-button type="primary" @click="router.push('/cart')">去购物车</el-button>
    </el-empty>

    <template v-else>
      <section class="panel">
        <h3>商品清单</h3>
        <div v-loading="loading" class="order-items">
          <div v-for="item in selectedItems" :key="item.id" class="order-item">
            <el-image
              :src="item.productCoverImage || '/placeholder.svg'"
              class="item-image"
              fit="cover"
            >
              <template #error>
                <div class="image-fallback">图片</div>
              </template>
            </el-image>
            <div class="item-info">
              <p class="item-name">{{ item.productName || ('商品' + item.productId) }}</p>
              <p class="item-count">数量：{{ item.quantity }}</p>
            </div>
            <div class="item-price">¥{{ formatPrice(item.productPrice) }}</div>
            <div class="item-subtotal">¥{{ formatPrice(Number(item.productPrice || 0) * Number(item.quantity || 0)) }}</div>
          </div>
        </div>
      </section>

      <section class="panel">
        <div class="summary-row">
          <span>商品总额</span>
          <span>¥{{ formatPrice(totalAmount) }}</span>
        </div>
        <div class="summary-row">
          <span>运费</span>
          <span class="free-shipping">免运费</span>
        </div>
        <div class="summary-row total-row">
          <span>应付金额</span>
          <span class="total-amount">¥{{ formatPrice(totalAmount) }}</span>
        </div>
        <el-button
          type="primary"
          size="large"
          class="submit-btn"
          :loading="submitting"
          @click="handleSubmit"
        >提交并模拟支付</el-button>
      </section>
    </template>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createOrder, payOrder } from '../../api/order'
import { useCartStore } from '../../store/cart'

const router = useRouter()
const cartStore = useCartStore()
const loading = ref(true)
const submitting = ref(false)

const selectedItems = computed(() => cartStore.checkedItems)
const totalAmount = computed(() => cartStore.checkedAmount)

onMounted(async () => {
  await loadCart()
})

// 加载购物车并检查是否存在选中商品
async function loadCart() {
  loading.value = true
  try {
    await cartStore.fetchCart()
    if (cartStore.checkedItems.length === 0) {
      ElMessage.warning('请先在购物车中选择要结算的商品')
    }
  } finally {
    loading.value = false
  }
}

function formatPrice(price) {
  return Number(price || 0).toFixed(2)
}

// 创建订单并模拟支付，成功后进入订单详情页
async function handleSubmit() {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('没有选中的商品')
    return
  }

  submitting.value = true
  try {
    const cartItemIds = selectedItems.value.map((item) => item.id)
    const orderRes = await createOrder({ cartItemIds })
    // 拦截器返回的是完整 Result 对象，从 res.data 取订单
    const order = (orderRes && orderRes.data) || orderRes
    await payOrder(order.id)
    cartStore.resetCart()
    ElMessage.success('支付成功')
    router.push(`/orders/${order.id}`)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.checkout-page {
  max-width: 960px;
  margin: 0 auto;
  padding: 24px 16px 60px;
  min-height: calc(100vh - 64px);
  background: linear-gradient(180deg, #fdf6e3 0%, #fffdf5 100%);
}

.page-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #1a1b3a;
  font-family: 'STKaiti', 'KaiTi', serif;
  font-weight: 700;
}

.panel {
  margin-bottom: 16px;
  padding: 22px;
  background: linear-gradient(135deg, #fffdf5 0%, #fff8e7 100%);
  border: 1.5px solid #d4b996;
  border-radius: 10px;
}

.panel h3 {
  margin: 0 0 14px;
  font-size: 16px;
  color: #1a1b3a;
  font-family: 'STKaiti', 'KaiTi', serif;
}

.order-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 14px 0;
  border-bottom: 1px dashed #d4b996;
}

.order-item:last-child {
  border-bottom: none;
}

.item-image,
.image-fallback {
  width: 72px;
  height: 72px;
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

.item-info {
  flex: 1;
}

.item-name {
  margin: 0 0 6px;
  color: #3e2723;
}

.item-count {
  margin: 0;
  color: #8b6f47;
  font-size: 13px;
}

.item-price {
  color: #5d4e37;
}

.item-subtotal {
  min-width: 100px;
  color: #c0392b;
  font-weight: 700;
  text-align: right;
  font-family: 'STKaiti', 'KaiTi', serif;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  color: #5d4e37;
}

.free-shipping {
  color: #c8901b;
}

.total-row {
  margin-top: 8px;
  border-top: 1px dashed #d4b996;
  padding-top: 16px;
}

.total-amount {
  color: #c0392b;
  font-size: 24px;
  font-weight: 700;
  font-family: 'STKaiti', 'KaiTi', serif;
}

.submit-btn {
  width: 100%;
  margin-top: 16px;
}

/* 中秋风格：提交按钮中秋金 */
:deep(.submit-btn.el-button--primary) {
  background: #c8901b;
  border-color: #c8901b;
}

:deep(.submit-btn.el-button--primary:hover) {
  background: #b87f0a;
  border-color: #b87f0a;
}

@media (max-width: 767px) {
  .order-item {
    flex-wrap: wrap;
  }

  .item-subtotal {
    min-width: auto;
  }
}
</style>
