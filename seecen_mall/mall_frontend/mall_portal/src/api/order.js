import request from '../utils/request'

/**
 * 用户端订单 API
 *
 * 全部需要登录。注意：request 已经有 baseURL='/api'，URL 不带 /api 前缀。
 *
 * 后端响应是 Result 包装对象（{code, data}），调用方需从 res.data 取数据。
 */

/** 从购物车项创建订单 */
export const createOrder = (data) => request.post('/portal/orders', data)

/** 模拟支付订单 */
export const payOrder = (id) => request.post(`/portal/orders/${id}/pay`)

/** 取消待支付订单 */
export const cancelOrder = (id) => request.post(`/portal/orders/${id}/cancel`)

/** 分页查询当前用户订单列表 */
export const getOrderList = (params) => request.get('/portal/orders', { params })

/** 查询订单详情 */
export const getOrderDetail = (id) => request.get(`/portal/orders/${id}`)
