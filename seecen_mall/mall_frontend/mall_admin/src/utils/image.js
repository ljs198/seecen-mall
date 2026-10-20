/**
 * 管理端图片地址解析
 * 数据库存的是 /api/uploads/... 相对路径：
 *   - 开发环境：走 vite proxy 由后端返回图片
 *   - 生产环境：图片随用户端构建托管在站点根 uploads/ 下
 *     （管理端部署在 /seecen-mall/admin/，图片实际位于 /seecen-mall/uploads/）
 */
export function fullImageUrl(path) {
  if (!path) return ''
  if (/^https?:\/\//.test(path)) return path
  if (import.meta.env.DEV) return path
  if (path.startsWith('/api/uploads/')) {
    // BASE_URL 形如 /seecen-mall/admin/，去掉末尾 admin/ 得到站点根
    const siteBase = import.meta.env.BASE_URL.replace(/admin\/$/, '')
    return siteBase + path.slice(4) // 去掉开头的 '/api'
  }
  return path
}

/** 上传接口完整地址：开发走 vite proxy，生产直连后端域名 */
export const uploadUrl = (import.meta.env.VITE_API_BASE_URL || '') + '/api/admin/upload/image'
