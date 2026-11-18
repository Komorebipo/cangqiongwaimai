<template>
    <div class="login">
        <el-form :inline="false" :model="loginForm" class="loginForm" ref="loginFormRef" :rules="rules" size="large">
            <el-form-item>
                <div class="loginTile">云端后端管理</div>
            </el-form-item>
            <el-form-item prop="username">
                <el-input v-model="loginForm.username" placeholder="请输入账户名称" clearable />
            </el-form-item>
            <el-form-item prop="password">
                <el-input v-model="loginForm.password" placeholder="请输入用户密码" type="password" show-password clearable />
            </el-form-item>
            <el-form-item prop="captcha">
                <el-row :gutter="10">
                    <el-col :span="16" :offset="0">
                        <el-input v-model="loginForm.captcha" placeholder="请输入验证码" />
                    </el-col>
                    <el-col :span="8" :offset="0">
                        <img class="images" :src="captchaImage" @click="refreshCaptcha" />
                    </el-col>
                </el-row>
            </el-form-item>
            <el-form-item>
                <el-row :gutter="20" class="formBtn">
                    <el-col :span="12" :offset="0">
                        <el-button type="primary" class="myBtn" @click="handleLogin">登 录</el-button>
                    </el-col>
                    <el-col :span="12" :offset="0">
                        <el-button type="danger" class="myBtn" @click="resetBtn">重 置</el-button>
                    </el-col>
                </el-row>
            </el-form-item>
        </el-form>
    </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { loginApi, getCaptchaApi } from '@/api/employeeApi.js'
import { useRouter } from 'vue-router';

//路由对象
const router = useRouter()

const loginFormRef = ref()  // 表单引用
const captchaKey = ref('')   // 验证码 key
const captchaImage = ref('') // 验证码图片 base64

// 登录表单数据
const loginForm = reactive({
    username: 'admin',
    password: '123456',
    captcha: '',
})

// 表单验证规则
const rules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度在3到20个字符', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度在6到20个字符', trigger: 'blur' }
    ],
    captcha: [
        { required: true, message: '请输入验证码', trigger: 'blur' },
        { length: 4, message: '验证码长度为4个字符', trigger: 'blur' }
    ]
}

/**
 * 刷新验证码
 * 调用后端接口获取新的验证码
 */
const refreshCaptcha = async () => {
    console.log('刷新验证码')
    try {
        const res = await getCaptchaApi()

        if (res.code === 200) {
            captchaKey.value = res.data.captchaKey
            captchaImage.value = res.data.captchaImage
            loginForm.captcha = ''
        }
    } catch (error) {
        console.error('获取验证码失败', error)
        ElMessage.error('获取验证码失败，请刷新页面重试')
    }
}

/**
 * 处理登录提交
 * 验证表单，调用登录接口
 */
const handleLogin = async () => {
    // 1. 验证表单
    if (!loginFormRef.value) return
    await loginFormRef.value.validate(async (valid) => {
        if (!valid) return
        try {
            // 2. 调用登录接口
            const res = await loginApi(loginForm)
            console.log("---------------------->>>", res)
            if (res.code === 200) {
                ElMessage.success('登录成功')
                sessionStorage.setItem('user', JSON.stringify(res.data))
                // 3. 跳转到dashboard页面
                router.push('/dashboard')
            } else {
                ElMessage.error(res.msg)
                // 登录失败，刷新验证码
                refreshCaptcha()
                // 清空验证码输入框
                loginForm.captcha = ''
            }
        } catch (error) {
            console.error('登录失败', error)
            // 4. 登录失败，刷新验证码
            refreshCaptcha()
            // 清空验证码输入框
            loginForm.captcha = ''
        }
    })
}

//重置
const resetBtn = () => {
    if (loginFormRef.value) {
        loginFormRef.value.resetFields()
    }
    loginForm.username = ''
    loginForm.password = ''
    loginForm.captcha = ''
    refreshCaptcha()
}

// 组件挂载时加载验证码
onMounted(() => {
    refreshCaptcha()
})
</script>

<style scoped>
.login {
    width: 100%;
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.loginForm {
    background: white;
    padding: 40px;
    border-radius: 12px;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
    width: 400px;
}

.loginTile {
    text-align: center;
    font-size: 24px;
    font-weight: bold;
    color: #333;
    margin-bottom: 30px;
}

.images {
    width: 100%;
    height: 40px;
    cursor: pointer;
    border-radius: 4px;
}

.formBtn {
    margin-top: 20px;
}

.myBtn {
    width: 100%;
}
</style>