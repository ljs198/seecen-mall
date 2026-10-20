const API_BASE = import.meta.env.VITE_API_BASE_URL || ''
// 站点部署在 GitHub Pages 的 /seecen-mall/ 子路径下，
// 站内静态资源（占位图等）必须带上 base 前缀才能正确加载
const SITE_BASE = import.meta.env.BASE_URL || '/'

/**
 * 把后端返回的图片路径转成可直接访问的完整地址
 * - 空值：返回占位图（带站点 base 前缀，避免部署到子路径后 404）
 * - http(s) 开头：原样返回
 * - /api/uploads/ 开头：商品图片已随前端静态托管（public/uploads/），
 *   改走站点自身域名，不再依赖后端域名（跨洋访问更稳定）
 * - 其他 /api/ 开头：拼接后端域名（真正的接口请求）
 * - 其他相对路径：原样返回
 */
export function fullImageUrl(path) {
  if (!path) return SITE_BASE + 'placeholder.svg'
  if (/^https?:\/\//.test(path)) return path
  // slice(5) 去掉 '/api/' 前缀（含尾斜杠），避免拼接出 // 双斜杠
  if (path.startsWith('/api/uploads/')) return SITE_BASE + path.slice(5)
  if (path.startsWith('/api/')) return API_BASE + path
  return path
}
