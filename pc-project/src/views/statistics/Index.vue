<template>
  <div class="statistics-container">
    <!-- 页面标题和筛选 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="title">数据统计</h1>
        <p class="subtitle">全面了解店铺运营状况</p>
      </div>
      <div class="header-right">
        <el-select v-model="timeRange" placeholder="选择时间范围" size="large">
          <el-option label="今日" value="today" />
          <el-option label="本周" value="week" />
          <el-option label="本月" value="month" />
          <el-option label="本年" value="year" />
        </el-select>
      </div>
    </div>

    <!-- 核心数据卡片 -->
    <div class="core-data">
      <div class="data-card" v-for="(item, index) in coreData" :key="index">
        <div class="card-header">
          <span class="card-icon">{{ item.icon }}</span>
          <span class="card-trend" :class="item.trend > 0 ? 'up' : 'down'">
            {{ item.trend > 0 ? '↑' : '↓' }} {{ Math.abs(item.trend) }}%
          </span>
        </div>
        <div class="card-body">
          <span class="card-value">{{ item.value }}</span>
          <span class="card-label">{{ item.label }}</span>
        </div>
        <div class="card-footer">
          <span class="footer-text">较上期</span>
        </div>
      </div>
    </div>

    <!-- 销售趋势 - 优化版 -->
    <div class="sales-trend">
      <div class="section-header">
        <h2 class="section-title">销售趋势</h2>
        <div class="trend-tabs">
          <span 
            v-for="tab in trendTabs" 
            :key="tab.value"
            :class="['tab-item', { active: currentTab === tab.value }]"
            @click="currentTab = tab.value; loadOrderTrend()"
          >
            {{ tab.label }}
          </span>
        </div>
      </div>
      
      <!-- 统计概览 -->
      <div class="trend-overview">
        <div class="overview-item">
          <span class="overview-label">本周订单量</span>
          <span class="overview-value order">{{ weekOrderCount }}</span>
          <span class="overview-change up">+12.5%</span>
        </div>
        <div class="overview-item">
          <span class="overview-label">本周销售额</span>
          <span class="overview-value money">¥{{ weekRevenue.toFixed(2) }}</span>
          <span class="overview-change up">+15.8%</span>
        </div>
        <div class="overview-item">
          <span class="overview-label">日均订单</span>
          <span class="overview-value">{{ dailyOrderCount }}</span>
          <span class="overview-change up">+8.3%</span>
        </div>
        <div class="overview-item">
          <span class="overview-label">客单价</span>
          <span class="overview-value">¥{{ avgOrderAmount.toFixed(2) }}</span>
          <span class="overview-change down">-2.1%</span>
        </div>
      </div>

      <!-- 图表区域 -->
      <div class="chart-wrapper">
        <div class="chart-header">
          <span class="chart-title">近7日趋势</span>
          <div class="chart-legend">
            <span class="legend-item">
              <span class="legend-dot order"></span>订单量
            </span>
            <span class="legend-item">
              <span class="legend-dot money"></span>销售额(百元)
            </span>
          </div>
        </div>
        <div class="chart-area">
          <!-- Y轴标签 -->
          <div class="y-axis">
            <span v-for="(label, i) in yAxisLabels" :key="i">{{ label }}</span>
          </div>
          <!-- 图表主体 -->
          <div class="chart-body">
            <!-- 网格线 -->
            <div class="grid-lines">
              <div class="grid-line" v-for="i in 5" :key="i"></div>
            </div>
            <!-- 折线图 -->
            <div class="lines-container">
              <!-- 订单量折线 -->
              <svg class="line-svg" viewBox="0 0 700 200" preserveAspectRatio="none">
                <defs>
                  <linearGradient id="orderGradient" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="0%" stop-color="#409eff" stop-opacity="0.3"/>
                    <stop offset="100%" stop-color="#409eff" stop-opacity="0"/>
                  </linearGradient>
                  <linearGradient id="moneyGradient" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="0%" stop-color="#67c23a" stop-opacity="0.3"/>
                    <stop offset="100%" stop-color="#67c23a" stop-opacity="0"/>
                  </linearGradient>
                </defs>
                <!-- 订单量面积 -->
                <path :d="orderAreaPath" fill="url(#orderGradient)" />
                <!-- 销售额面积 -->
                <path :d="moneyAreaPath" fill="url(#moneyGradient)" />
                <!-- 订单量折线 -->
                <path :d="orderLinePath" fill="none" stroke="#409eff" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" />
                <!-- 销售额折线 -->
                <path :d="moneyLinePath" fill="none" stroke="#67c23a" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" />
                <!-- 数据点 -->
                <circle v-for="(point, i) in orderPoints" :key="'oc'+i" :cx="point.x" :cy="point.y" r="4" fill="#409eff" class="data-point" />
                <circle v-for="(point, i) in moneyPoints" :key="'mc'+i" :cx="point.x" :cy="point.y" r="4" fill="#67c23a" class="data-point" />
              </svg>
            </div>
            <!-- X轴标签 -->
            <div class="x-axis">
              <span v-for="(label, i) in xAxisLabels" :key="i">{{ label }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 订单统计 -->
    <div class="order-stats">
      <div class="stats-card">
        <h2 class="card-title">订单分析</h2>
        <div class="stats-grid">
          <div class="stat-item" v-for="(stat, index) in orderStats" :key="index">
            <span class="stat-value" :style="{ color: stat.color }">{{ stat.value }}</span>
            <span class="stat-label">{{ stat.label }}</span>
          </div>
        </div>
      </div>
      <div class="stats-card">
        <h2 class="card-title">订单状态分布</h2>
        <div class="status-distribution">
          <div class="status-item" v-for="(status, index) in orderStatus" :key="index">
            <div class="status-bar">
              <div class="status-fill" :style="{ width: status.percent + '%', background: status.color }"></div>
            </div>
            <div class="status-info">
              <span class="status-name">{{ status.name }}</span>
              <span class="status-percent">{{ status.percent }}%</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 菜品销量排行 -->
    <div class="dish-ranking">
      <div class="section-header">
        <h2 class="section-title">菜品销量排行</h2>
        <el-radio-group v-model="rankingType" size="small">
          <el-radio-button label="sales">销量</el-radio-button>
          <el-radio-button label="amount">销售额</el-radio-button>
        </el-radio-group>
      </div>
      <div class="ranking-list">
        <div class="ranking-item" v-for="(dish, index) in dishRanking" :key="index">
          <div class="rank-number" :class="'rank-' + (index + 1)">{{ index + 1 }}</div>
          <div class="dish-info">
            <span class="dish-name">{{ dish.dishName || '未知菜品' }}</span>
            <span class="dish-sales">已售 {{ dish.salesCount }} 份</span>
          </div>
          <div class="dish-chart">
            <div class="dish-bar" :style="{ width: dish.percent + '%' }"></div>
          </div>
          <span class="dish-value">¥{{ (dish.totalAmount || 0).toFixed(2) }}</span>
        </div>
      </div>
    </div>

    <!-- 分类销量排行 -->
    <div class="category-ranking">
      <div class="section-header">
        <h2 class="section-title">分类销量排行</h2>
      </div>
      <div class="ranking-list">
        <div class="ranking-item" v-for="(category, index) in categoryRanking" :key="index">
          <div class="rank-number" :class="'rank-' + (index + 1)">{{ index + 1 }}</div>
          <div class="dish-info">
            <span class="dish-name">{{ category.categoryName || '未知分类' }}</span>
            <span class="dish-sales">已售 {{ category.salesCount }} 份</span>
          </div>
          <div class="dish-chart">
            <div class="dish-bar category" :style="{ width: category.percent + '%' }"></div>
          </div>
          <span class="dish-value">¥{{ (category.totalAmount || 0).toFixed(2) }}</span>
        </div>
      </div>
    </div>

    <!-- 用户分析 -->
    <div class="user-analysis">
      <div class="section-header">
        <h2 class="section-title">店铺概览</h2>
      </div>
      <div class="user-cards">
        <div class="user-card" v-for="(user, index) in userAnalysis" :key="index">
          <div class="user-header">
            <span class="user-icon">{{ user.icon }}</span>
            <span class="user-title">{{ user.title }}</span>
          </div>
          <div class="user-value">{{ user.value }}</div>
          <div class="user-trend">
            <span :class="user.trend > 0 ? 'up' : 'down'">
              {{ user.trend > 0 ? '↑' : '↓' }} {{ Math.abs(user.trend) }}%
            </span>
            <span class="trend-label">较上期</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { 
  getOverallStatistics, 
  getTodayStatistics, 
  getOrderTrend, 
  getDishRanking,
  getCategoryRanking 
} from '@/api/statisticsApi.js'

const timeRange = ref('week')
const currentTab = ref('day')
const rankingType = ref('sales')
const loading = ref(false)

const trendTabs = [
  { label: '按天', value: 'day' },
  { label: '按周', value: 'week' },
  { label: '按月', value: 'month' }
]

// 核心数据
const overallStats = reactive({
  totalOrders: 0,
  totalRevenue: 0,
  totalUsers: 0,
  totalDishes: 0,
  totalSetmeals: 0
})

const todayStats = reactive({
  todayOrders: 0,
  todayRevenue: 0
})

const coreData = computed(() => [
  { icon: '💰', label: '总销售额', value: `¥${overallStats.totalRevenue.toFixed(2)}`, trend: 15.2 },
  { icon: '📦', label: '总订单量', value: overallStats.totalOrders.toString(), trend: 8.5 },
  { icon: '👥', label: '员工数量', value: overallStats.totalUsers.toString(), trend: 12.8 },
  { icon: '🍳', label: '菜品数量', value: overallStats.totalDishes.toString(), trend: -2.1 }
])

// 销售趋势数据
const salesData = ref([
  { day: '周一', orders: 0, money: 0 },
  { day: '周二', orders: 0, money: 0 },
  { day: '周三', orders: 0, money: 0 },
  { day: '周四', orders: 0, money: 0 },
  { day: '周五', orders: 0, money: 0 },
  { day: '周六', orders: 0, money: 0 },
  { day: '周日', orders: 0, money: 0 }
])

const maxOrders = computed(() => {
  const max = Math.max(...salesData.value.map(d => d.orders))
  return max === 0 ? 100 : max
})

const maxMoney = computed(() => {
  const max = Math.max(...salesData.value.map(d => d.money))
  return max === 0 ? 1000 : max
})

// 计算周统计数据
const weekOrderCount = computed(() => salesData.value.reduce((sum, d) => sum + d.orders, 0))
const weekRevenue = computed(() => salesData.value.reduce((sum, d) => sum + d.money, 0))
const dailyOrderCount = computed(() => Math.round(weekOrderCount.value / 7))
const avgOrderAmount = computed(() => {
  if (weekOrderCount.value === 0) return 0
  return weekRevenue.value / weekOrderCount.value
})

// Y轴标签
const yAxisLabels = computed(() => {
  const max = Math.ceil(maxOrders.value / 50) * 50
  return [max, Math.round(max * 0.75), Math.round(max * 0.5), Math.round(max * 0.25), 0]
})

const xAxisLabels = computed(() => salesData.value.map(d => d.day))

// 计算折线图路径
const chartWidth = 700
const chartHeight = 200

const orderPoints = computed(() => {
  return salesData.value.map((d, i) => ({
    x: (i / (salesData.value.length - 1)) * chartWidth,
    y: chartHeight - (d.orders / maxOrders.value) * chartHeight
  }))
})

const moneyPoints = computed(() => {
  return salesData.value.map((d, i) => ({
    x: (i / (salesData.value.length - 1)) * chartWidth,
    y: chartHeight - (d.money / maxMoney.value) * chartHeight
  }))
})

const orderLinePath = computed(() => {
  const points = orderPoints.value
  return points.map((p, i) => (i === 0 ? `M ${p.x} ${p.y}` : `L ${p.x} ${p.y}`)).join(' ')
})

const moneyLinePath = computed(() => {
  const points = moneyPoints.value
  return points.map((p, i) => (i === 0 ? `M ${p.x} ${p.y}` : `L ${p.x} ${p.y}`)).join(' ')
})

const orderAreaPath = computed(() => {
  const linePath = orderLinePath.value
  return `${linePath} L ${chartWidth} ${chartHeight} L 0 ${chartHeight} Z`
})

const moneyAreaPath = computed(() => {
  const linePath = moneyLinePath.value
  return `${linePath} L ${chartWidth} ${chartHeight} L 0 ${chartHeight} Z`
})

// 订单统计
const orderStats = computed(() => [
  { label: '总订单', value: overallStats.totalOrders.toString(), color: '#67c23a' },
  { label: '总菜品', value: overallStats.totalDishes.toString(), color: '#409eff' },
  { label: '总套餐', value: overallStats.totalSetmeals.toString(), color: '#e6a23c' },
  { label: '今日订单', value: todayStats.todayOrders.toString(), color: '#909399' }
])

// 订单状态分布（模拟数据）
const orderStatus = reactive([
  { name: '已完成', percent: 85, color: '#67c23a' },
  { name: '进行中', percent: 10, color: '#409eff' },
  { name: '已取消', percent: 3, color: '#909399' },
  { name: '待接单', percent: 2, color: '#e6a23c' }
])

// 菜品排行
const dishRanking = ref([])

// 分类排行
const categoryRanking = ref([])

// 用户分析
const userAnalysis = computed(() => [
  { icon: '🆕', title: '总订单', value: overallStats.totalOrders.toString(), trend: 12.5 },
  { icon: '👀', title: '总菜品', value: overallStats.totalDishes.toString(), trend: 8.3 },
  { icon: '🛒', title: '总套餐', value: overallStats.totalSetmeals.toString(), trend: 15.8 },
  { icon: '⭐', title: '总销售额', value: `¥${overallStats.totalRevenue.toFixed(2)}`, trend: 3.2 }
])

const loadOverallStatistics = async () => {
  try {
    const res = await getOverallStatistics()
    if (res.code === 200) {
      Object.assign(overallStats, res.data)
    }
  } catch (error) {
    console.error('获取总体统计失败', error)
  }
}

const loadTodayStatistics = async () => {
  try {
    const res = await getTodayStatistics()
    if (res.code === 200) {
      Object.assign(todayStats, res.data)
    }
  } catch (error) {
    console.error('获取今日统计失败', error)
  }
}

const loadOrderTrend = async () => {
  try {
    const days = currentTab.value === 'day' ? 7 : currentTab.value === 'week' ? 4 : 12
    const res = await getOrderTrend(days)
    if (res.code === 200) {
      const trendData = res.data
      if (trendData && trendData.length > 0) {
        salesData.value = trendData.map(item => ({
          day: item.date || '未知',
          orders: item.orderCount || 0,
          money: (item.revenue || 0) * 100
        }))
      }
    }
  } catch (error) {
    console.error('获取订单趋势失败', error)
  }
}

const loadDishRanking = async () => {
  try {
    const res = await getDishRanking(8)
    if (res.code === 200) {
      const data = res.data
      if (data && data.length > 0) {
        const maxSales = Math.max(...data.map(d => d.salesCount || 0))
        dishRanking.value = data.map(item => ({
          ...item,
          percent: maxSales > 0 ? ((item.salesCount || 0) / maxSales * 100) : 0
        }))
      }
    }
  } catch (error) {
    console.error('获取菜品排行失败', error)
  }
}

const loadCategoryRanking = async () => {
  try {
    const res = await getCategoryRanking()
    if (res.code === 200) {
      const data = res.data
      if (data && data.length > 0) {
        const maxSales = Math.max(...data.map(d => d.salesCount || 0))
        categoryRanking.value = data.map(item => ({
          ...item,
          percent: maxSales > 0 ? ((item.salesCount || 0) / maxSales * 100) : 0
        }))
      }
    }
  } catch (error) {
    console.error('获取分类排行失败', error)
  }
}

onMounted(() => {
  loadOverallStatistics()
  loadTodayStatistics()
  loadOrderTrend()
  loadDishRanking()
  loadCategoryRanking()
})
</script>

<style scoped>
.statistics-container {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
  font-family: 'PingFang SC', 'Microsoft YaHei', -apple-system, sans-serif;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.title {
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 8px;
}

.subtitle {
  font-size: 14px;
  color: #95a5a6;
  margin: 0;
}

/* 核心数据卡片 */
.core-data {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.data-card {
  background: #fff;
  padding: 20px;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease;
}

.data-card:hover {
  transform: translateY(-5px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-icon {
  font-size: 28px;
}

.card-trend {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 12px;
  font-weight: 500;
}

.card-trend.up {
  background: #f0f9eb;
  color: #67c23a;
}

.card-trend.down {
  background: #fef0f0;
  color: #f56c6c;
}

.card-body {
  display: flex;
  flex-direction: column;
  margin-bottom: 8px;
}

.card-value {
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
}

.card-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.card-footer {
  padding-top: 12px;
  border-top: 1px dashed #ebeef5;
}

.footer-text {
  font-size: 12px;
  color: #909399;
}

/* 销售趋势 */
.sales-trend {
  background: #fff;
  padding: 24px;
  border-radius: 16px;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
  padding-left: 12px;
  border-left: 4px solid #409eff;
}

.trend-tabs {
  display: flex;
  gap: 8px;
}

.tab-item {
  padding: 6px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
  color: #606266;
  transition: all 0.3s;
}

.tab-item:hover {
  color: #409eff;
}

.tab-item.active {
  background: #409eff;
  color: #fff;
}

/* 趋势概览 */
.trend-overview {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
}

.overview-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.overview-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.overview-value {
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 4px;
}

.overview-value.order {
  color: #409eff;
}

.overview-value.money {
  color: #67c23a;
}

.overview-change {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
}

.overview-change.up {
  background: #f0f9eb;
  color: #67c23a;
}

.overview-change.down {
  background: #fef0f0;
  color: #f56c6c;
}

/* 图表区域 */
.chart-wrapper {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 12px;
  padding: 20px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.chart-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.chart-legend {
  display: flex;
  gap: 20px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #606266;
}

.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.legend-dot.order {
  background: #409eff;
}

.legend-dot.money {
  background: #67c23a;
}

.chart-area {
  display: flex;
  height: 260px;
}

.y-axis {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding-right: 12px;
  padding-bottom: 30px;
}

.y-axis span {
  font-size: 11px;
  color: #909399;
  text-align: right;
  min-width: 40px;
}

.chart-body {
  flex: 1;
  position: relative;
}

.grid-lines {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 30px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.grid-line {
  border-bottom: 1px dashed #ebeef5;
}

.lines-container {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 30px;
}

.line-svg {
  width: 100%;
  height: 100%;
}

.data-point {
  cursor: pointer;
  transition: r 0.2s ease;
}

.data-point:hover {
  r: 6;
}

.x-axis {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-between;
}

.x-axis span {
  font-size: 11px;
  color: #909399;
  text-align: center;
  flex: 1;
}

/* 订单统计 */
.order-stats {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 24px;
}

.stats-card {
  background: #fff;
  padding: 20px;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f0f0f0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.status-distribution {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.status-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.status-bar {
  height: 8px;
  background: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
}

.status-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.5s ease;
}

.status-info {
  display: flex;
  justify-content: space-between;
}

.status-name {
  font-size: 13px;
  color: #606266;
}

.status-percent {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
}

/* 菜品排行 */
.dish-ranking {
  background: #fff;
  padding: 20px;
  border-radius: 16px;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.category-ranking {
  background: #fff;
  padding: 20px;
  border-radius: 16px;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 16px;
  background: #f8f9fa;
  border-radius: 10px;
  transition: all 0.3s;
}

.ranking-item:hover {
  background: #f0f4f8;
}

.rank-number {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  font-weight: 700;
  font-size: 14px;
  background: #e0e0e0;
  color: #fff;
}

.rank-number.rank-1 { background: linear-gradient(135deg, #ffd700, #ffb800); }
.rank-number.rank-2 { background: linear-gradient(135deg, #c0c0c0, #a8a8a8); }
.rank-number.rank-3 { background: linear-gradient(135deg, #cd7f32, #b87333); }

.dish-info {
  width: 180px;
  display: flex;
  flex-direction: column;
}

.dish-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.dish-sales {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.dish-chart {
  flex: 1;
  height: 8px;
  background: #ebeef5;
  border-radius: 4px;
  overflow: hidden;
}

.dish-bar {
  height: 100%;
  background: linear-gradient(90deg, #409eff, #67c23a);
  border-radius: 4px;
  transition: width 0.5s ease;
}

.dish-bar.category {
  background: linear-gradient(90deg, #e6a23c, #f56c6c);
}

.dish-value {
  width: 80px;
  text-align: right;
  font-size: 14px;
  font-weight: 600;
  color: #f56c6c;
}

/* 用户分析 */
.user-analysis {
  background: #fff;
  padding: 20px;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.user-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.user-card {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  text-align: center;
}

.user-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 12px;
}

.user-icon {
  font-size: 24px;
}

.user-title {
  font-size: 14px;
  color: #606266;
}

.user-value {
  font-size: 32px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 8px;
}

.user-trend {
  font-size: 13px;
}

.user-trend .up {
  color: #67c23a;
  font-weight: 600;
}

.user-trend .down {
  color: #f56c6c;
  font-weight: 600;
}

.trend-label {
  color: #909399;
  margin-left: 4px;
}

/* 响应式 */
@media (max-width: 1200px) {
  .core-data {
    grid-template-columns: repeat(2, 1fr);
  }
  .trend-overview {
    grid-template-columns: repeat(2, 1fr);
  }
  .order-stats {
    grid-template-columns: 1fr;
  }
  .user-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .core-data {
    grid-template-columns: 1fr;
  }
  .trend-overview {
    grid-template-columns: 1fr;
  }
  .user-cards {
    grid-template-columns: 1fr;
  }
}
</style>