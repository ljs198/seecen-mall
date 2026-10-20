import request from '../utils/request'

/**
 * 后台仪表盘 API
 */

/** 获取仪表盘统计 */
export const getDashboard = () => request.get('/admin/dashboard')
