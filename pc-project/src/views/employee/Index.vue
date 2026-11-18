<template>
    <el-card shadow="always">
        <el-header>
            <el-row :gutter="30">
                <el-col :span="7">
                    <el-input placeholder="请输入员工姓名" style="width:100%" v-model="queryParams.name" @clear="selectBtn"
                        clearable size="large"></el-input>
                </el-col>
                <el-col :span="6" style="margin-top:5px">
                    <el-button type="primary" @click="selectBtn" icon="Search">查询</el-button>
                    <el-button type="warning" icon="Loading" @click="resetBtn">重置</el-button>
                    <el-button type="primary" @click="addBtn" icon="Plus" color="#337ab7">添加</el-button>
                </el-col>
            </el-row>
        </el-header>
        <!-- 分隔线 -->
        <el-divider style="margin:0"></el-divider>
        <el-main>
            <!-- 表格 -->
            <el-table :data="employeeList" stripe>
                <el-table-column align="center" type="index" :index="indexMethod" label="序号" width="80" />
                <el-table-column align="center" prop="name" label="用户姓名"  />
                <el-table-column align="center" prop="username" label="用户昵称"  />
                <el-table-column align="center" prop="sex" label="性别" >
                    <template #default="scope">
                        <el-tag v-if="scope.row.sex == 1" type="success">男</el-tag>
                        <el-tag v-else type="danger">女</el-tag>
                    </template>
                </el-table-column>
                <el-table-column align="center" prop="phone" label="手机号码"  />
                <el-table-column align="center" prop="idNumber" label="身份证号码" width="180" />
                <el-table-column align="center" prop="status" label="状态" >
                    <template #default="scope">
                        <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0" active-text="启用"
                            inactive-text="禁用" @change="enableOrDisable(scope.row)" />
                    </template>
                </el-table-column>
                <el-table-column align="center" fixed="right" label="操作" width="200">
                    <template #default="scope">
                        <el-button type="primary" :icon="Edit" link @click="edit(scope.row)">修改</el-button>
                        <el-button type="danger" :icon="Delete" link @click="del(scope.row.id)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <!-- 分页插件 -->
            <div style="margin-top:15px">
                <el-pagination :current-page="pages.curPage" :page-size="pages.pageSize" :page-sizes="[2, 3, 4, 5, 6]"
                    :size="size" :disabled="disabled" :background="background"
                    layout="total, sizes, prev, pager, next, jumper" :total="pages.pageCount"
                    @size-change="handleSizeChange" @current-change="handleCurrentChange" />
            </div>
        </el-main>
    </el-card>

    <!-- 修改/添加对话框 -->
    <el-dialog v-model="dialog.dialogVisible" :title="dialog.tops" width="40%" align-center draggable
        :before-close="handleClose">
        <el-divider border-style="double" style="margin:0;" />
        <el-form label-position="right" label-width="auto" style="max-width:380px;margin:20px auto"
            class="demo-form-inline" ref="itemFormRef" :model="dialog.item" :rules="rules">
            <el-form-item label="员工账号：" prop="username">
                <el-input v-model="dialog.item.username" placeholder="请输入员工账号" />
            </el-form-item>
            <el-form-item label="员工姓名：" prop="name">
                <el-input v-model="dialog.item.name" placeholder="请输入员工姓名" />
            </el-form-item>
            <el-form-item label="性别：" prop="sex">
                <el-radio-group v-model="dialog.item.sex" style="width:320px">
                    <el-radio :label="1">男</el-radio>
                    <el-radio :label="0">女</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="员工手机：" prop="phone">
                <el-input v-model="dialog.item.phone" placeholder="请输入员工手机" />
            </el-form-item>
            <el-form-item label="身份证号：" prop="idNumber">
                <el-input v-model="dialog.item.idNumber" placeholder="请输入身份证号" />
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
import { Edit, Delete, Plus } from '@element-plus/icons-vue';
import { ref, reactive, computed, onMounted, nextTick } from 'vue';
import { getEmployeePageApi, saveEmployeeApi, updateEmployeeStatusApi, getEmployeeByIdApi, updateEmployeeApi, deleteEmployeeApi } from "@/api/employeeApi.js";
import { ElMessage, ElMessageBox } from 'element-plus';
// 查询参数
const queryParams = reactive({
    name: "",
    pageSize: "6", // 默认第一页
    curPage: "1"
});

const employeeList = ref([]); // 员工列表数据

// 分页属性封装
const pages = reactive({
    pageCount: 0, // 总记录数
    pageSize: 6, // 每页记录数
    curPage: 1, // 当前页
    totalPage: 0 // 总页数
});

// 获取员工列表
const getEmployeePageList = async () => {
    // 调用接口获取员工列表数据
    const res = await getEmployeePageApi(queryParams);
    console.log("-----res----->", res);
    // 判断接口返回数据是否成功
    if (res.code == 200) {
        employeeList.value = res.data.records; // 员工列表数据
        pages.pageCount = res.data.total; // 总记录数
        pages.totalPage = res.data.pages; // 总页数
        pages.curPage = res.data.current; // 当前页
        pages.pageSize = res.data.size; // 每页记录数
    } else {
        ElMessage.error(res.msg);
    }

};

// 页面加载时获取用户列表
onMounted(() => {
    getEmployeePageList();
});

// 计算属性
const indexMethod = computed(() => {
    return pages.curPage * pages.pageSize - pages.pageSize + 1;
});
// 选中页码
const handleCurrentChange = (curPage) => {
    pages.curPage = curPage;
    queryParams.curPage = curPage; // 参数pageSize是服务端接收页码参数名
    // 重新渲染表格
    getEmployeePageList();
};
const handleSizeChange = (size) => {
    pages.pageSize = size;
    queryParams.pageSize = size; // 参数pageSize是服务端接收页码参数名
    getEmployeePageList();
};
// 启用禁用
const enableOrDisable = async (row) => {
  console.log("---row---->", row);
    // 调用接口
    const res = await updateEmployeeStatusApi(row.status, row.id);
    if (res.code == 200) {
        ElMessage.success(res.msg);
        getEmployeePageList();
    } else {
        ElMessage.error(res.msg);
    }
};
// 查询按钮
const selectBtn = () => {
    queryParams.curPage = 1; // 查询时重置页码为1
    getEmployeePageList();
};
// 重置按钮
const resetBtn = () => {
    queryParams.name = "";
    queryParams.curPage = 1;
    getEmployeePageList();
};
// 模态框数据
const dialog = reactive({
    dialogVisible: false, // 模态框状态
    tops: "", // 模态框标题,
    item: {  // 模态框数据
        id: "",
        username: "",
        name: "",
        sex: "",
        phone: "",
        password: "",
        idNumber: "",
        status: "", // 状态 1：启用；2：停用
        createTime: "",
        updateTime: "",
        createUser: "",
        updateUser: "",
    }
});
// 校验规则
const rules = reactive({
    username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
    name: [{ required: true, message: "请输入真实姓名", trigger: "blur" }],
    phone: [{ required: true, message: "请输入手机号", trigger: "blur" },
    { min: 11, max: 11, message: '手机号必须是11个字符', trigger: 'blur' },
    ],
    idNumber: [{ required: true, message: "请输入身份证号", trigger: "blur" },
    { min: 18, max: 18, message: '身份证号必须是18个字符', trigger: 'blur' },
    ],
    sex: [{ required: true, message: "请选择性别", trigger: "change" }]
});
// 表单引用
const itemFormRef = ref(null);
// 对话框关闭处理
const handleClose = () => {
    dialog.dialogVisible = false;
    resetForm(); // 重置表单
};
// 重置表单
const resetForm = () => {
    if (itemFormRef.value) {
        itemFormRef.value.resetFields();
    }
};

// 添加按钮
const addBtn = () => {
    dialog.tops = "添加员工";
    dialog.dialogVisible = true;
    resetForm();  // 重置表单
};
// 修改按钮
const edit = async (row) => {
    dialog.tops = "修改员工";
    dialog.dialogVisible = true;
    // 根据id查询员工信息
    const res = await getEmployeeByIdApi(row.id);
    //console.log("-----res----->", res);
    if (res.code == 200) {
        dialog.item = res.data; // 将查询到的员工信息赋值给dialog.item
        dialog.item.sex = Number(res.data.sex) // 将性别转换成数字
    } else {
        ElMessage.error(res.msg);
    }
};
// 保存或更新
const saveOrUpdate = () => {
    // 表单校验
    itemFormRef.value.validate(async (valid) => {
        if (valid) {
            let res;
            if (dialog.item.id) {
                // 编辑
                res = await updateEmployeeApi(dialog.item);
            } else {
                // 添加
                res = await saveEmployeeApi(dialog.item);
            }
            if (res.code == 200) {
                ElMessage.success(res.msg);
                getEmployeePageList();
                dialog.dialogVisible = false;
            } else {
                ElMessage.error(res.msg);
            }
        } else {
            ElMessage.error("表单校验失败");
        }
    });
};
// 取消按钮
const cancel = () => {
    dialog.dialogVisible = false;
}
// 删除按钮
const del = (id) => {
    ElMessageBox.confirm('确定要删除吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(async () => {
        // 调用接口删除员工
        const res = await deleteEmployeeApi(id);
        if (res.code == 200) {
            ElMessage.success(res.msg);
            getEmployeePageList();
        } else {
            ElMessage.error(res.msg);
        }
    }).catch(() => {
        ElMessage.info('已取消删除');
    });
};
</script>

<style lang="scss" scoped></style>