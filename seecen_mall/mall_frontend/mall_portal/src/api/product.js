import request from '../utils/request'

/**
 * 用户端商品 API
 *
 * 调用的都是 /api/portal/** 公开接口，不需要登录。
 * 后端会固定只返回 status=1 的上架商品。
 */

/** 用户端分页查询商品 */
export const getProducts = (params) => request.get('/portal/products', { params })

/** 用户端查询商品详情（返回 res.data 即商品对象） */
export const getProductDetail = (id) => request.get(`/portal/products/${id}`)

/** 用户端分类树 */
export const getCategories = () => request.get('/portal/categories')
