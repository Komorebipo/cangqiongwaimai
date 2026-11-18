<template>
    <view class="u-m-40 title">修改用户密码</view>
    <view class="u-wrap u-p-l-20 u-p-r-20 u-m-t-30">
        <u-form label-width="auto" :model="addModel" ref="form1">
            <u-form-item label="原密码:" prop="oldPassword">
                <u-input v-model="addModel.oldPassword" />
            </u-form-item>
            <u-form-item label="新密码:" prop="password">
                <u-input v-model="addModel.password" />
            </u-form-item>
            <u-form-item label="确定密码:" prop="confirm">
                <u-input v-model="addModel.confirm" />
            </u-form-item>
        </u-form>
        <u-button @click='commit' class="customStyle">确定修改</u-button>
    </view>
</template>

<script setup>
import { onReady } from '@dcloudio/uni-app';
import { ref, reactive } from 'vue';
import { updatePasswordApi } from '../../api/userApi.js'

const form1 = ref('')
const addModel = reactive({
    userId: uni.getStorageSync("user").id,
    password: '',
    oldPassword: '',
    confirm: ''
})

const commit = async () => {
    form1.value.validate(async (valid) => {
        if (addModel.confirm != addModel.password) {
            uni.showToast({
                title: '新密码和确定密码不一致!',
                icon: 'none',
                mask: true,
                duration: 3000
            })
            return;
        }
        if (valid) {
            console.log('验证通过')
            let res = await updatePasswordApi(addModel)
            console.log("--->" + res)
            if (res.code == 200) {
                uni.clearStorageSync();
                uni.reLaunch({
                    url: '../login/login'
                });
            }
        }
    })
}

onReady(() => {
    form1.value.setRules(rules);
})

const rules = reactive({
    oldPassword: [{ required: true, message: '请输入原密码', trigger: ['change'] }],
    password: [{ required: true, message: '请输入新密码', trigger: ['change'] }],
    confirm: [{ required: true, message: '请输入确定密码', trigger: ['change'] }]
})
</script>

<style>
.title {
    color: #6f8fb1;
    font-size: 30px;
    text-align: center;
    letter-spacing: 4px;
}

.customStyle {
    background: #2ad8ff;
    color: #FFF;
    margin-top: 50px;
    width: 100%;
}
</style>
