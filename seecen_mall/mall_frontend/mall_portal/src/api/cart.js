import request from '../utils/request'

/**
 * 用户端购物车 API
 *
 * 全部需要登录（JWT 拦截器自动加 Authorization）。
 */

export const getCartList = () => request.get('/portal/cart')

export const addToCart = (data) => request.post('/portal/cart/add', data)

export const updateCartItem = (data) => request.put('/portal/cart/update', data)

export const deleteCartItem = (id) => request.delete(`/portal/cart/delete/${id}`)
