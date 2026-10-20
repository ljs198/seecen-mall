const API_BASE = import.meta.env.VITE_API_BASE_URL || ''
// 站点部署在 GitHub Pages 的 /seecen-mall/ 子路径下，
// 站内静态资源（占位图等）必须带上 base 前缀才能正确加载
const SITE_BASE = import.meta.env.BASE_URL || '/'

/**
 * 把后端返回的图片路径转成可直接访问的完整地址
 * - 空值：返回占位图（带站点 base 前缀，避免部署到子路径后 404）
 * - http(s) 开头：原样返回
 * - /api/ 开头：拼接后端域名（生产环境商品图片由 Railway 后端提供）
 * - 其他相对路径：原样返回
 */
export function fullImageUrl(path) {
  if (!path) return SITE_BASE + 'placeholder.svg'
  if (/^https?:\/\//.test(path)) return path
  if (path.startsWith('/api/')) return API_BASE + path
  return path
}
