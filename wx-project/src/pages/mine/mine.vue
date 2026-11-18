<template>
  <view class="container">
    <view class="header">
      <view class="user-info">
        <image 
          :src="getAvatarUrl(userInfo?.avatar)" 
          class="avatar" 
          mode="aspectFill" 
          @error="handleImageError"
        />
        <view class="user-detail">
          <text class="user-name">{{ userInfo?.name || '点击登录' }}</text>
          <text class="user-phone">{{ userInfo?.phone || '' }}</text>
        </view>
      </view>
      <view 
        v-if="!isLoggedIn" 
        class="login-btn"
        @tap="goToLogin"
      >
        <text>登录</text>
      </view>
      <view 
        v-else 
        class="logout-btn"
        @tap="handleLogout"
      >
        <text>退出</text>
      </view>
    </view>

    <view class="order-stats">
      <view class="stats-item" @tap="goToOrder('pending_payment')">
        <text class="stats-count">0</text>
        <text class="stats-label">待付款</text>
      </view>
      <view class="stats-item" @tap="goToOrder('pending_receive')">
        <text class="stats-count">0</text>
        <text class="stats-label">待收货</text>
      </view>
      <view class="stats-item" @tap="goToOrder('completed')">
        <text class="stats-count">0</text>
        <text class="stats-label">已完成</text>
      </view>
      <view class="stats-item" @tap="goToOrder('cancelled')">
        <text class="stats-count">0</text>
        <text class="stats-label">已取消</text>
      </view>
    </view>

    <view class="menu-list">
      <view class="menu-item" @tap="goToUserInfo">
        <text class="menu-icon">👤</text>
        <text class="menu-text">个人信息</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @tap="goToUpdatePassword">
        <text class="menu-icon">🔒</text>
        <text class="menu-text">修改密码</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @tap="goToAddress">
        <text class="menu-icon">📍</text>
        <text class="menu-text">收货地址</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @tap="goToFavorite">
        <text class="menu-icon">❤️</text>
        <text class="menu-text">我的收藏</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @tap="goToCoupon">
        <text class="menu-icon">🎫</text>
        <text class="menu-text">优惠券</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @tap="goToHelp">
        <text class="menu-icon">❓</text>
        <text class="menu-text">帮助中心</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'

const userInfo = ref(null)
const isLoggedIn = ref(false)

const loadUserInfo = () => {
  const user = uni.getStorageSync('user')
  console.log('读取用户信息:', JSON.stringify(user))
  if (user && user.id) {
    userInfo.value = user
    isLoggedIn.value = true
    console.log('头像路径:', user.avatar)
    console.log('处理后的头像URL:', getAvatarUrl(user.avatar))
  } else {
    userInfo.value = null
    isLoggedIn.value = false
  }
}

const getAvatarUrl = (avatar) => {
  if (!avatar) {
    return '/static/logo.png'
  }
  if (typeof avatar === 'string' && avatar.startsWith('http')) {
    return avatar
  }
  return 'http://localhost:10086' + avatar
}

const handleImageError = (e) => {
  console.error('头像加载失败:', e)
  e.target.src = '/static/logo.png'
}

const goToLogin = () => {
  uni.navigateTo({
    url: '/pages/login/login'
  })
}

const handleLogout = () => {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        uni.clearStorageSync()
        userInfo.value = null
        isLoggedIn.value = false
        uni.showToast({
          title: '退出成功',
          icon: 'success'
        })
      }
    }
  })
}

const goToOrder = (status) => {
  uni.showToast({
    title: `查看${status}订单`,
    icon: 'none'
  })
}

const goToUserInfo = () => {
  if (!isLoggedIn.value) {
    uni.showToast({
      title: '请先登录',
      icon: 'none'
    })
    return
  }
  uni.navigateTo({
    url: '/pages/userInfo/userInfo'
  })
}

const goToUpdatePassword = () => {
  if (!isLoggedIn.value) {
    uni.showToast({
      title: '请先登录',
      icon: 'none'
    })
    return
  }
  uni.navigateTo({
    url: '/pages/updatePassword/updatePassword'
  })
}

const goToAddress = () => {
  uni.showToast({
    title: '收货地址',
    icon: 'none'
  })
}

const goToFavorite = () => {
  uni.showToast({
    title: '我的收藏',
    icon: 'none'
  })
}

const goToCoupon = () => {
  uni.showToast({
    title: '优惠券',
    icon: 'none'
  })
}

const goToHelp = () => {
  uni.showToast({
    title: '帮助中心',
    icon: 'none'
  })
}

onShow(() => {
  loadUserInfo()
})
</script>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  background: linear-gradient(135deg, #4ec3e6 0%, #8bbfff 100%);
  padding: 60rpx 30rpx 40rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.user-info {
  display: flex;
  align-items: center;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  border: 4rpx solid rgba(255, 255, 255, 0.5);
}

.user-detail {
  margin-left: 24rpx;
}

.user-name {
  font-size: 36rpx;
  font-weight: bold;
  color: #ffffff;
  display: block;
}

.user-phone {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 8rpx;
  display: block;
}

.login-btn {
  padding: 16rpx 32rpx;
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 40rpx;
  
  text {
    font-size: 28rpx;
    color: #ffffff;
  }
}

.logout-btn {
  padding: 16rpx 32rpx;
  background-color: rgba(255, 255, 255, 0.2);
  border-radius: 40rpx;
  
  text {
    font-size: 28rpx;
    color: #ffffff;
  }
}

.order-stats {
  display: flex;
  background-color: #ffffff;
  margin: -40rpx 20rpx 20rpx;
  border-radius: 20rpx;
  padding: 40rpx 0;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
}

.stats-item {
  flex: 1;
  text-align: center;
}

.stats-count {
  font-size: 40rpx;
  font-weight: bold;
  color: #333333;
  display: block;
}

.stats-label {
  font-size: 24rpx;
  color: #999999;
  margin-top: 8rpx;
  display: block;
}

.menu-list {
  background-color: #ffffff;
  margin: 0 20rpx;
  border-radius: 20rpx;
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 30rpx 24rpx;
  border-bottom: 2rpx solid #f5f5f5;
  
  &:last-child {
    border-bottom: none;
  }
}

.menu-icon {
  font-size: 36rpx;
  margin-right: 20rpx;
}

.menu-text {
  flex: 1;
  font-size: 30rpx;
  color: #333333;
}

.menu-arrow {
  font-size: 36rpx;
  color: #cccccc;
}
</style>
