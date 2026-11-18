<template>
  <div class="workbench-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="title">工作台</h1>
      <p class="subtitle">欢迎回来，今日加油！</p>
    </div>

    <!-- 今日数据概览 -->
    <div class="data-overview">
      <div class="data-card" v-for="(item, index) in dataCards" :key="index" :style="{ background: item.bgColor }">
        <div class="data-icon">{{ item.icon }}</div>
        <div class="data-info">
          <span class="data-value">{{ item.value }}</span>
          <span class="data-label">{{ item.label }}</span>
        </div>
        <div class="data-trend" :class="item.trend > 0 ? 'up' : 'down'">
          {{ item.trend > 0 ? '↑' : '↓' }} {{ Math.abs(item.trend) }}%
        </div>
      </div>
    </div>

    <!-- 待处理任务 -->
    <div class="pending-tasks">
      <h2 class="section-title">待处理任务</h2>
      <div class="task-grid">
        <div class="task-card" v-for="(task, index) in pendingTasks" :key="index" @click="handleTask(task)">
          <div class="task-icon" :style="{ background: task.bgColor }">{{ task.icon }}</div>
          <div class="task-info">
            <span class="task-count">{{ task.count }}</span>
            <span class="task-name">{{ task.name }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="main-content">
      <!-- 左侧：订单动态 -->
      <div class="order-dynamic">
        <h2 class="section-title">实时订单动态</h2>
        <div class="order-list">
          <div class="order-item" v-for="(order, index) in recentOrders" :key="index">
            <div class="order-left">
              <span class="order-icon" :class="order.statusClass">{{ order.statusIcon }}</span>
              <div class="order-info">
                <span class="order-no">订单号：{{ order.orderNo }}</span>
                <span class="order-desc">{{ order.desc }}</span>
              </div>
            </div>
            <span class="order-time">{{ order.time }}</span>
          </div>
        </div>
      </div>

      <!-- 右侧：快捷入口 -->
      <div class="quick-entry">
        <h2 class="section-title">快捷操作</h2>
        <div class="entry-grid">
          <div class="entry-item" v-for="(entry, index) in quickEntries" :key="index" @click="handleEntry(entry)">
            <div class="entry-icon" :style="{ background: entry.bgColor }">{{ entry.icon }}</div>
            <span class="entry-name">{{ entry.name }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部统计 -->
    <div class="bottom-stats">
      <div class="stat-card">
        <div class="stat-chart">
          <div class="chart-placeholder">本周订单趋势</div>
        </div>
        <div class="stat-list">
          <div class="stat-item" v-for="(stat, index) in weekStats" :key="index">
            <span class="stat-day">{{ stat.day }}</span>
            <div class="stat-bar">
              <div class="stat-bar-fill" :style="{ width: stat.percent + '%', background: stat.color }"></div>
            </div>
            <span class="stat-value">{{ stat.value }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getOverallStatistics, getTodayStatistics, getOrderTrend } from '@/api/statisticsApi.js'
import { getOrderPage } from '@/api/orderApi.js'

const router = useRouter()

// 今日数据卡片
const dataCards = reactive([
  { icon: '📦', label: '今日订单', value: '0', trend: 0, bgColor: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { icon: '💰', label: '今日营业额', value: '¥0.00', trend: 0, bgColor: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { icon: '⭐', label: '总订单', value: '0', trend: 0, bgColor: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { icon: '🍳', label: '菜品数量', value: '0', trend: 0, bgColor: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' }
])

// 待处理任务
const pendingTasks = reactive([
  { icon: '🛵', name: '待接单', count: 0, bgColor: '#fff3e0' },
  { icon: '📱', name: '待确认', count: 0, bgColor: '#e3f2fd' },
  { icon: '🚚', name: '待发货', count: 0, bgColor: '#f3e5f5' },
  { icon: '💬', name: '待回复', count: 0, bgColor: '#e8f5e9' },
  { icon: '🔄', name: '退款申请', count: 0, bgColor: '#ffebee' },
  { icon: '⚠️', name: '异常订单', count: 0, bgColor: '#fff8e1' }
])

// 实时订单动态
const recentOrders = ref([
  { orderNo: '-', desc: '暂无订单', statusIcon: '📭', statusClass: 'empty', time: '-' }
])

// 快捷入口
const quickEntries = reactive([
  { icon: '➕', name: '添加菜品', bgColor: '#fff3e0', path: '/dish/add' },
  { icon: '📋', name: '处理订单', bgColor: '#e3f2fd', path: '/order' },
  { icon: '📊', name: '数据统计', bgColor: '#f3e5f5', path: '/statistics' },
  { icon: '💬', name: '消息中心', bgColor: '#e8f5e9', path: '/' },
  { icon: '⚙️', name: '店铺设置', bgColor: '#eceff1', path: '/' },
  { icon: '📱', name: '联系客服', bgColor: '#e0f7fa', path: '/' }
])

// 本周统计
const weekStats = reactive([
  { day: '周一', value: 0, percent: 0, color: '#4facfe' },
  { day: '周二', value: 0, percent: 0, color: '#4facfe' },
  { day: '周三', value: 0, percent: 0, color: '#4facfe' },
  { day: '周四', value: 0, percent: 0, color: '#4facfe' },
  { day: '周五', value: 0, percent: 0, color: '#4facfe' },
  { day: '周六', value: 0, percent: 0, color: '#43e97b' },
  { day: '周日', value: 0, percent: 0, color: '#43e97b' }
])

// 点击任务
const handleTask = (task) => {
  console.log('处理任务:', task.name)
  if (task.name === '待接单') {
    router.push('/order?status=1')
  } else if (task.name === '待确认') {
    router.push('/order?status=2')
  } else if (task.name === '待发货') {
    router.push('/order?status=3')
  } else {
    router.push('/order')
  }
}

// 点击快捷入口
const handleEntry = (entry) => {
  console.log('快捷操作:', entry.name)
  if (entry.path && entry.path !== '/') {
    router.push(entry.path)
  }
}

// 获取今日统计数据
const loadTodayStatistics = async () => {
  try {
    const res = await getTodayStatistics()
    if (res.code === 200) {
      const data = res.data
      dataCards[0].value = data.todayOrders?.toString() || '0'
      dataCards[1].value = `¥${(data.todayRevenue || 0).toFixed(2)}`
    }
  } catch (error) {
    console.error('获取今日统计失败', error)
  }
}

// 获取总体统计数据
const loadOverallStatistics = async () => {
  try {
    const res = await getOverallStatistics()
    if (res.code === 200) {
      const data = res.data
      dataCards[2].value = data.totalOrders?.toString() || '0'
      dataCards[3].value = data.totalDishes?.toString() || '0'
    }
  } catch (error) {
    console.error('获取总体统计失败', error)
  }
}

// 获取订单趋势（本周数据）
const loadOrderTrend = async () => {
  try {
    const res = await getOrderTrend(7)
    if (res.code === 200 && res.data && res.data.length > 0) {
      const trendData = res.data
      const weekdays = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
      let maxValue = 1
      
      trendData.forEach((item, index) => {
        const count = item.orderCount || 0
        weekStats[index].value = count
        weekStats[index].day = weekdays[index] || item.date || '未知'
        if (count > maxValue) maxValue = count
      })
      
      weekStats.forEach(item => {
        item.percent = maxValue > 0 ? (item.value / maxValue * 100) : 0
      })
    }
  } catch (error) {
    console.error('获取订单趋势失败', error)
  }
}

// 获取最近订单
const loadRecentOrders = async () => {
  try {
    const res = await getOrderPage({ page: 1, pageSize: 5, sortBy: 'orderTime', sortDir: 'desc' })
    if (res.code === 200 && res.data.records && res.data.records.length > 0) {
      recentOrders.value = res.data.records.map(order => {
        const statusMap = {
          1: { icon: '🆕', class: 'new', text: '待接单' },
          2: { icon: '✅', class: 'confirmed', text: '已接单' },
          3: { icon: '🚴', class: 'delivering', text: '配送中' },
          4: { icon: '⭐', class: 'completed', text: '已完成' },
          5: { icon: '❌', class: 'cancelled', text: '已取消' }
        }
        const status = statusMap[order.status] || { icon: '📦', class: 'unknown', text: '未知' }
        
        return {
          orderNo: order.number || order.orderNo || '-',
          desc: `${order.consignee || '用户'} 购买了商品`,
          statusIcon: status.icon,
          statusClass: status.class,
          time: formatTime(order.orderTime)
        }
      })
    }
  } catch (error) {
    console.error('获取最近订单失败', error)
  }
}

// 格式化时间
const formatTime = (dateStr) => {
  if (!dateStr) return '未知'
  const now = new Date()
  const orderTime = new Date(dateStr)
  const diffMs = now.getTime() - orderTime.getTime()
  const diffMins = Math.floor(diffMs / 60000)
  
  if (diffMins < 1) return '刚刚'
  if (diffMins < 60) return `${diffMins}分钟前`
  if (diffMins < 1440) return `${Math.floor(diffMins / 60)}小时前`
  return `${Math.floor(diffMins / 1440)}天前`
}

onMounted(() => {
  loadTodayStatistics()
  loadOverallStatistics()
  loadOrderTrend()
  loadRecentOrders()
})
</script>

<style scoped>
.workbench-container {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
  font-family: 'PingFang SC', 'Microsoft YaHei', -apple-system, sans-serif;
}

.page-header {
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

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 16px;
  padding-left: 12px;
  border-left: 4px solid #409eff;
}

/* 今日数据概览 */
.data-overview {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.data-card {
  padding: 20px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  position: relative;
  overflow: hidden;
  color: #fff;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.data-card:hover {
  transform: translateY(-5px);
}

.data-icon {
  font-size: 40px;
  margin-right: 16px;
}

.data-info {
  display: flex;
  flex-direction: column;
}

.data-value {
  font-size: 28px;
  font-weight: 700;
}

.data-label {
  font-size: 14px;
  opacity: 0.9;
}

.data-trend {
  position: absolute;
  right: 20px;
  top: 20px;
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.2);
}

.data-trend.up {
  background: rgba(255, 255, 255, 0.3);
}

.data-trend.down {
  background: rgba(0, 0, 0, 0.2);
}

/* 待处理任务 */
.pending-tasks {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.task-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
}

.task-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px;
  border-radius: 12px;
  background: #fafafa;
  cursor: pointer;
  transition: all 0.3s ease;
}

.task-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.task-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-bottom: 8px;
}

.task-count {
  font-size: 20px;
  font-weight: 700;
  color: #2c3e50;
}

.task-name {
  font-size: 12px;
  color: #95a5a6;
}

/* 主体内容 */
.main-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
  margin-bottom: 24px;
}

/* 订单动态 */
.order-dynamic {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.order-list {
  display: flex;
  flex-direction: column;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.order-item:last-child {
  border-bottom: none;
}

.order-left {
  display: flex;
  align-items: center;
}

.order-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  margin-right: 12px;
}

.order-icon.new {
  background: #fff3e0;
  color: #e6a23c;
}

.order-icon.confirmed {
  background: #e3f2fd;
  color: #409eff;
}

.order-icon.delivering {
  background: #e8f5e9;
  color: #67c23a;
}

.order-icon.completed {
  background: #f3e5f5;
  color: #9c27b0;
}

.order-icon.cancelled {
  background: #ffebee;
  color: #f56c6c;
}

.order-icon.empty {
  background: #f5f5f5;
  color: #909399;
}

.order-info {
  display: flex;
  flex-direction: column;
}

.order-no {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
}

.order-desc {
  font-size: 12px;
  color: #95a5a6;
}

.order-time {
  font-size: 12px;
  color: #c0c4cc;
}

/* 快捷入口 */
.quick-entry {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.entry-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.entry-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px;
  border-radius: 12px;
  background: #fafafa;
  cursor: pointer;
  transition: all 0.3s ease;
}

.entry-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.entry-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-bottom: 8px;
}

.entry-name {
  font-size: 12px;
  color: #666;
}

/* 底部统计 */
.bottom-stats {
  display: grid;
  grid-template-columns: 1fr;
}

.stat-card {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.stat-chart {
  height: 120px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  margin-bottom: 20px;
}

.chart-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #fff;
  font-size: 16px;
  opacity: 0.8;
}

.stat-list {
  display: flex;
  justify-content: space-between;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 12%;
}

.stat-day {
  font-size: 12px;
  color: #95a5a6;
  margin-bottom: 8px;
}

.stat-bar {
  width: 100%;
  height: 40px;
  background: #f5f5f5;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 8px;
}

.stat-bar-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.5s ease;
}

.stat-value {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
}

@media screen and (max-width: 1200px) {
  .data-overview {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .main-content {
    grid-template-columns: 1fr;
  }
  
  .task-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}
</style>