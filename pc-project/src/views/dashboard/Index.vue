<template>
  <div class="dashboard-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="title">🍽️ Welcome to zhijie canteen</h1>
      <p class="subtitle">用心做好每一份外卖</p>
    </div>

    <!-- 轮播图区域 -->
    <div class="carousel-wrapper">
      <el-carousel 
        :interval="4000" 
        type="card" 
        height="320px"
        indicator-position="none"
        class="custom-carousel"
      >
        <el-carousel-item v-for="(path, index) in pathArr" :key="index">
          <div class="carousel-item-wrapper">
            <el-image 
              :src="path" 
              fit="cover" 
              class="carousel-image"
            />
            <div class="carousel-overlay"></div>
            <div class="carousel-caption">
              <span class="caption-text">精选美食 · 品质外卖</span>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
      <div class="carousel-indicators">
        <span 
          v-for="(_, index) in pathArr" 
          :key="index"
          :class="['indicator', { active: currentIndex === index }]"
          @click="goToSlide(index)"
        ></span>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="main-content">
      <!-- 左侧店铺卡片 -->
      <div class="shop-card">
        <div class="card-image-wrapper">
          <img 
            src="@/assets/餐厅实拍.jpg" 
            alt="餐厅实拍" 
            class="shop-image"
          />
          <div class="image-badge">
            <span class="badge-text">🏆 金牌店铺</span>
          </div>
        </div>
        <div class="card-content">
          <div class="shop-header">
            <h2 class="shop-name">🍜 巷口·烟火食堂</h2>
            <div class="shop-rating">
              <span class="rating-star">⭐</span>
              <span class="rating-value">4.9</span>
              <span class="rating-label">分</span>
            </div>
          </div>
          <p class="shop-sales">🔥 月售6000+ 单</p>
          <div class="shop-info">
            <div class="info-item">
              <span class="info-icon">📍</span>
              <span class="info-text">南宁市青秀区东葛路美食街88号</span>
            </div>
            <div class="info-item">
              <span class="info-icon">🕐</span>
              <span class="info-text">10:00 - 22:00</span>
            </div>
          </div>
          <div class="shop-tags">
            <span class="tag">中式快餐</span>
            <span class="tag">家常菜</span>
            <span class="tag">品质保证</span>
          </div>
        </div>
      </div>

      <!-- 右侧内容区 -->
      <div class="right-section">
        <!-- 外卖特色徽章 -->
        <div class="delivery-benefits">
          <div 
            v-for="(benefit, index) in benefits" 
            :key="index"
            class="benefit-card"
          >
            <span class="benefit-icon">{{ benefit.icon }}</span>
            <div class="benefit-content">
              <span class="benefit-title">{{ benefit.title }}</span>
              <span class="benefit-desc">{{ benefit.desc }}</span>
            </div>
          </div>
        </div>

        <!-- 店铺介绍 -->
        <div class="intro-card">
          <h3 class="card-title">关于我们</h3>
          <p class="intro-text">
            在忙碌的都市生活中，「巷口·烟火食堂」用心做好每一份外卖。严选新鲜食材，匠心烹饪，让您足不出户享受地道风味。热腾腾的家常味，治愈一天的疲惫。即刻下单，骑手极速送达，美味无需久等。
          </p>
        </div>

        <!-- 服务特色 -->
        <div class="features-card">
          <h3 class="card-title">服务特色</h3>
          <ul class="features-list">
            <li class="feature-item" v-for="(feature, index) in features" :key="index">
              <div class="feature-icon-wrapper" :class="feature.colorClass">
                <span class="feature-icon">{{ feature.icon }}</span>
              </div>
              <div class="feature-content">
                <span class="feature-title">{{ feature.title }}</span>
                <p class="feature-desc">{{ feature.desc }}</p>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>

    <!-- 底部装饰 -->
    <div class="footer-decoration">
      <div class="wave"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import food1 from '@/assets/food_1.jpg'
import food2 from '@/assets/food_2.jpg'
import food3 from '@/assets/food_3.jpg'
import food4 from '@/assets/food_4.jpg'
import food5 from '@/assets/food_5.jpg'
import food6 from '@/assets/food_6.jpg'

// 轮播图图片数组
const pathArr = reactive([
  food1,
  food2,
  food3,
  food4,
  food5,
  food6
])

// 当前轮播图索引
const currentIndex = ref(0)

// 外卖特色服务
const benefits = reactive([
  { icon: '🚚', title: '专属配送', desc: '30分钟必达' },
  { icon: '🧧', title: '新客优惠', desc: '立减12元' },
  { icon: '⭐', title: '招牌套餐', desc: '5折起' }
])

// 服务特色列表
const features = reactive([
  { 
    icon: '🍲', 
    title: '超值爆款', 
    desc: '秘制红烧肉套餐｜香辣口水鸡｜广式腊味煲仔饭，限时8折热卖中，下单即赠冰柠可乐。',
    colorClass: 'feature-orange'
  },
  { 
    icon: '⏱️', 
    title: '极速出餐', 
    desc: '专业后厨团队，标准化流程，高峰期平均出餐仅12分钟，打包牢固保温，送达依旧锅气满满。',
    colorClass: 'feature-green'
  },
  { 
    icon: '🚴', 
    title: '安心服务', 
    desc: '食安险护航 + 无接触配送，每单都附赠"安心卡"，记录后厨与骑手体温，让您吃得放心。',
    colorClass: 'feature-blue'
  }
])

// 轮播图切换
const goToSlide = (index) => {
  currentIndex.value = index
}

// 监听轮播图变化
onMounted(() => {
  setInterval(() => {
    currentIndex.value = (currentIndex.value + 1) % pathArr.length
  }, 4000)
})
</script>

<style scoped>
/* 页面容器 */
.dashboard-container {
  min-height: 100vh;
  background: linear-gradient(180deg, #f0f9f4 0%, #fff 50%, #e8f5e9 100%);
  padding: 30px 20px;
  font-family: 'PingFang SC', 'Microsoft YaHei', -apple-system, sans-serif;
}

/* 页面标题 */
.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.title {
  font-size: 32px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 8px;
  background: linear-gradient(135deg, #2d6a4f 0%, #52b788 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  font-size: 16px;
  color: #7f8c8d;
}

/* 轮播图容器 */
.carousel-wrapper {
  position: relative;
  max-width: 1200px;
  margin: 0 auto 35px;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 25px 45px -15px rgba(0, 0, 0, 0.15);
}

.custom-carousel {
  height: 320px !important;
}

.carousel-item-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.carousel-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.6) 0%, rgba(0, 0, 0, 0.1) 50%, transparent 100%);
}

.carousel-caption {
  position: absolute;
  bottom: 30px;
  left: 30px;
}

.caption-text {
  font-size: 22px;
  font-weight: 600;
  color: #fff;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

/* 轮播图指示器 */
.carousel-indicators {
  position: absolute;
  bottom: 20px;
  right: 30px;
  display: flex;
  gap: 10px;
}

.indicator {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.4);
  cursor: pointer;
  transition: all 0.3s ease;
}

.indicator.active {
  width: 24px;
  border-radius: 20px;
  background: #fff;
}

/* 主体内容 */
.main-content {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 28px;
}

/* 店铺卡片 */
.shop-card {
  background: #fff;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 12px 35px -10px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.shop-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 45px -15px rgba(0, 0, 0, 0.12);
}

.card-image-wrapper {
  position: relative;
}

.shop-image {
  width: 100%;
  height: 220px;
  object-fit: cover;
}

.image-badge {
  position: absolute;
  top: 15px;
  right: 15px;
  background: linear-gradient(135deg, #2d6a4f 0%, #52b788 100%);
  padding: 6px 16px;
  border-radius: 20px;
}

.badge-text {
  font-size: 13px;
  font-weight: 600;
  color: #fff;
}

.card-content {
  padding: 22px;
}

.shop-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.shop-name {
  font-size: 22px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0;
}

.shop-rating {
  display: flex;
  align-items: center;
  background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
  padding: 6px 12px;
  border-radius: 20px;
}

.rating-star {
  font-size: 16px;
  margin-right: 4px;
}

.rating-value {
  font-size: 16px;
  font-weight: 700;
  color: #2d6a4f;
}

.rating-label {
  font-size: 13px;
  color: #95a5a6;
  margin-left: 2px;
}

.shop-sales {
  font-size: 14px;
  color: #2d6a4f;
  font-weight: 500;
  margin: 0 0 15px;
}

.shop-info {
  margin-bottom: 15px;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-icon {
  font-size: 14px;
  margin-right: 8px;
}

.info-text {
  font-size: 14px;
  color: #5d6d7e;
}

.shop-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tag {
  background: linear-gradient(135deg, #f0f4f8 0%, #e8ecef 100%);
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  color: #5d6d7e;
}

/* 右侧区域 */
.right-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 外卖特色徽章 */
.delivery-benefits {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
}

.benefit-card {
  background: linear-gradient(135deg, #f0f9f4 0%, #e8f5e9 100%);
  border-radius: 16px;
  padding: 18px;
  display: flex;
  align-items: center;
  gap: 12px;
  transition: transform 0.2s ease;
}

.benefit-card:hover {
  transform: translateY(-3px);
}

.benefit-icon {
  font-size: 28px;
}

.benefit-content {
  display: flex;
  flex-direction: column;
}

.benefit-title {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
}

.benefit-desc {
  font-size: 12px;
  color: #2d6a4f;
  font-weight: 500;
}

/* 介绍卡片 */
.intro-card {
  background: #fff;
  border-radius: 16px;
  padding: 22px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.04);
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 15px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f0f0f0;
}

.intro-text {
  font-size: 14px;
  color: #5d6d7e;
  line-height: 1.8;
  margin: 0;
}

/* 服务特色 */
.features-card {
  background: #fff;
  border-radius: 16px;
  padding: 22px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.04);
}

.features-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.feature-item {
  display: flex;
  gap: 15px;
  padding: 15px 0;
  border-bottom: 1px dashed #f0f0f0;
}

.feature-item:last-child {
  border-bottom: none;
}

.feature-icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.feature-orange {
  background: linear-gradient(135deg, #fff5ee 0%, #ffe4d4 100%);
}

.feature-green {
  background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
}

.feature-blue {
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
}

.feature-icon {
  font-size: 22px;
}

.feature-content {
  flex: 1;
}

.feature-title {
  font-size: 15px;
  font-weight: 600;
  color: #2c3e50;
  display: block;
  margin-bottom: 6px;
}

.feature-desc {
  font-size: 13px;
  color: #7f8c8d;
  line-height: 1.6;
  margin: 0;
}

/* 底部装饰 */
.footer-decoration {
  margin-top: 40px;
}

.wave {
  height: 60px;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 1440 120'%3E%3Cpath fill='%23ff6b35' fill-opacity='0.1' d='M0,64L48,69.3C96,75,192,85,288,80C384,75,480,53,576,48C672,43,768,53,864,64C960,75,1056,85,1152,80C1248,75,1344,53,1392,42.7L1440,32L1440,120L1392,120C1344,120,1248,120,1152,120C1056,120,960,120,864,120C768,120,672,120,576,120C480,120,384,120,288,120C192,120,96,120,48,120L0,120Z'%3E%3C/path%3E%3C/svg%3E") no-repeat bottom center;
  background-size: cover;
}

/* 响应式布局 */
@media (max-width: 992px) {
  .main-content {
    grid-template-columns: 1fr;
  }

  .delivery-benefits {
    grid-template-columns: 1fr;
  }

  .title {
    font-size: 26px;
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 20px 15px;
  }

  .custom-carousel {
    height: 220px !important;
  }

  .caption-text {
    font-size: 18px;
  }

  .shop-name {
    font-size: 20px;
  }

  .shop-image {
    height: 180px;
  }
}
</style>

组名：第8组
组长：张三
组员：李四、王五、赵六
存在的问题： 1. 购物车页面的购物车图标和数量显示错误

