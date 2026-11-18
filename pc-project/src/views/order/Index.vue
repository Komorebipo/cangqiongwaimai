<template>
  <div class="order-container">
    <div class="page-header">
      <div class="header-left">
        <h1 class="title">订单管理</h1>
        <p class="subtitle">查看和处理客户订单</p>
      </div>
    </div>

    <div class="status-tabs">
      <el-radio-group v-model="statusTab" @change="handleTabChange">
        <el-radio-button label="all">全部 ({{ stats.all }})</el-radio-button>
        <el-radio-button :label="1">待接单 ({{ stats.pending }})</el-radio-button>
        <el-radio-button :label="2">已接单 ({{ stats.accepted }})</el-radio-button>
        <el-radio-button :label="3">配送中 ({{ stats.delivering }})</el-radio-button>
        <el-radio-button :label="4">已完成 ({{ stats.completed }})</el-radio-button>
        <el-radio-button :label="5">已取消 ({{ stats.canceled }})</el-radio-button>
      </el-radio-group>
    </div>

    <div class="search-bar">
      <el-input v-model="queryParams.keyword" placeholder="搜索订单号/客户姓名" prefix-icon="Search" clearable style="width: 220px; margin-right: 12px;" @clear="handleSearch" />
      <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" style="width: 260px; margin-right: 12px;" value-format="YYYY-MM-DD HH:mm:ss" @change="handleDateChange" />
      <el-button type="primary" @click="handleSearch">筛选</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <div class="table-container">
      <el-table :data="orderList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="number" label="订单号" width="180" />
        <el-table-column prop="consignee" label="客户姓名" width="100" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="address" label="配送地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="amount" label="订单金额" width="100" align="center">
          <template #default="{ row }">
            <span class="amount">¥{{ row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusName(row.status) }}</el-tag>
            <div v-if="row.status === 5 && (row.cancelReason || row.cancel_reason)" class="cancel-reason">{{ row.cancelReason || row.cancel_reason }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="orderTime" label="下单时间" width="170" />
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
            <el-button link type="success" v-if="row.status === 1" @click="handleConfirm(row)">接单</el-button>
            <el-button link type="warning" v-if="row.status === 2" @click="handleDeliver(row)">发货</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-container">
      <el-pagination
        v-model:current-page="queryParams.curPage"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[2, 3, 4, 5, 6, 7, 8, 9, 10]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <el-dialog v-model="detailVisible" title="订单详情" width="600px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单号">{{ currentOrder.number }}</el-descriptions-item>
        <el-descriptions-item label="客户姓名">{{ currentOrder.consignee }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentOrder.phone }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ currentOrder.amount }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(currentOrder.status)">{{ getStatusName(currentOrder.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ currentOrder.orderTime }}</el-descriptions-item>
        <el-descriptions-item label="取消原因">
          <span v-if="currentOrder.status === 5">{{ currentOrder.cancelReason || currentOrder.cancel_reason || '未填写取消原因' }}</span>
          <span v-else class="text-muted">-</span>
        </el-descriptions-item>
        <el-descriptions-item label="配送地址" :span="2">{{ currentOrder.address }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderPage, getOrderStats, getOrderById, updateOrderStatus } from '@/api/orderApi.js'

const loading = ref(false)
const statusTab = ref('all')
const dateRange = ref([])
const orderList = ref([])
const total = ref(0)
const detailVisible = ref(false)
const currentOrder = ref(null)

const stats = reactive({
  all: 0,
  pending: 0,
  accepted: 0,
  delivering: 0,
  completed: 0,
  canceled: 0
})

const queryParams = reactive({
  keyword: '',
  status: null,
  beginTime: '',
  endTime: '',
  curPage: 1,
  pageSize: 10
})

const getStatusName = (status) => {
  const map = { 1: '待接单', 2: '已接单', 3: '配送中', 4: '已完成', 5: '已取消' }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = { 1: 'warning', 2: 'primary', 3: 'info', 4: 'success', 5: 'danger' }
  return map[status] || 'info'
}

const loadOrderStats = async () => {
  try {
    const res = await getOrderStats()
    if (res.code === 200) {
      Object.assign(stats, res.data)
    }
  } catch (error) {
    console.error('获取订单统计失败', error)
  }
}

const loadOrderList = async () => {
  loading.value = true
  try {
    const params = { ...queryParams }
    if (statusTab.value !== 'all') {
      params.status = statusTab.value
    } else {
      params.status = null
    }

    const res = await getOrderPage(params)
    if (res.code === 200) {
      orderList.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  queryParams.curPage = 1
  loadOrderList()
}

const handleSearch = () => {
  queryParams.curPage = 1
  loadOrderList()
}

const handleReset = () => {
  queryParams.keyword = ''
  queryParams.status = null
  queryParams.beginTime = ''
  queryParams.endTime = ''
  dateRange.value = []
  statusTab.value = 'all'
  queryParams.curPage = 1
  loadOrderList()
}

const handleDateChange = (val) => {
  if (val && val.length === 2) {
    queryParams.beginTime = val[0]
    queryParams.endTime = val[1]
  } else {
    queryParams.beginTime = ''
    queryParams.endTime = ''
  }
}

const handleDetail = async (row) => {
  try {
    const res = await getOrderById(row.id)
    if (res.code === 200) {
      currentOrder.value = res.data
      detailVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取订单详情失败')
  }
}

const handleConfirm = (row) => {
  ElMessageBox.confirm(`确定要接单吗？`, '接单确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await updateOrderStatus(2, row.id)
      if (res.code === 200) {
        ElMessage.success('接单成功')
        loadOrderList()
        loadOrderStats()
      }
    } catch (error) {
      ElMessage.error('接单失败')
    }
  }).catch(() => {})
}

const handleDeliver = (row) => {
  ElMessageBox.confirm(`确定要发货吗？`, '发货确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await updateOrderStatus(3, row.id)
      if (res.code === 200) {
        ElMessage.success('发货成功')
        loadOrderList()
        loadOrderStats()
      }
    } catch (error) {
      ElMessage.error('发货失败')
    }
  }).catch(() => {})
}

const handleSizeChange = (val) => {
  queryParams.pageSize = val
  loadOrderList()
}

const handleCurrentChange = (val) => {
  queryParams.curPage = val
  loadOrderList()
}

onMounted(() => {
  loadOrderStats()
  loadOrderList()
})
</script>

<style scoped>
.order-container {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
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

.status-tabs {
  margin-bottom: 20px;
}

.search-bar {
  display: flex;
  margin-bottom: 20px;
  align-items: center;
}

.table-container {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
}

.amount {
  color: #f56c6c;
  font-weight: 600;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  padding: 16px 0;
}

.cancel-reason {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.text-muted {
  color: #909399;
}
</style>