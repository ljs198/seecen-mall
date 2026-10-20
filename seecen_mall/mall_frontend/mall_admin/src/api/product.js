import request from '../utils/request'

/** 分页查询商品 */
export const getProducts = (params) => request.get('/admin/products', { params })

/** 查询商品详情 */
export const getProduct = (id) => request.get(`/admin/products/${id}`)

/** 新增商品 */
export const createProduct = (data) => request.post('/admin/products', data)

/** 更新商品 */
export const updateProduct = (id, data) => request.put(`/admin/products/${id}`, data)

/** 切换商品上下架状态 */
export const toggleProductStatus = (id) => request.put(`/admin/products/${id}/status`)
