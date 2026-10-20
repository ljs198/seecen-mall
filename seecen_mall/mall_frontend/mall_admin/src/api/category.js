import request from '../utils/request'

/** 查询分类树 */
export const getCategories = () => request.get('/admin/categories')

/** 新增分类 */
export const createCategory = (data) => request.post('/admin/categories', data)

/** 编辑分类 */
export const updateCategory = (id, data) => request.put(`/admin/categories/${id}`, data)

/** 删除分类 */
export const deleteCategory = (id) => request.delete(`/admin/categories/${id}`)
