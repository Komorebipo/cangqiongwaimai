const BASE_URL = 'http://localhost:10086'

export const request = (options) => {
  console.log('请求开始:', options.url, options.data)
  
  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        ...options.header
      },
      success: (res) => {
        console.log('请求成功:', res.statusCode, res.data)
        
        if (res.statusCode === 200) {
          if (res.data.code === 200) {
            resolve(res.data)
          } else {
            uni.showToast({
              title: res.data.message || '请求失败',
              icon: 'none'
            })
            reject(res.data)
          }
        } else {
          uni.showToast({
            title: '网络错误',
            icon: 'none'
          })
          reject(res)
        }
      },
      fail: (err) => {
        console.error('请求失败:', err)
        uni.showToast({
          title: '网络请求失败',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

export const userApi = {
  login: (data) => request({
    url: '/user/user/login',
    method: 'POST',
    data
  }),
  register: (data) => request({
    url: '/user/user/register',
    method: 'POST',
    data
  })
}

export const dishApi = {
  getList: (params) => request({
    url: '/user/dish/list',
    method: 'GET',
    data: params
  })
}

export const categoryApi = {
  getList: (params) => request({
    url: '/user/category/list',
    method: 'GET',
    data: params
  })
}

export const setmealApi = {
  getList: (params) => request({
    url: '/user/setmeal/list',
    method: 'GET',
    data: params
  })
}

export const serviceApi = {
  chat: (data) => request({
    url: '/user/service/chat',
    method: 'POST',
    data
  })
}
