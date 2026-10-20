<template>
  <div class="cart-page container">
    <h1>我的购物车</h1>

    <el-empty
      v-if="!loading && cartStore.cartList.length === 0"
      description="购物车是空的"
    />

    <template v-else>
      <el-table :data="cartStore.cartList" v-loading="loading" class="cart-table">
        <el-table-column width="60">
          <template #header>
            <el-checkbox
              :model-value="allChecked"
              @change="toggleAll"
            />
          </template>
          <template #default="{ row }">
            <el-checkbox
              :model-value="Number(row.checked) === 1"
              @change="(value) => handleCheck(row, value)"
            />
          </template>
        </el-table-column>

        <el-table-column label="商品" min-width="280">
          <template #default="{ row }">
            <div class="product-cell">
              <el-image
                :src="row.productCoverImage || '/placeholder.svg'"
                class="cover"
                fit="cover"
              >
                <template #error>
                  <div class="cover-placeholder">无图</div>
                </template>
              </el-image>
              <div>
                <div class="product-name">{{ row.productName }}</div>
                <div class="product-stock">库存 {{ row.productStock ?? 0 }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="单价" width="120">
          <template #default="{ row }">
            <span class="price">¥{{ formatPrice(row.productPrice) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="数量" width="160">
          <template #default="{ row }">
            <el-input-number
              v-model="row.quantity"
              :min="1"
              :max="row.productStock || 99"
              size="small"
              @change="(value) => handleQuantity(row, value)"
            />
          </template>
        </el-table-column>

        <el-table-column label="小计" width="120">
          <template #default="{ row }">
            <span class="price">
              ¥{{ formatPrice(Number(row.productPrice || 0) * Number(row.quantity || 0)) }}
            </span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="100" align="center">
          <template #default="{ row }">
            <el-popconfirm title="确定删除该商品吗？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="danger" link size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="cart-footer" v-if="cartStore.cartList.length > 0">
        <el-checkbox :model-value="allChecked" @change="toggleAll">全选</el-checkbox>
        <span>已选 {{ cartStore.checkedCount }} 件</span>
        <span>
          合计：<b class="total-price">¥{{ formatPrice(cartStore.checkedAmount) }}</b>
        </span>
        <el-button
          type="danger"
          :disabled="cartStore.checkedCount === 0"
          @click="handleCheckout"
        >去结算</el-button>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useCartStore } from '../../store/cart'

const router = useRouter()

const cartStore = useCartStore()
const loading = ref(true)

// 是否全选：根据当前购物车列表计算（只读）
const allChecked = computed(() => {
  return (
    cartStore.cartList.length > 0 &&
    cartStore.cartList.every((item) => Number(item.checked) === 1)
  )
})

onMounted(async () => {
  try {
    await cartStore.fetchCart()
  } finally {
    loading.value = false
  }
})

function formatPrice(price) {
  return Number(price || 0).toFixed(2)
}

// 切换单条勾选状态
async function handleCheck(row, value) {
  await cartStore.update(row.id, null, value ? 1 : 0)
}

// 改变数量
async function handleQuantity(row, value) {
  await cartStore.update(row.id, value, null)
}

// 全选 / 反选
async function toggleAll(value) {
  const checked = value ? 1 : 0
  for (const item of cartStore.cartList) {
    if (Number(item.checked) !== checked) {
      await cartStore.update(item.id, null, checked)
    }
  }
}

// 删除购物车项
async function handleDelete(id) {
  await cartStore.remove(id)
  ElMessage.success('删除成功')
}

// 跳转到结算页（要求至少选中一件商品）
function handleCheckout() {
  if (cartStore.checkedCount === 0) {
    ElMessage.warning('请先选择要结算的商品')
    return
  }
  router.push('/checkout')
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

h1 {
  margin: 0 0 22px;
  font-size: 26px;
  color: #1a1b3a;
  font-family: 'STKaiti', 'KaiTi', serif;
  font-weight: 700;
}

.product-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.cover {
  width: 64px;
  height: 64px;
  border-radius: 6px;
  background: #fdf6e3;
  border: 1px solid #d4b996;
}

.cover-placeholder {
  width: 64px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c8901b;
  background: #fdf6e3;
}

.product-name {
  color: #3e2723;
}

.product-stock {
  margin-top: 4px;
  color: #8b6f47;
  font-size: 12px;
}

.price {
  color: #c0392b;
  font-weight: 600;
}

/* 中秋风格：购物车表格 */
:deep(.cart-table) {
  background: transparent;
  --el-table-row-hover-bg-color: rgba(200, 144, 27, 0.06);
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
}

:deep(.cart-table .el-table__inner-wrapper),
:deep(.cart-table .el-table__body-wrapper) {
  background: linear-gradient(135deg, #fffdf5 0%, #fff8e7 100%);
}

:deep(.cart-table .el-table__row) {
  background: transparent !important;
}

:deep(.cart-table .el-table__row:hover > td.el-table__cell) {
  background: rgba(200, 144, 27, 0.06) !important;
}

:deep(.cart-table .el-table__cell) {
  border-bottom: 1px dashed #d4b996;
  background: transparent;
}

:deep(.cart-table .el-table__header-wrapper th) {
  background: linear-gradient(90deg, #fff8e7 0%, #fdf6e3 100%) !important;
  color: #5d4e37;
  font-weight: 700;
}

:deep(.cart-table .el-table__border-left-patch) {
  background: #d4b996;
}

:deep(.cart-table::before) {
  background: #d4b996;
}

.cart-footer {
  margin-top: 16px;
  padding: 18px 20px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 20px;
  background: linear-gradient(135deg, #fffdf5 0%, #fff8e7 100%);
  border: 1.5px solid #d4b996;
  border-radius: 10px;
  box-shadow: 0 6px 20px rgba(200, 144, 27, 0.12);
}

.total-price {
  color: #c0392b;
  font-size: 22px;
  font-family: 'STKaiti', 'KaiTi', serif;
}

/* 中秋风格：去结算按钮改为中秋红 */
:deep(.cart-footer .el-button--danger) {
  background: #c0392b;
  border-color: #c0392b;
}

:deep(.cart-footer .el-button--danger:hover) {
  background: #a93226;
  border-color: #a93226;
}

@media (max-width: 767px) {
  .container {
    padding: 14px;
  }

  .cart-footer {
    align-items: flex-start;
    flex-direction: column;
    gap: 10px;
  }
}
</style>
