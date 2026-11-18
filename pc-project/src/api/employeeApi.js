// 引入封装好的axios  @等价于/src
import http from '@/http/index.js';

/**
 * 员工登录
 */
export const loginApi = (data) => {
    return http.post('/employee/login', data)
}

// 获取当前登录验证码
export const getCaptchaApi = (data) => {
    return http.get('/employee/captcha')
}

// 修改密码
export const updatePasswordApi = (data) => {
    return http.put("/employee/updatePassword", data)
}

/**
 * 新增员工
 */
export const saveEmployeeApi = (data) => {
    return http.post('/employee', data)
}

/**
 * 分页查询员工
 */
export const getEmployeePageApi = (params) => {
    return http.get('/employee/page', { params })
}

/**
 * 根据id查询员工
 */
export const getEmployeeByIdApi = (id) => {
    return http.get(`/employee/${id}`)
}

/**
 * 修改员工信息
 */
export const updateEmployeeApi = (data) => {
    return http.put('/employee', data)
}

/**
 * 删除员工
 */
export const deleteEmployeeApi = (id) => {
    return http.delete(`/employee/${id}`)
}

/**
 * 启用/禁用员工账号
 */
export const updateEmployeeStatusApi = (status, id) => {
    return http.post(`/employee/status/${status}/${id}`)
}