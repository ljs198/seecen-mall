// 图片地址拼接：/api/ 开头的相对路径在生产环境要指向后端服务，开发环境走 vite 代理
const API_BASE = import.meta.env.VITE_API_BASE_URL || ''

export function fullImageUrl(path) {
  if (!path) return '/placeholder.svg'
  if (/^https?:\/\//.test(path)) return path
  if (path.startsWith('/api/')) return API_BASE + path
  return path
}
