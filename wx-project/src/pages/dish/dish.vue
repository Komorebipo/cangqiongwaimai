<template>
  <view class="container">
    <image :src="dish.image" class="dish-header-image" mode="aspectFill" />
    
    <view class="dish-info">
      <view class="dish-name-row">
        <text class="dish-name">{{ dish.name }}</text>
        <view class="dish-status" :class="{ active: dish.status === 1 }">
          {{ dish.status === 1 ? '在售' : '停售' }}
        </view>
      </view>
      <text class="dish-price">¥{{ dish.price }}</text>
      <text class="dish-desc">{{ dish.description }}</text>
    </view>

    <view class="dish-properties">
      <view class="property-item">
        <text class="property-label">所属分类</text>
        <text class="property-value">{{ categoryName }}</text>
      </view>
      <view class="property-item">
        <text class="property-label">口味</text>
        <text class="property-value">{{ dish.flavor || '未设置' }}</text>
      </view>
      <view class="property-item">
        <text class="property-label">规格</text>
        <text class="property-value">{{ dish.specification || '未设置' }}</text>
      </view>
    </view>

    <view class="bottom-bar">
      <view class="bar-left">
        <view class="bar-item" @tap="goToFavorite">
          <text class="bar-icon">❤️</text>
          <text class="bar-text">收藏</text>
        </view>
        <view class="bar-item" @tap="goToShare">
          <text class="bar-icon">📤</text>
          <text class="bar-text">分享</text>
        </view>
      </view>
      <view class="bar-right">
        <view class="add-cart-btn" @tap="addToCart">
          <text>加入购物车</text>
        </view>
        <view class="buy-now-btn" @tap="buyNow">
          <text>立即购买</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { ref, onMounted } from 'vue'
import { dishApi, categoryApi } from '@/utils/api'

export default {
  setup() {
    const dish = ref({
      id: '',
      name: '',
      price: 0,
      image: '',
      description: '',
      status: 1,
      categoryId: '',
      flavor: '',
      specification: ''
    })

    const categoryName = ref('')

    const loadDishDetail = async (dishId) => {
      try {
        const res = await dishApi.getList({})
        if (res.data) {
          const foundDish = res.data.find(d => d.id === parseInt(dishId))
          if (foundDish) {
            dish.value = foundDish
            loadCategoryName(foundDish.categoryId)
          }
        }
      } catch (error) {
        console.error('获取菜品详情失败:', error)
      }
    }

    const loadCategoryName = async (categoryId) => {
      try {
        const res = await categoryApi.getList({ type: 1 })
        if (res.data) {
          const category = res.data.find(c => c.id === categoryId)
          if (category) {
            categoryName.value = category.name
          }
        }
      } catch (error) {
        console.error('获取分类名称失败:', error)
      }
    }

    const goToFavorite = () => {
      uni.showToast({ title: '已收藏', icon: 'success' })
    }

    const goToShare = () => {
      uni.showToast({ title: '分享功能开发中', icon: 'none' })
    }

    const addToCart = () => {
      uni.showToast({ title: '已加入购物车', icon: 'success' })
    }

    const buyNow = () => {
      uni.showToast({ title: '立即购买功能开发中', icon: 'none' })
    }

    onMounted(() => {
      const pages = getCurrentPages()
      const currentPage = pages[pages.length - 1]
      const options = currentPage.$page?.options || {}
      
      if (options.id) {
        loadDishDetail(options.id)
      }
    })

    return {
      dish,
      categoryName,
      goToFavorite,
      goToShare,
      addToCart,
      buyNow
    }
  }
}
</script>

<style lang="scss">
.container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 140rpx;
}

.dish-header-image {
  width: 100%;
  height: 500rpx;
}

.dish-info {
  background-color: #ffffff;
  padding: 30rpx;
  margin-top: -40rpx;
  border-radius: 30rpx 30rpx 0 0;
  position: relative;
}

.dish-name-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.dish-name {
  font-size: 40rpx;
  font-weight: bold;
  color: #333333;
}

.dish-status {
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
  background-color: #f0f0f0;
  color: #999999;
  
  &.active {
    background-color: #e8f5e9;
    color: #4caf50;
  }
}

.dish-price {
  font-size: 48rpx;
  color: #ff6b6b;
  font-weight: bold;
  margin-top: 20rpx;
  display: block;
}

.dish-desc {
  font-size: 28rpx;
  color: #666666;
  margin-top: 16rpx;
  display: block;
  line-height: 1.6;
}

.dish-properties {
  background-color: #ffffff;
  margin-top: 20rpx;
  padding: 20rpx 30rpx;
}

.property-item {
  display: flex;
  padding: 20rpx 0;
  border-bottom: 2rpx solid #f5f5f5;
  
  &:last-child {
    border-bottom: none;
  }
}

.property-label {
  width: 140rpx;
  font-size: 28rpx;
  color: #999999;
}

.property-value {
  flex: 1;
  font-size: 28rpx;
  color: #333333;
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  background-color: #ffffff;
  padding: 20rpx 30rpx;
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.1);
}

.bar-left {
  display: flex;
}

.bar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-right: 40rpx;
}

.bar-icon {
  font-size: 40rpx;
}

.bar-text {
  font-size: 22rpx;
  color: #666666;
  margin-top: 8rpx;
}

.bar-right {
  flex: 1;
  display: flex;
  gap: 16rpx;
  margin-left: 20rpx;
}

.add-cart-btn {
  flex: 1;
  padding: 24rpx;
  background-color: #ffb347;
  border-radius: 12rpx;
  text-align: center;
  
  text {
    font-size: 30rpx;
    color: #ffffff;
    font-weight: bold;
  }
}

.buy-now-btn {
  flex: 1;
  padding: 24rpx;
  background-color: #b4e3e3;
  border-radius: 12rpx;
  text-align: center;
  
  text {
    font-size: 30rpx;
    color: #ffffff;
    font-weight: bold;
  }
}
</style>
