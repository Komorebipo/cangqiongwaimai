<template>
  <view class="container">
    <scroll-view 
      scroll-y 
      class="message-scroll"
      :scroll-into-view="scrollToId"
      scroll-with-animation
    >
      <view class="message-list">
        <view class="welcome-message">
          <text class="welcome-text">您好！我是智能客服，有什么可以帮您的？</text>
        </view>
        
        <view 
          v-for="(msg, index) in messageList" 
          :key="index"
          :id="'msg-' + index"
          class="message-item"
          :class="{ 'user-message': msg.isUser, 'bot-message': !msg.isUser }"
        >
          <image 
            v-if="msg.isUser" 
            src="/static/logo.png" 
            class="avatar" 
            mode="aspectFill" 
          />
          <image 
            v-else 
            src="/static/service.png" 
            class="avatar" 
            mode="aspectFill" 
          />
          <view class="message-content">
            <text class="message-text">{{ msg.content }}</text>
            <text class="message-time">{{ msg.time }}</text>
          </view>
        </view>
        
        <view v-if="isLoading" class="loading-item">
          <view class="loading-dots">
            <view class="dot"></view>
            <view class="dot"></view>
            <view class="dot"></view>
          </view>
        </view>
      </view>
    </scroll-view>

    <view class="input-bar">
      <input 
        type="text" 
        v-model="inputText" 
        placeholder="输入您的问题..." 
        class="input-field"
        @confirm="sendMessage"
      />
      <view class="send-btn" :class="{ active: inputText.trim() }" @tap="sendMessage">
        <text>发送</text>
      </view>
    </view>
  </view>
</template>

<script>
import { ref, nextTick } from 'vue'
import { serviceApi } from '@/utils/api'

export default {
  setup() {
    const inputText = ref('')
    const messageList = ref([])
    const isLoading = ref(false)
    const scrollToId = ref('')

    const getCurrentTime = () => {
      const now = new Date()
      const hours = now.getHours().toString().padStart(2, '0')
      const minutes = now.getMinutes().toString().padStart(2, '0')
      return `${hours}:${minutes}`
    }

    const sendMessage = async () => {
      if (!inputText.value.trim() || isLoading.value) return

      const userMessage = {
        content: inputText.value.trim(),
        isUser: true,
        time: getCurrentTime()
      }
      messageList.value.push(userMessage)
      inputText.value = ''

      await nextTick()
      scrollToBottom()

      isLoading.value = true
      
      try {
        const res = await serviceApi.chat({ message: userMessage.content })
        
        const botMessage = {
          content: res.data?.content || '抱歉，我暂时无法回答这个问题',
          isUser: false,
          time: getCurrentTime()
        }
        messageList.value.push(botMessage)
      } catch (error) {
        console.error('AI客服请求失败:', error)
        const errorMessage = {
          content: '网络连接失败，请稍后重试',
          isUser: false,
          time: getCurrentTime()
        }
        messageList.value.push(errorMessage)
      } finally {
        isLoading.value = false
        await nextTick()
        scrollToBottom()
      }
    }

    const scrollToBottom = () => {
      if (messageList.value.length > 0) {
        scrollToId.value = 'msg-' + (messageList.value.length - 1)
      }
    }

    return {
      inputText,
      messageList,
      isLoading,
      scrollToId,
      sendMessage
    }
  }
}
</script>

<style lang="scss">
.container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f5f5;
}

.message-scroll {
  flex: 1;
  padding: 20rpx;
}

.message-list {
  padding-bottom: 20rpx;
}

.welcome-message {
  text-align: center;
  margin: 30rpx 0;
}

.welcome-text {
  display: inline-block;
  padding: 16rpx 32rpx;
  background-color: #e8f5e9;
  border-radius: 20rpx;
  font-size: 26rpx;
  color: #4caf50;
}

.message-item {
  display: flex;
  margin-bottom: 24rpx;
  
  &.user-message {
    flex-direction: row-reverse;
    
    .message-content {
      background-color: #b4e3e3;
      border-radius: 24rpx 8rpx 24rpx 24rpx;
    }
  }
  
  &.bot-message {
    .message-content {
      background-color: #ffffff;
      border-radius: 8rpx 24rpx 24rpx 24rpx;
    }
  }
}

.avatar {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  flex-shrink: 0;
}

.message-content {
  max-width: 70%;
  padding: 20rpx 24rpx;
  margin: 0 16rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.message-text {
  font-size: 28rpx;
  color: #333333;
  line-height: 1.6;
  display: block;
}

.message-time {
  font-size: 20rpx;
  color: #999999;
  margin-top: 8rpx;
  display: block;
  text-align: right;
}

.loading-item {
  display: flex;
  justify-content: flex-start;
  padding-left: 16rpx;
}

.loading-dots {
  display: flex;
  align-items: center;
  padding: 16rpx 24rpx;
  background-color: #ffffff;
  border-radius: 20rpx;
}

.dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
  background-color: #b4e3e3;
  margin: 0 6rpx;
  animation: dotAnimation 1.4s infinite ease-in-out;
  
  &:nth-child(2) {
    animation-delay: 0.2s;
  }
  
  &:nth-child(3) {
    animation-delay: 0.4s;
  }
}

@keyframes dotAnimation {
  0%, 80%, 100% {
    transform: scale(0);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

.input-bar {
  display: flex;
  align-items: center;
  padding: 20rpx;
  background-color: #ffffff;
  border-top: 2rpx solid #f0f0f0;
}

.input-field {
  flex: 1;
  height: 80rpx;
  padding: 0 24rpx;
  background-color: #f5f5f5;
  border-radius: 40rpx;
  font-size: 28rpx;
}

.send-btn {
  width: 120rpx;
  height: 80rpx;
  background-color: #e0e0e0;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: 20rpx;
  
  &.active {
    background-color: #b4e3e3;
    
    text {
      color: #ffffff;
    }
  }
  
  text {
    font-size: 28rpx;
    color: #999999;
    font-weight: bold;
  }
}
</style>
