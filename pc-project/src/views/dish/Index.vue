<template>
  <el-card shadow="always">
    <el-header>
      <el-row :gutter="10">
        <el-col :span="14">
          <label>菜品名称：</label>
          <el-input
            v-model="queryParams.name"
            placeholder="填写菜品名称"
            style="width: 20%"
            clearable
            @clear="selectBtn"
          />
          <label style="margin-left: 15px;">菜品分类：</label>
          <el-select
            v-model="queryParams.categoryId"
            placeholder="请选择"
            clearable
            style="width: 20%"
            @clear="selectBtn"
          >
            <el-option
              v-for="item in dishCategoryList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <label style="margin-left: 15px;">售卖状态：</label>
          <el-select
            v-model="queryParams.status"
            placeholder="请选择"
            clearable
            style="width: 20%"
            @clear="selectBtn"
          >
            <el-option
              v-for="item in saleStatus"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-col>
        <el-col :span="10" style="margin-top: 5px">
          <el-button type="primary" icon="Search" @click="selectBtn">查询</el-button>
          <el-button type="warning" icon="Loading" @click="resetBtn">重置</el-button>
          <el-button type="primary" icon="Delete" @click="delDishBtn" color="#337ab7">批量删除</el-button>
          <el-button type="primary" icon="Plus" @click="addDishBtn" color="#337ab7">新增菜品</el-button>
        </el-col>
      </el-row>
    </el-header>

    <el-divider style="margin: 0" />

    <el-main>
      <!-- 表格 -->
      <el-table
        :data="dishList"
        stripe
        class="tableBox"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="菜品名称" />
        <el-table-column prop="image" label="图片">
          <template #default="scope">
            <el-image
              style="width: 80px; height: 45px; border: none; cursor: pointer; margin-top: 8px;"
              :src="scope.row.image"
              fit="fill"
            />
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="菜品分类" />
        <el-table-column label="售价">
          <template #default="scope">
            <span style="margin-right: 10px">¥{{ (scope.row.price).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="售卖状态">
          <template #default="scope">
            <el-tag v-if="scope.row.status == 1" type="success">启售</el-tag>
            <el-tag v-else type="danger">停售</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="最后操作时间" width="200" />
        <el-table-column label="操作" width="200" align="center">
          <template #default="scope">
            <el-button type="success" plain size="small" @click="edit(scope.row)">修改</el-button>
            <el-button type="danger" plain size="small" @click="del(scope.row)">删除</el-button>
            <el-button type="warning" plain size="small" @click="enableOrDisable(scope.row)">
              {{ scope.row.status == "1" ? "停售" : "启售" }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-main>

    <!-- 分页插件 -->
    <div style="margin-top: 15px">
      <el-pagination
        :current-page="pages.curPage"
        :page-size="pages.pageSize"
        :page-sizes="[2, 3, 4, 5, 6, 8, 10]"
        :background="true"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pages.pageCount"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </el-card>
</template>

<script setup>
import { Edit, Delete, Plus, Loading, Search } from "@element-plus/icons-vue";
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getDishPage,
  deleteDish,
  dishStatusByStatus,
  getCategoryList,
} from "@/api/dishApi.js";
import { useRouter } from "vue-router";

// 查询参数
const queryParams = reactive({
  name: "",
  categoryId: "",
  status: "",
  pageSize: "6",
  curPage: "1",
});

// 菜品分类列表
const dishCategoryList = ref([]);

// 菜品列表
const dishList = ref([]);

// 选项数据
const saleStatus = ref([
  { value: 0, label: "停售" },
  { value: 1, label: "启售" },
]);

// 分页属性封装
const pages = reactive({
  pageCount: 0,
  pageSize: 6,
  curPage: 1,
  totalPage: 0,
});

// 存储选中的菜品ID
const selectedIds = ref([]);

const router = useRouter();

// 获取菜品分类下拉数据
const getDishCategoryList = async () => {
  const res = await getCategoryList({ type: 1 });
  console.log("---res---", res);
  if (res.code === 200) {
    dishCategoryList.value = res.data.map((item) => ({
      value: item.id,
      label: item.name,
    }));
  } else {
    ElMessage.error(res.message || "获取菜品分类列表失败");
  }
};

// 获取菜品分页列表
const getDishPageList = async () => {
  const res = await getDishPage(queryParams);
  console.log("---getDishPageList---res---", res);
  if (res.code === 200) {
    dishList.value = res.data.records;
    pages.pageCount = res.data.total;
    pages.pageSize = res.data.size;
    pages.curPage = res.data.current;
    pages.totalPage = res.data.pages;
  } else {
    ElMessage.error(res.message || "获取菜品分页列表失败");
  }
};

// 初始化加载
onMounted(() => {
  getDishCategoryList();
  getDishPageList();
});

// 选中页码
const handleCurrentChange = (curPage) => {
  pages.curPage = curPage;
  queryParams.curPage = curPage;
  getDishPageList();
};

const handleSizeChange = (size) => {
  pages.pageSize = size;
  queryParams.pageSize = size;
  getDishPageList();
};

// 查询按钮
const selectBtn = () => {
  queryParams.curPage = 1;
  getDishPageList();
};

// 重置按钮
const resetBtn = () => {
  queryParams.name = "";
  queryParams.categoryId = "";
  queryParams.status = "";
  queryParams.curPage = 1;
  getDishPageList();
};

// 新增菜品按钮
const addDishBtn = () => {
  router.push("/dish/add");
};

// 修改菜品按钮
const edit = (row) => {
  console.log(row);
  router.push({ path: "/dish/add", query: { id: row.id } });
};

// 处理表格选中变化
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map((item) => item.id);
};

// 单个删除功能
const del = (row) => {
  ElMessageBox.confirm(`确定要删除菜品"${row.name}"吗？`, "删除确认", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        console.log("删除菜品ID:", row.id);
        const res = await deleteDish([row.id]);
        console.log("删除响应:", res);
        if (res.code === 200) {
          ElMessage.success("删除成功");
          getDishPageList();
        } else {
          ElMessage.error(res.message || "删除失败");
        }
      } catch (error) {
        console.error("删除错误:", error);
        const errorMsg = error.response?.data?.message || error.message || "删除失败，请检查网络连接";
        ElMessage.error(errorMsg);
      }
    })
    .catch(() => {
      ElMessage.info("已取消删除");
    });
};

// 实现批量删除功能
const delDishBtn = () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning("请先选择要删除的菜品");
    return;
  }
  ElMessageBox.confirm(`确定要删除选中的${selectedIds.value.length}个菜品吗？`, "批量删除确认")
    .then(async () => {
      try {
        const res = await deleteDish(selectedIds.value);
        if (res.code === 200) {
          ElMessage.success(`成功删除 ${selectedIds.value.length} 个菜品`);
          selectedIds.value = [];
          getDishPageList();
        }
      } catch (error) {
        console.error("异常信息：起售中的菜品不能删除");
        ElMessage.error("删除菜品失败，请稍后再试");
        return;
      }
    })
    .catch(() => {
      ElMessage.info("已取消删除");
    });
};

// 实现启售/停售功能
const enableOrDisable = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1;
  const action = newStatus === 1 ? "启售" : "停售";
  ElMessageBox.confirm(`确定要${action}菜品"${row.name}"吗？`)
    .then(async () => {
      const res = await dishStatusByStatus({
        id: row.id,
        status: newStatus,
      });
      if (res.code === 200) {
        ElMessage.success(`${action}成功`);
        getDishPageList();
      }
    })
    .catch(() => {
      ElMessage.info("已取消操作");
    });
};
</script>

<style lang="scss" scoped>
.tableBox {
  margin-top: 10px;
}
</style>