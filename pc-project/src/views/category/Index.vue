<template>
  <el-card shadow="always">
    <!-- 头部：查询和操作按钮 -->
    <el-header>
      <el-row :gutter="10">
        <el-col :span="12">
          <label>分类名称：</label>
          <el-input 
            v-model="queryParams.name" 
            placeholder="请填写分类名称" 
            style="width: 30%" 
            clearable 
            @clear="selectBtn" 
            size="large" 
          />
          <label style="margin-left: 15px;">分类类型：</label>
          <el-select 
            v-model="queryParams.type" 
            placeholder="请选择" 
            clearable 
            style="width: 30%" 
            @clear="selectBtn" 
            size="large"
          >
            <el-option 
              v-for="item in options" 
              :key="item.value" 
              :label="item.label" 
              :value="item.value" 
            />
          </el-select>
        </el-col>
        <el-col :span="12" style="margin-top:5px">
          <el-button type="primary" icon="Search" @click="selectBtn">查询</el-button>
          <el-button type="warning" icon="Loading" @click="resetBtn">重置</el-button>
          <el-button type="primary" icon="Plus" @click="addDishBtn" color="#337ab7">新增菜品分类</el-button>
          <el-button type="primary" icon="Plus" @click="addSetmealBtn" color="#337ab7">新增套餐分类</el-button>
        </el-col>
      </el-row>
    </el-header>

    <!-- 分隔线 -->
    <el-divider style="margin:0"></el-divider>

    <!-- 主体：表格 -->
    <el-main>
      <el-table :data="categoryList" stripe>
        <el-table-column align="center" type="index" :index="indexMethod" label="序号" width="90" />
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="type" label="分类类型">
          <template #default="scope">
            <span>{{ scope.row.type == '1' ? '菜品分类' : '套餐分类' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" />
        <el-table-column label="状态">
          <template #default="scope">
            <el-tag v-if="scope.row.status == '1'" type="success">启用</el-tag>
            <el-tag v-else type="danger">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="操作时间" />
        <el-table-column label="操作" width="200" align="center">
          <template #default="scope">
            <el-button type="success" plain size="small" @click="edit(scope.row)">修改</el-button>
            <el-button type="danger" plain size="small" @click="del(scope.row)">删除</el-button>
            <el-button type="warning" plain size="small" @click="enableOrDisable(scope.row)">
              {{ scope.row.status == '1' ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页插件 -->
      <div style="margin-top:15px">
        <el-pagination
          :current-page="pages.curPage"
          :page-size="pages.pageSize"
          :page-sizes="[2, 3, 4, 5, 6, 8, 10]"
          :background="background"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pages.pageCount"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-main>
  </el-card>

  <!-- 修改/添加对话框 -->
  <el-dialog 
    v-model="dialog.dialogVisible" 
    :title="dialog.tops" 
    width="40%" 
    align-center 
    draggable 
    :before-close="handleClose"
  >
    <el-divider border-style="double" style="margin:0;" />
    <el-form 
      label-position="right" 
      label-width="auto" 
      style="max-width:380px;margin:20px auto" 
      class="demo-form-inline" 
      ref="itemFormRef" 
      :model="dialog.item" 
      :rules="rules"
    >
      <el-form-item label="分类名称：" prop="name">
        <el-input v-model="dialog.item.name" placeholder="请输入分类名称" />
      </el-form-item>
      <el-form-item label="分类类型：" prop="type">
        <el-select v-model="dialog.item.type" placeholder="请选择分类类型">
          <el-option 
            v-for="option in options" 
            :key="option.value" 
            :label="option.label" 
            :value="option.value" 
          />
        </el-select>
      </el-form-item>
      <el-form-item label="排序：" prop="sort">
        <el-input v-model="dialog.item.sort" placeholder="请输入排序" />
      </el-form-item>
    </el-form>
    <el-divider border-style="double" style="margin:0;" />
    <template #footer>
      <span class="dialog-footer" style="padding-top: 0px">
        <el-button type="primary" @click="saveOrUpdate">保存</el-button>
        <el-button type="danger" @click="cancel">取消</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { Edit, Delete, Plus } from '@element-plus/icons-vue'
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { 
  getCategoryPage, 
  deleteCategory, 
  editCategory, 
  addCategory, 
  enableOrDisableCategory, 
  getCategoryList 
} from '@/api/categoryApi.js'
import { ElMessage, ElMessageBox } from 'element-plus'

// 查询参数
const queryParams = reactive({
  name: '', // 分类名称
  type: '', // 分类类型
  pageSize: '6', // 默认每页6条
  curPage: '1' // 默认第一页
})

// 选项数据
const options = ref([
  { value: '1', label: '菜品分类' },
  { value: '2', label: '套餐分类' }
])

const categoryList = ref([]) // 分类列表数据

// 分页属性封装
const pages = reactive({
  pageCount: 0, // 总记录数
  pageSize: 6, // 每页记录数
  curPage: 1, // 当前页
  totalPage: 0 // 总页数
})

// 获取分类列表
const getCategoryPageList = async () => {
  const res = await getCategoryPage(queryParams)
  console.log('---res---', res)
  if (res.code === 200) {
    categoryList.value = res.data.records // 分类列表数据
    pages.pageCount = res.data.total // 总记录数
    pages.pageSize = res.data.size // 每页记录数
    pages.curPage = res.data.current // 当前页
    pages.totalPage = res.data.pages // 总页数
  } else {
    ElMessage.error(res.msg)
  }
}

// 计算属性：序号
const indexMethod = computed(() => {
  return pages.curPage * pages.pageSize - pages.pageSize + 1
})

// 初始化加载分类列表
onMounted(() => {
  getCategoryPageList()
})

// 选中页码
const handleCurrentChange = (curPage) => {
  pages.curPage = curPage
  queryParams.curPage = curPage // 参数pageSize是服务端接收页码参数名
  getCategoryPageList()
}

// 每页条数变化
const handleSizeChange = (size) => {
  pages.pageSize = size
  queryParams.pageSize = size // 参数pageSize是服务端接收页码参数名
  getCategoryPageList()
}

// 查询按钮
const selectBtn = () => {
  queryParams.curPage = 1 // 查询时重置页码为1
  getCategoryPageList()
}

// 重置按钮
const resetBtn = () => {
  queryParams.name = ''
  queryParams.type = ''
  queryParams.curPage = 1
  getCategoryPageList()
}

// 模态框数据
const dialog = reactive({
  dialogVisible: false, // 模态框状态
  tops: '', // 模态框标题
  item: { // 模态框数据
    id: '', // 分类id
    type: '', // 分类类型
    name: '', // 分类名称
    sort: '' // 排序
  }
})

// 校验规则
const rules = reactive({
  type: [{ required: true, message: '请输入分类类型', trigger: 'blur' }],
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  sort: [{ required: true, message: '请输入排序', trigger: 'blur' }]
})

// 表单引用
const itemFormRef = ref(null)

// 对话框关闭处理
const handleClose = () => {
  dialog.dialogVisible = false
  resetForm() // 重置表单
}

// 重置表单
const resetForm = () => {
  if (itemFormRef.value) {
    itemFormRef.value.resetFields()
  }
}

// 添加菜品按钮
const addDishBtn = () => {
  dialog.tops = '添加菜品'
  dialog.dialogVisible = true
  dialog.item.id = '' // 确保id为空
  resetForm() // 重置表单
}

// 添加套餐按钮
const addSetmealBtn = () => {
  dialog.tops = '添加套餐'
  dialog.dialogVisible = true
  dialog.item.id = '' // 确保id为空
  resetForm() // 重置表单
}

// 修改按钮
const edit = (row) => {
  dialog.tops = '修改分类'
  dialog.dialogVisible = true
  // 初始化模态框数据
  nextTick(() => {
    dialog.item.id = row.id
    dialog.item.type = row.type
    dialog.item.name = row.name
    dialog.item.sort = row.sort
  })
}

// 保存或更新
const saveOrUpdate = async () => {
  // 表单校验
  itemFormRef.value.validate(async (valid) => {
    if (valid) {
      let res
      if (dialog.item.id) {
        // 编辑
        res = await editCategory(dialog.item)
      } else {
        // 添加
        res = await addCategory(dialog.item)
      }
      if (res.code === 200) {
        ElMessage.success(res.msg)
        getCategoryPageList()
        dialog.dialogVisible = false
      } else {
        ElMessage.error(res.msg)
      }
    } else {
      ElMessage.error('表单校验失败')
    }
  })
}

// 取消按钮
const cancel = () => {
  dialog.dialogVisible = false
}

// 删除按钮
const del = (row) => {
  ElMessageBox.confirm(`确定要删除【${row.name}】吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      // 调用接口删除
      const res = await deleteCategory(row.id)
      if (res.code === 200) {
        ElMessage.success(res.msg)
        getCategoryPageList()
      } else {
        ElMessage.error(res.msg)
      }
    })
    .catch(() => {
      ElMessage.info('已取消删除')
    })
}

// 启用/禁用按钮
const enableOrDisable = async (row) => {
  // 提示框
  ElMessageBox.confirm(`确定要${row.status === '1' ? '禁用' : '启用'}【${row.name}】吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      // 调用接口启用/禁用
      const res = await enableOrDisableCategory({
        id: row.id,
        status: row.status === '1' ? 0 : 1
      })
      if (res.code === 200) {
        ElMessage.success(res.msg)
        getCategoryPageList()
      }
    })
    .catch(() => {
      ElMessage.info('取消操作')
    })
}
</script>

<style lang="scss" scoped>
</style>