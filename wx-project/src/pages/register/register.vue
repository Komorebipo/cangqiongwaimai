<template>
  <view class="container">
    <view class="header">
      <text class="back-btn" @tap="goBack">‹</text>
      <text class="title">用户注册</text>
      <view class="placeholder"></view>
    </view>

    <view class="form-section">
      <view class="form-item">
        <text class="form-icon">👤</text>
        <input 
          type="text" 
          v-model="form.username" 
          placeholder="请输入用户名" 
          class="form-input"
        />
      </view>
      
      <view class="form-item">
        <text class="form-icon">📱</text>
        <input 
          type="number" 
          v-model="form.phone" 
          placeholder="请输入手机号" 
          class="form-input"
        />
      </view>

      <view class="form-item">
        <text class="form-icon">🔒</text>
        <input 
          type="password" 
          v-model="form.password" 
          placeholder="请输入密码" 
          class="form-input"
        />
      </view>

      <view class="form-item">
        <text class="form-icon">🔒</text>
        <input 
          type="password" 
          v-model="form.confirmPassword" 
          placeholder="请确认密码" 
          class="form-input"
        />
      </view>

      <view class="register-btn" @tap="handleRegister">
        <text>注册</text>
      </view>
    </view>
  </view>
</template>

<script>
import { ref } from 'vue'
import { userApi } from '@/utils/api'

export default {
  setup() {
    const form = ref({
      username: '',
      phone: '',
      password: '',
      confirmPassword: ''
    })

    const goBack = () => {
      uni.navigateBack()
    }

    const handleRegister = async () => {
      if (!form.value.username) {
        uni.showToast({ title: '请输入用户名', icon: 'none' })
        return
      }
      if (!form.value.phone) {
        uni.showToast({ title: '请输入手机号', icon: 'none' })
        return
      }
      if (!form.value.password) {
        uni.showToast({ title: '请输入密码', icon: 'none' })
        return
      }
      if (form.value.password !== form.value.confirmPassword) {
        uni.showToast({ title: '两次输入的密码不一致', icon: 'none' })
        return
      }
      
      try {
        const res = await userApi.register({
          name: form.value.username,
          phone: form.value.phone,
          password: form.value.password
        })
        
        if (res.code === 200) {
          uni.showToast({
            title: '注册成功',
            icon: 'success'
          })
          setTimeout(() => {
            uni.navigateBack()
          }, 1500)
        }
      } catch (error) {
        console.error('注册失败:', error)
      }
    }

    return {
      form,
      goBack,
      handleRegister
    }
  }
}
</script>

<style lang="scss">
.container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 30rpx;
  background-color: #ffffff;
}

.back-btn {
  font-size: 56rpx;
  color: #333333;
  line-height: 1;
}

.title {
  font-size: 34rpx;
  font-weight: bold;
  color: #333333;
}

.placeholder {
  width: 60rpx;
}

.form-section {
  margin: 30rpx;
  background-color: #ffffff;
  border-radius: 30rpx;
  padding: 40rpx;
}

.form-item {
  display: flex;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 2rpx solid #f0f0f0;
}

.form-icon {
  font-size: 36rpx;
  margin-right: 20rpx;
}

.form-input {
  flex: 1;
  font-size: 30rpx;
  padding: 16rpx 0;
}

.register-btn {
  margin-top: 40rpx;
  padding: 28rpx;
  background: linear-gradient(135deg, #b4e3e3 0%, #8bbfff 100%);
  border-radius: 40rpx;
  text-align: center;
  
  text {
    font-size: 32rpx;
    color: #ffffff;
    font-weight: bold;
  }
}
</style>
