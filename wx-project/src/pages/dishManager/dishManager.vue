<template>
    <view class="container">
        <view class="header">
            <text class="title">菜品管理</text>
            <view class="add-btn" @tap="goAdd">
                <text>+ 添加</text>
            </view>
        </view>
        
        <scroll-view scroll-y class="dish-list">
            <view 
                v-for="dish in dishList" 
                :key="dish.id" 
                class="dish-item"
            >
                <image :src="getFullImageUrl(dish.image)" class="dish-image" mode="aspectFill" />
                <view class="dish-info">
                    <text class="dish-name">{{ dish.name }}</text>
                    <text class="dish-desc">{{ dish.description }}</text>
                    <text class="dish-price">¥{{ dish.price }}</text>
                </view>
                <view class="dish-actions">
                    <view class="action-btn edit" @tap="goEdit(dish)">
                        <text>编辑</text>
                    </view>
                    <view class="action-btn delete" @tap="handleDelete(dish.id)">
                        <text>删除</text>
                    </view>
                </view>
            </view>
            
            <view v-if="dishList.length === 0" class="empty">
                <text class="empty-icon">🍽️</text>
                <text class="empty-text">暂无菜品</text>
            </view>
        </scroll-view>
    </view>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { dishApi } from '@/utils/api';

const baseUrl = 'http://localhost:10086';
const dishList = ref([]);

const getFullImageUrl = (path) => {
    if (!path) return '';
    if (path.startsWith('http')) return path;
    return baseUrl + path;
};

const loadDishes = async () => {
    try {
        const res = await dishApi.getList({});
        if (res.data) {
            dishList.value = res.data;
        }
    } catch (error) {
        console.error('获取菜品失败:', error);
    }
};

const goAdd = () => {
    uni.navigateTo({ url: '/pages/dishForm/dishForm' });
};

const goEdit = (dish) => {
    uni.navigateTo({ url: `/pages/dishForm/dishForm?id=${dish.id}` });
};

const handleDelete = (id) => {
    uni.showModal({
        title: '提示',
        content: '确定要删除该菜品吗？',
        success: async (res) => {
            if (res.confirm) {
                try {
                    const result = await dishApi.delete(id);
                    if (result.code === 200) {
                        uni.showToast({ title: '删除成功', icon: 'success' });
                        loadDishes();
                    }
                } catch (error) {
                    console.error('删除失败:', error);
                    uni.showToast({ title: '删除失败', icon: 'none' });
                }
            }
        }
    });
};

onMounted(() => {
    loadDishes();
});
</script>

<style scoped>
.container {
    min-height: 100vh;
    background-color: #f5f5f5;
}

.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 30rpx 40rpx;
    background-color: #fff;
}

.title {
    font-size: 36rpx;
    font-weight: bold;
    color: #333;
}

.add-btn {
    padding: 16rpx 32rpx;
    background-color: #b4e3e3;
    border-radius: 30rpx;
    
    text {
        font-size: 28rpx;
        color: #fff;
        font-weight: bold;
    }
}

.dish-list {
    padding: 20rpx;
    height: calc(100vh - 120rpx);
}

.dish-item {
    display: flex;
    background-color: #fff;
    border-radius: 20rpx;
    padding: 20rpx;
    margin-bottom: 20rpx;
    box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.dish-image {
    width: 180rpx;
    height: 180rpx;
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
    color: #333;
}

.dish-desc {
    font-size: 24rpx;
    color: #999;
    margin-top: 10rpx;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.dish-price {
    font-size: 28rpx;
    color: #ff6b6b;
    font-weight: bold;
    margin-top: auto;
}

.dish-actions {
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.action-btn {
    padding: 12rpx 24rpx;
    border-radius: 20rpx;
    margin-bottom: 10rpx;
    text-align: center;
    
    text {
        font-size: 24rpx;
        color: #fff;
    }
    
    &.edit {
        background-color: #4CAF50;
    }
    
    &.delete {
        background-color: #ff6b6b;
    }
}

.empty {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 100rpx 0;
}

.empty-icon {
    font-size: 100rpx;
    margin-bottom: 20rpx;
}

.empty-text {
    font-size: 28rpx;
    color: #999;
}
</style>
