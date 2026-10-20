<template>
  <div class="product-detail container" v-loading="loading">
    <div v-if="product" class="detail-layout">
      <div class="detail-image">
        <el-image
          :src="fullImageUrl(product.coverImage)"
          :alt="product.name"
          fit="cover"
          class="main-image"
        >
          <template #error>
            <div class="image-placeholder">暂无图片</div>
          </template>
        </el-image>
      </div>
      <div class="detail-info">
        <h1>{{ product.name }}</h1>
        <p class="desc">{{ product.description }}</p>
        <div class="price-row">
          <span class="price">¥{{ formatPrice(product.price) }}</span>
          <span
            v-if="product.originalPrice && Number(product.originalPrice) > Number(product.price)"
            class="original-price"
          >
            ¥{{ formatPrice(product.originalPrice) }}
          </span>
        </div>
        <div class="meta">
          <span>库存：{{ product.stock ?? 0 }}</span>
          <span>已售：{{ product.salesCount ?? 0 }}</span>
        </div>

        <div class="actions">
          <el-input-number v-model="quantity" :min="1" :max="product.stock || 1" />
          <el-button type="warning" :loading="adding" @click="handleAddCart">加入购物车</el-button>
        </div>
        <div class="actions">
          <el-button type="danger" disabled>立即购买</el-button>
        </div>
        <el-alert
          class="stage-tip"
          title="订单结算将在后续阶段开放"
          type="info"
          :closable="false"
        />
      </div>
    </div>

    <section v-if="product" class="detail-content">
      <h2>商品详情</h2>
      <div class="rich-content" v-html="product.detail || '暂无详情'"></div>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProductDetail } from '../../api/product'
import { useCartStore } from '../../store/cart'
import { useUserStore } from '../../store/user'
import { fullImageUrl } from '../../utils/image'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const product = ref(null)
const loading = ref(true)
const adding = ref(false)
const quantity = ref(1)

onMounted(async () => {
  await loadProduct()
})

// 根据路由参数加载商品详情
async function loadProduct() {
  loading.value = true
  try {
    const res = await getProductDetail(route.params.id)
    // 拦截器返回的是完整 Result 对象，需要从 res.data 取商品
    product.value = (res && res.data) || null
  } finally {
    loading.value = false
  }
}

// 格式化商品价格
function formatPrice(price) {
  if (price == null) return '0.00'
  return Number(price).toFixed(2)
}

// 加入购物车：未登录先跳登录，登录后调用 Store
async function handleAddCart() {
  if (!userStore.isLogin) {
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  if (!product.value || product.value.id == null) return

  adding.value = true
  try {
    await cartStore.add(product.value.id, quantity.value)
    ElMessage.success('已加入购物车')
  } catch (e) {
    // 拦截器已经弹出错误提示，这里不重复提示
  } finally {
    adding.value = false
  }
}
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 28px 20px 40px;
  min-height: calc(100vh - 64px);
  background: linear-gradient(180deg, #fdf6e3 0%, #fffdf5 100%);
}

.detail-layout {
  display: flex;
  gap: 36px;
  align-items: flex-start;
  background: linear-gradient(135deg, #fffdf5 0%, #fff8e7 100%);
  border: 1.5px solid #d4b996;
  border-radius: 12px;
  padding: 28px;
}

.detail-image {
  width: 420px;
  flex-shrink: 0;
}

.main-image {
  width: 100%;
  aspect-ratio: 1;
  border-radius: 10px;
  background: #fdf6e3;
  border: 1px solid #d4b996;
}

.image-placeholder {
  width: 100%;
  aspect-ratio: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c8901b;
  background: #fdf6e3;
}

.detail-info {
  flex: 1;
}

.detail-info h1 {
  margin: 0 0 12px;
  color: #1a1b3a;
  font-size: 26px;
  font-family: 'STKaiti', 'KaiTi', serif;
  font-weight: 700;
}

.desc {
  color: #5d4e37;
  line-height: 1.7;
}

.price-row {
  margin: 20px 0;
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.price {
  color: #c0392b;
  font-size: 30px;
  font-weight: 700;
  font-family: 'STKaiti', 'KaiTi', serif;
}

.original-price {
  color: #b0a18a;
  text-decoration: line-through;
}

.meta {
  display: flex;
  gap: 24px;
  margin-bottom: 20px;
  color: #8b6f47;
}

.actions {
  margin-top: 16px;
  display: flex;
  gap: 12px;
}

.stage-tip {
  margin-top: 16px;
}

.detail-content {
  margin-top: 28px;
  padding: 24px 28px;
  background: linear-gradient(135deg, #fffdf5 0%, #fff8e7 100%);
  border: 1.5px solid #d4b996;
  border-radius: 12px;
}

.detail-content h2 {
  margin: 0 0 16px;
  font-size: 22px;
  color: #1a1b3a;
  font-family: 'STKaiti', 'KaiTi', serif;
  font-weight: 700;
}

.rich-content {
  color: #3e2723;
  line-height: 1.8;
}

/* 中秋风格：加入购物车按钮 */
:deep(.actions .el-button--warning) {
  background: #c8901b;
  border-color: #c8901b;
  color: #fff;
}

:deep(.actions .el-button--warning:hover) {
  background: #b87f0a;
  border-color: #b87f0a;
}

@media (max-width: 767px) {
  .container {
    padding: 14px;
  }

  .detail-layout {
    display: block;
    padding: 18px;
  }

  .detail-image {
    width: 100%;
    margin-bottom: 18px;
  }

  .detail-info h1 {
    font-size: 22px;
  }

  .price {
    font-size: 26px;
  }

  .actions {
    align-items: stretch;
    flex-direction: column;
  }
}
</style>
