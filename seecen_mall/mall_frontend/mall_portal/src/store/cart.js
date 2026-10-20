import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import {
  addToCart,
  deleteCartItem,
  getCartList,
  updateCartItem
} from '../api/cart'

/**
 * 用户端购物车 Store
 *
 * - cartList 当前用户购物车列表（含商品展示字段）
 * - cartCount 总件数，用于头部角标
 * - checkedItems / checkedAmount 用于购物车页合计金额
 */
export const useCartStore = defineStore('cart', () => {
  const cartList = ref([])
  // 数量角标先从 localStorage 恢复，避免刷新瞬间变 0
  const cartCount = ref(Number(localStorage.getItem('cartCount')) || 0)

  const checkedItems = computed(() =>
    cartList.value.filter((item) => Number(item.checked) === 1)
  )
  const checkedCount = computed(() =>
    checkedItems.value.reduce((sum, item) => sum + Number(item.quantity || 0), 0)
  )
  const checkedAmount = computed(() =>
    checkedItems.value.reduce(
      (sum, item) =>
        sum + Number(item.productPrice || 0) * Number(item.quantity || 0),
      0
    )
  )

  /** 加载购物车列表并刷新数量角标 */
  async function fetchCart() {
    const res = await getCartList()
    cartList.value = (res && res.data) || []
    syncCartCount()
  }

  /** 加入购物车 */
  async function add(productId, quantity) {
    await addToCart({ productId, quantity })
    await fetchCart()
  }

  /** 更新数量或勾选状态 */
  async function update(id, quantity, checked) {
    await updateCartItem({ id, quantity, checked })
    await fetchCart()
  }

  /** 删除购物车项 */
  async function remove(id) {
    await deleteCartItem(id)
    await fetchCart()
  }

  /**
   * 清空本地购物车状态（用于订单创建后）
   * 后端在创建订单时已经清掉对应购物车项，这里只重置前端状态
   */
  function resetCart() {
    cartList.value = []
    cartCount.value = 0
    localStorage.setItem('cartCount', '0')
  }

  /** 只刷新角标（头部使用） */
  async function refreshCartCount() {
    const res = await getCartList()
    cartList.value = (res && res.data) || []
    syncCartCount()
  }

  /** 计算总件数，写入本地缓存 */
  function syncCartCount() {
    const total = cartList.value.reduce(
      (sum, item) => sum + Number(item.quantity || 0),
      0
    )
    cartCount.value = total
    localStorage.setItem('cartCount', String(total))
  }

  return {
    cartList,
    cartCount,
    checkedItems,
    checkedCount,
    checkedAmount,
    fetchCart,
    add,
    update,
    remove,
    refreshCartCount,
    resetCart
  }
})
