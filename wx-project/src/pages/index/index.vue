<template>
  <view class="container">
    <view class="search-bar">
      <view class="search-input">
        <text class="search-icon">🔍</text>
        <input 
          type="text" 
          v-model="searchText" 
          placeholder="搜索菜品" 
          class="input"
          @confirm="handleSearch"
        />
      </view>
    </view>

    <swiper class="swiper" autoplay indicator-dots circular>
      <swiper-item v-for="(item, index) in swiperList" :key="index">
        <image :src="item" class="swiper-image" mode="aspectFill" />
      </swiper-item>
    </swiper>

    <scroll-view scroll-x class="category-scroll">
      <view class="category-list">
        <view 
          class="category-item" 
          :class="{ active: currentCategory === -1 }"
          @tap="selectCategory(-1)"
        >
          全部
        </view>
        <view 
          v-for="category in categoryList" 
          :key="category.id"
          class="category-item"
          :class="{ active: currentCategory === category.id }"
          @tap="selectCategory(category.id)"
        >
          {{ category.name }}
        </view>
      </view>
    </scroll-view>

    <view class="dish-list">
      <view 
        v-for="dish in dishList" 
        :key="dish.id" 
        class="dish-item"
        @tap="goToDetail(dish)"
      >
        <image :src="getFullImageUrl(dish.image)" class="dish-image" mode="aspectFill" />
        <view class="dish-info">
          <text class="dish-name">{{ dish.name }}</text>
          <text class="dish-desc">{{ dish.description }}</text>
          <view class="dish-bottom">
            <text class="dish-price">¥{{ dish.price }}</text>
            <view class="add-btn" @tap.stop="addToCart(dish)">
              <text>+</text>
            </view>
          </view>
        </view>
      </view>
      
      <view v-if="dishList.length === 0" class="empty">
        <text class="empty-icon">🍽️</text>
        <text class="empty-text">暂无菜品</text>
        <text class="empty-hint">请检查后端服务是否启动</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { dishApi, categoryApi } from '@/utils/api'

const baseUrl = 'http://localhost:10086'

const searchText = ref('')
const currentCategory = ref(-1)
const swiperList = ref([
  '/static/img2.jpeg',
  '/static/img3.jpeg',
  '/static/img4.jpeg',
  '/static/img5.jpeg',
  '/static/img6.jpeg'
])
const categoryList = ref([])
const dishList = ref([])

const getFullImageUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return baseUrl + path
}

const loadCategories = async () => {
  try {
    const res = await categoryApi.getList({ type: 1 })
    if (res.data) {
      categoryList.value = res.data
    }
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

const loadDishes = async () => {
  try {
    const params = {
      categoryId: currentCategory.value === -1 ? '' : currentCategory.value
    }
    const res = await dishApi.getList(params)
    if (res.data) {
      dishList.value = res.data
    }
  } catch (error) {
    console.error('获取菜品失败:', error)
  }
}

const selectCategory = (categoryId) => {
  currentCategory.value = categoryId
  loadDishes()
}

const handleSearch = () => {
  console.log('搜索:', searchText.value)
}

const goToDetail = (dish) => {
  uni.navigateTo({
    url: `/pages/dish/dish?id=${dish.id}`
  })
}

const addToCart = (dish) => {
  uni.showToast({
    title: '已加入购物车',
    icon: 'success'
  })
}

onMounted(() => {
  loadCategories()
  loadDishes()
})
</script>

<style lang="scss">
.container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 100rpx;
}

.search-bar {
  padding: 20rpx;
  background-color: #ffffff;
}

.search-input {
  display: flex;
  align-items: center;
  background-color: #f5f5f5;
  border-radius: 40rpx;
  padding: 16rpx 24rpx;
}

.search-icon {
  font-size: 28rpx;
  margin-right: 16rpx;
}

.input {
  flex: 1;
  font-size: 28rpx;
  background: transparent;
}

.swiper {
  height: 300rpx;
  margin: 0 20rpx;
  border-radius: 20rpx;
  overflow: hidden;
}

.swiper-image {
  width: 100%;
  height: 100%;
}

.category-scroll {
  white-space: nowrap;
  background-color: #ffffff;
  padding: 20rpx 0;
  margin-top: 20rpx;
}

.category-list {
  display: inline-flex;
  padding: 0 20rpx;
}

.category-item {
  padding: 12rpx 32rpx;
  border-radius: 30rpx;
  background-color: #f5f5f5;
  font-size: 26rpx;
  margin-right: 20rpx;
  color: #666666;
  
  &.active {
    background-color: #b4e3e3;
    color: #ffffff;
  }
}

.dish-list {
  padding: 20rpx;
}

.dish-item {
  display: flex;
  background-color: #ffffff;
  border-radius: 20rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.dish-image {
  width: 200rpx;
  height: 200rpx;
  border-radius: 16rpx;
}

.dish-info {
  flex: 1;
  margin-left: 20rpx;
  display: flex;
  flex-direction: column;
}

.dish-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
}

.dish-desc {
  font-size: 24rpx;
  color: #999999;
  margin-top: 12rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dish-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: auto;
}

.dish-price {
  font-size: 32rpx;
  color: #ff6b6b;
  font-weight: bold;
}

.add-btn {
  width: 56rpx;
  height: 56rpx;
  background-color: #b4e3e3;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  
  text {
    font-size: 36rpx;
    color: #ffffff;
    font-weight: bold;
  }
}

.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100rpx 0;
}

.empty-icon {
  font-size: 100rpx;
  margin-bottom: 20rpx;
}

.empty-text {
  font-size: 32rpx;
  color: #666;
  margin-bottom: 10rpx;
}

.empty-hint {
  font-size: 24rpx;
  color: #999;
}
</style>
