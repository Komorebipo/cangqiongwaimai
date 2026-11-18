<template>
  <view class="container">
    <view class="left-sidebar">
      <view 
        v-for="category in categoryList" 
        :key="category.id"
        class="sidebar-item"
        :class="{ active: currentCategory === category.id }"
        @tap="selectCategory(category)"
      >
        {{ category.name }}
      </view>
    </view>

    <view class="right-content">
      <view class="content-header">
        <text class="header-title">{{ currentCategoryName }}</text>
        <text class="header-count">共{{ dishList.length }}款</text>
      </view>
      
      <scroll-view scroll-y class="dish-scroll">
        <view class="dish-grid">
          <view 
            v-for="dish in dishList" 
            :key="dish.id" 
            class="dish-card"
            @tap="goToDetail(dish)"
          >
            <image :src="dish.image" class="dish-image" mode="aspectFill" />
            <view class="dish-info">
              <text class="dish-name">{{ dish.name }}</text>
              <text class="dish-price">¥{{ dish.price }}</text>
            </view>
          </view>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { dishApi, categoryApi } from '@/utils/api'

export default {
  setup() {
    const categoryList = ref([])
    const currentCategory = ref(null)
    
    const currentCategoryName = computed(() => {
      const category = categoryList.value.find(c => c.id === currentCategory.value)
      return category ? category.name : '请选择分类'
    })

    const dishList = ref([])

    const loadCategories = async () => {
      try {
        const res = await categoryApi.getList({ type: 1 })
        if (res.data && res.data.length > 0) {
          categoryList.value = res.data
          currentCategory.value = res.data[0].id
          loadDishes()
        }
      } catch (error) {
        console.error('获取分类失败:', error)
      }
    }

    const loadDishes = async () => {
      if (!currentCategory.value) return
      
      try {
        const params = {
          categoryId: currentCategory.value
        }
        const res = await dishApi.getList(params)
        if (res.data) {
          dishList.value = res.data
        }
      } catch (error) {
        console.error('获取菜品失败:', error)
      }
    }

    const selectCategory = (category) => {
      currentCategory.value = category.id
      loadDishes()
    }

    const goToDetail = (dish) => {
      uni.navigateTo({
        url: `/pages/dish/dish?id=${dish.id}`
      })
    }

    onMounted(() => {
      loadCategories()
    })

    return {
      categoryList,
      currentCategory,
      currentCategoryName,
      dishList,
      selectCategory,
      goToDetail
    }
  }
}
</script>

<style lang="scss">
.container {
  display: flex;
  height: 100vh;
  background-color: #f5f5f5;
}

.left-sidebar {
  width: 200rpx;
  background-color: #f8f8f8;
  overflow-y: auto;
}

.sidebar-item {
  padding: 32rpx 20rpx;
  text-align: center;
  font-size: 28rpx;
  color: #666666;
  border-left: 6rpx solid transparent;
  
  &.active {
    background-color: #ffffff;
    color: #b4e3e3;
    border-left-color: #b4e3e3;
    font-weight: bold;
  }
}

.right-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #ffffff;
}

.content-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx;
  border-bottom: 2rpx solid #f0f0f0;
}

.header-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
}

.header-count {
  font-size: 24rpx;
  color: #999999;
}

.dish-scroll {
  flex: 1;
  padding: 20rpx;
}

.dish-grid {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}

.dish-card {
  width: calc(50% - 10rpx);
  margin-bottom: 20rpx;
  background-color: #ffffff;
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.dish-image {
  width: 100%;
  height: 200rpx;
}

.dish-info {
  padding: 16rpx;
}

.dish-name {
  font-size: 28rpx;
  color: #333333;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dish-price {
  font-size: 32rpx;
  color: #ff6b6b;
  font-weight: bold;
  margin-top: 8rpx;
  display: block;
}
</style>
