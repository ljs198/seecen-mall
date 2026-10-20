import request from '../utils/request'

/**
 * 后台用户 API
 */

/** 分页查询用户列表 */
export const getUsers = (params) => request.get('/admin/users', { params })

/** 切换用户启用状态 */
export const toggleUserStatus = (id) => request.put(`/admin/users/${id}/status`)
