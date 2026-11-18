// 引入封装好的axios  @等价于/src
import http from '@/http/index.js';
//分类管理

// 查询分类列表接口
export const getCategoryPage = (data) => {
    return http.get('/category/page', { params: data })
}

// 删除当前列的接口
export const deleteCategory = (data) => {
    return http.delete(`/category/${data}`)
}

// 修改接口
export const editCategory = (data) => {
    return http.put('/category', data)
}  

// 新增接口
export const addCategory = (data) => {
    return http.post('/category', data)
} 

// 修改---启用禁用接口
export const enableOrDisableCategory = (data) => {
    return http.post(`/category/status/${data.status}/${data.id}`)
} 

// 根据类型查询分类
export const getCategoryList = (data) => {
    return http.get('/category/list', { params: data })
} 