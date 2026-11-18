<template>
    <div class="content">
        <el-dropdown>
            <span class="el-dropdown-link">
                <img class="userimg" src="@/assets/01.jpg" />
            </span>
            <template #dropdown>
                <el-dropdown-menu>
                    <el-dropdown-item @click="upBtn">修改密码</el-dropdown-item>
                    <el-dropdown-item @click="loginBtn">退出登录</el-dropdown-item>
                </el-dropdown-menu>
            </template>
        </el-dropdown>
        <span class="title">
            欢迎：{{ employeeInfo?.name }}
        </span>
    </div>

    <!-- 修改密码弹框 -->
    <el-dialog v-model="dialogVisible" title="修改密码" width="500" height="260" :before-close="handleClose">
        <el-form :model="addModel" ref="form" :rules="rules" label-width="80px" :inline="false" size="default">
            <el-form-item prop="oldPassword" label="原密码">
                <el-input v-model="addModel.oldPassword"></el-input>
            </el-form-item>
            <el-form-item prop="password" label="新密码">
                <el-input v-model="addModel.password"></el-input>
            </el-form-item>
            <el-form-item prop="newPassword" label="确定密码">
                <el-input v-model="addModel.newPassword"></el-input>
            </el-form-item>
        </el-form>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="commit">
                    修改
                </el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import { ref, reactive, onMounted } from 'vue';
import { ElMessageBox, ElMessage, FormInstance } from 'element-plus';
import { getSessionStorage, removeSessionStorage } from '@/views/common.js';
import { updatePasswordApi } from '@/api/employeeApi.js'

//弹框属性
//表单ref属性
const router = useRouter()

const employeeInfo = ref(null)

//表单ref属性
const form = ref(null);

/**
 * 加载用户信息
 */
const loadEmployeeInfo = () => {
    employeeInfo.value = getSessionStorage('user')
  console.log("--->",employeeInfo.value)
}

// 组件挂载时加载用户信息
onMounted(() => {
  loadEmployeeInfo()
})

const dialogVisible = ref(false);
//退出登录按钮
const loginBtn = async () => {
    ElMessageBox.confirm('确定退出系统吗?', '系统提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        }).then(async () => {
            // 调用退出登录
            removeSessionStorage('user')
            // 跳转到登录页
            router.push('/login')
            //跳转登录
            //router.push({ name: 'login' })
        }).catch(() => {
            ElMessage.info("取消退出登录！！！")
        })
}
//表单绑定的对象
const addModel = reactive({
    userId: '',
    password: '',
    oldPassword: '',
    newPassword: ''
})

//修改密码
const upBtn = () => {
    addModel.userId = employeeInfo.value.id
    dialogVisible.value = true;
};

//提交表单
const commit = () => {
    //新密码和确定密码判断
    if (addModel.newPassword != addModel.password) {
        ElMessage.warning("新密码和确定密码不一致!");
        return;
    }
    form.value.validate(async (valid) => {
        if (valid) {
            let res = await updatePasswordApi(addModel);
            if (res.code === 200) {
                ElMessage.success(res.msg);
                //清空缓存
                removeSessionStorage('user')
                //跳转登录
                router.push('/login');
                dialogVisible.value = false;
            } else {
                ElMessage.error(res.msg);
            }
        }
    });
}

//关闭对话框
const handleClose = () => {
    dialogVisible.value = false;
}
//表单验证规则
const rules = reactive({
    oldPassword: [{ required: "true", trigger: "change", message: "请输入原密码", }],
    password: [{ required: "true", trigger: "change", message: "请输入新密码", }],
    newPassword: [{ required: "true", trigger: "change", message: "请输入确定密码", }],
});


</script>

<style scoped lang="scss">
.content {
    display: flex;
    justify-content: center;
    align-items: center;

    .title {
        color: white;
        margin-left: 20px;
        margin-right: 18px;
    }

    .el-dropdown-link {
        .userimg {
            height: 40px;
            width: 40px;
            border-radius: 50%;
        }
    }
}
</style>