<template>
    <el-card shadow="always">
        <el-header>
            <el-row :gutter="10">
                <el-col :span="14">
                    <label>套餐名称：</label>
                    <el-input v-model="queryParams.name" placeholder="填写套餐名称" style="width: 20%" clearable @clear="selectBtn" />
                    <label style="margin-left: 15px;">套餐分类：</label>
                    <el-select v-model="queryParams.categoryId" placeholder="请选择" clearable style="width: 20%" @clear="selectBtn">
                        <el-option v-for="item in setmealCategoryList" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                    <label style="margin-left: 15px;">售卖状态：</label>
                    <el-select v-model="queryParams.status" placeholder="请选择" clearable style="width: 20%" @clear="selectBtn">
                        <el-option v-for="item in saleStatus" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-col>
                <el-col :span="10" style="margin-top:5px">
                    <el-button type="primary" icon="Search" @click="selectBtn">查询</el-button>
                    <el-button type="warning" icon="Loading" @click="resetBtn">重置</el-button>
                    <el-button type="primary" icon="Delete" @click="delSetmealBtn" color="#337ab7">批量删除</el-button>
                    <el-button type="primary" icon="Plus" @click="addSetmealBtn" color="#337ab7">新增套餐</el-button>
                </el-col>
            </el-row>
        </el-header>
        <el-divider style="margin:0"></el-divider>
        <el-main>
            <el-table :data="setmealList" stripe class="tableBox" @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55" />
                <el-table-column prop="name" label="套餐名称" />
                <el-table-column prop="image" label="套餐图片">
                    <template #default="scope">
                        <el-image style="width: 80px; height: 45px; border: none; cursor: pointer;margin-top: 8px;" :src="scope.row.image" fit="fill" />
                    </template>
                </el-table-column>
                <el-table-column prop="categoryName" label="套餐分类" />
                <el-table-column label="套餐价格">
                    <template #default="scope">
                        <span style="margin-right: 10px">￥{{ ((scope.row.price).toFixed(2) * 100 / 100).toFixed(2) }}</span>
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
                            {{ scope.row.status == '1' ? '停售' : '启售' }}
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-main>
        <div style="margin-top:15px">
            <el-pagination :current-page="pages.curPage" :page-size="pages.pageSize" :page-sizes="[2, 3, 4, 5, 6, 8, 10]" :layout="layout" :total="pages.pageCount" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
        </div>
    </el-card>
</template>

<script setup>
import { Edit, Delete, Plus, Loading, Search } from '@element-plus/icons-vue';
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getSetmealPage, deleteSetmeal, addSetmeal, querySetmealById, setmealStatusByStatus, getCategoryList } from "@/api/setmealApi.js";
import { useRouter } from 'vue-router';

const queryParams = reactive({
    name: "",
    categoryId: "",
    status: "",
    pageSize: "6",
    curPage: "1"
});

const pages = reactive({
    pageCount: 0,
    pageSize: 6,
    curPage: 1,
    totalPage: 0
});

const layout = 'total, sizes, prev, pager, next, jumper';

const setmealCategoryList = ref([]);
const setmealList = ref([]);
const selectedIds = ref([]);

const saleStatus = ref([{ value: 0, label: '停售' }, { value: 1, label: '启售' }])

const router = useRouter();

const getSetmealCategoryList = async () => {
    const res = await getCategoryList({ type: 2 });
    if (res.code === 200) {
        setmealCategoryList.value = res.data.map(item => ({
            value: item.id,
            label: item.name
        }));
    } else {
        ElMessage.error(res.msg || "获取套餐分类列表失败");
    }
}

const getSetmealPageList = async () => {
    const res = await getSetmealPage(queryParams);
    if (res.code === 200) {
        setmealList.value = res.data.records;
        pages.pageCount = res.data.total;
        pages.pageSize = res.data.size;
        pages.curPage = res.data.current;
        pages.totalPage = res.data.pages;
    } else {
        ElMessage.error(res.msg || "获取套餐分页列表失败");
    }
};

onMounted(() => {
    getSetmealCategoryList();
    getSetmealPageList();
});

const handleCurrentChange = (curPage) => {
    pages.curPage = curPage;
    queryParams.curPage = curPage;
    getSetmealPageList();
};

const handleSizeChange = (size) => {
    pages.pageSize = size;
    queryParams.pageSize = size;
    getSetmealPageList();
};

const selectBtn = () => {
    queryParams.curPage = 1;
    getSetmealPageList();
};

const resetBtn = () => {
    queryParams.name = "";
    queryParams.categoryId = "";
    queryParams.status = "";
    queryParams.curPage = 1;
    getSetmealPageList();
};

const addSetmealBtn = () => {
    router.push("/setmeal/addorupdate");
}

const edit = (row) => {
    router.push({ path: '/setmeal/addorupdate', query: { id: row.id, page: pages.curPage } });
}

const handleSelectionChange = (selection) => {
    selectedIds.value = selection.map(item => item.id);
};

const del = (row) => {
    ElMessageBox.confirm(`确定要删除套餐"${row.name}"吗？`, '删除确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(async () => {
        try {
            const idArray = [row.id];
            const queryString = idArray.map((id) => `ids=${id}`).join("&");
            const res = await deleteSetmeal(queryString);
            if (res.code === 200) {
                ElMessage.success('删除成功');
                getSetmealPageList();
            }
        } catch (error) {
            ElMessage.error("异常信息：起售中的套餐不能删除");
        }
    }).catch(() => {
        ElMessage.info('已取消删除');
    });
};

const delSetmealBtn = () => {
    if (selectedIds.value.length === 0) {
        ElMessage.warning('请先选择要删除的套餐');
        return;
    }

    ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个套餐吗？`, '批量删除确认')
        .then(async () => {
            try {
                const idArray = Array.isArray(selectedIds.value) ? selectedIds.value : [selectedIds.value];
                const queryString = idArray.map((id) => `ids=${id}`).join("&");
                const res = await deleteSetmeal(queryString);
                if (res.code === 200) {
                    ElMessage.success(`成功删除 ${selectedIds.value.length} 个套餐`);
                    selectedIds.value = [];
                    getSetmealPageList();
                }
            } catch (error) {
                ElMessage.error('删除套餐失败，请稍后再试');
                return;
            }
        }).catch(() => {
            ElMessage.info('已取消删除');
        });
};

const enableOrDisable = async (row) => {
    const newStatus = row.status == 1 ? 0 : 1;
    const action = newStatus == 1 ? '启售' : '停售';

    ElMessageBox.confirm(`确定要${action}套餐"${row.name}"吗？`)
        .then(async () => {
            const res = await setmealStatusByStatus({
                id: row.id,
                status: newStatus
            });
            if (res.code === 200) {
                ElMessage.success(`${action}成功`);
                getSetmealPageList();
            }
        });
};
</script>

<style lang="scss" scoped>
</style>