const state = {
  userInfo: null,
  token: null
}

export const setUserInfo = (user) => {
  state.userInfo = user
  uni.setStorageSync('userInfo', JSON.stringify(user))
}

export const getUserInfo = () => {
  if (!state.userInfo) {
    const user = uni.getStorageSync('userInfo')
    if (user) {
      state.userInfo = JSON.parse(user)
    }
  }
  return state.userInfo
}

export const clearUserInfo = () => {
  state.userInfo = null
  state.token = null
  uni.removeStorageSync('userInfo')
}

export const isLoggedIn = () => {
  return !!getUserInfo()
}
