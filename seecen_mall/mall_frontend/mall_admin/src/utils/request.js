import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

/**
 * Axios 封装（管理后台）
 * 请求拦截器：自动加 Authorization: Bearer <token>
 * 响应拦截器：统一处理后端 Result(code,message,data)
 */
// 开发环境走 vite proxy（baseURL 为 /api），生产环境拼接 .env.production 里的后端域名
const API_BASE = import.meta.env.VITE_API_BASE_URL || ''
const request = axios.create({ baseURL: API_BASE + '/api', timeout: 10000 })

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) config.headers.Authorization = `Bearer ${token}`
    return config
  },
  (error) => Promise.reject(error)
)

request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res && res.code === 200) return res
    if (res && res.code === 401) {
      ElMessage.warning(res.message || '请先登录')
      localStorage.removeItem('token'); localStorage.removeItem('userInfo')
      router.push('/login')
      return Promise.reject(new Error(res.message || '未登录'))
    }
    if (res && res.code === 403) {
      ElMessage.error(res.message || '无权限访问')
      return Promise.reject(new Error(res.message || '无权限'))
    }
    ElMessage.error(res?.message || '请求失败')
    return Promise.reject(new Error(res?.message || '请求失败'))
  },
  (error) => {
    if (error.response?.status === 401) {
      ElMessage.warning('请先登录')
      localStorage.removeItem('token'); localStorage.removeItem('userInfo')
      router.push('/login')
    } else {
      ElMessage.error(error.message || '网络异常')
    }
    return Promise.reject(error)
  }
)

export default request