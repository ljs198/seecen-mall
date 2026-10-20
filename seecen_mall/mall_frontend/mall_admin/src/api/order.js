import request from '../utils/request'

/**
 * 后台订单 API
 *
 * 后端响应是 Result 包装对象（{code, data}），调用方需从 res.data 取数据。
 */

/** 分页查询订单列表 */
export const getOrders = (params) => request.get('/admin/orders', { params })

/** 查询订单详情 */
export const getOrderDetail = (id) => request.get(`/admin/orders/${id}`)

/** 查询订单统计 */
export const getOrderStats = () => request.get('/admin/orders/stats')
