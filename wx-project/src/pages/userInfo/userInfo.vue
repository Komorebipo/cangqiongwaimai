<template>
    <view class="u-m-40 title">用户个人中心</view>

    <view class="u-wrap u-p-l-20 u-p-r-20">
        <u-form label-width="auto" :model="formData" ref="formRef">
            <u-form-item label="姓名:" prop="name">
                <u-input v-model="formData.name" placeholder="请输入姓名" />
            </u-form-item>
            <u-form-item label="电话:" prop="phone">
                <u-input v-model="formData.phone" placeholder="请输入电话" />
            </u-form-item>
            <u-form-item label="证件:" prop="idNumber">
                <u-input v-model="formData.idNumber" placeholder="请输入证件号" />
            </u-form-item>
            <u-form-item prop="avatar" label="头像:">
                <u-upload max-count="1" ref="uploadRef" :file-list="fileList" @on-remove="handleRemove"
                    @on-change="handleChange" :action="uploadUrl"></u-upload>
            </u-form-item>
        </u-form>
        <u-button @click="handleSubmit" :custom-style="submitStyle">
            确定修改
        </u-button>
    </view>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import http from '../../common/http.js';
import { editInfoApi, getUserInfoApi } from '../../api/userApi.js';

const baseUrl = 'http://localhost:10086';

const formData = reactive({
    id: '',
    name: '',
    phone: '',
    idNumber: '',
    avatar: ''
});

const formRef = ref(null);
const uploadRef = ref(null);
const fileList = ref([]);

const uploadUrl = 'http://localhost:10086/admin/common/upload';

const submitStyle = reactive({
    background: '#4ec3e6',
    color: '#FFF',
    marginTop: '50px',
    width: '100%'
});

const handleChange = (res, index, lists, name) => {
    console.log('图片上传响应:', res);
    const result = JSON.parse(res.data);
    formData.avatar = result.data;
    console.log('当前头像路径:', formData.avatar);
};

const handleRemove = (index) => {
    console.log('删除图片，索引:', index);
    formData.avatar = '';
};

const handleSubmit = async () => {
    if (!formData.name.trim()) {
        uni.showToast({ title: '请输入姓名', icon: 'none' });
        return;
    }
    if (!formData.phone.trim()) {
        uni.showToast({ title: '请输入电话', icon: 'none' });
        return;
    }
    if (!formData.idNumber.trim()) {
        uni.showToast({ title: '请输入证件号', icon: 'none' });
        return;
    }

    if (!formData.id) {
        uni.showToast({ title: '用户ID缺失，请重新登录', icon: 'none' });
        return;
    }

    try {
        const submitData = JSON.parse(JSON.stringify(formData));
        console.log('提交数据:', JSON.stringify(submitData));
        const res = await editInfoApi(submitData);
        console.log('修改接口返回:', JSON.stringify(res));

        if (res.code === 200) {
            uni.showToast({ title: '修改成功', icon: 'success' });

            const user = uni.getStorageSync('user') || {};
            const newUserInfo = {
                ...user,
                name: formData.name,
                phone: formData.phone,
                idNumber: formData.idNumber,
                avatar: formData.avatar ? ("http://localhost:10086" + formData.avatar) : user.avatar
            };
            console.log('更新本地存储:', newUserInfo);
            uni.setStorageSync('user', newUserInfo);

            setTimeout(() => {
                uni.navigateBack({ delta: 1 });
            }, 1500);
        } else {
            uni.showToast({ title: res.msg || '修改失败', icon: 'none' });
        }
    } catch (error) {
        console.error('修改失败:', error);
        uni.showToast({ title: '修改失败，请检查网络', icon: 'none' });
    }
};

const getUserInfoData = async () => {
    const user = uni.getStorageSync('user');
    const userId = user ? user.id : null;
    if (!userId) {
        console.warn('用户未登录，从本地存储回显');
        if (user) {
            formData.id = user.id || '';
            formData.name = user.name || '';
            formData.phone = user.phone || '';
            formData.idNumber = user.idNumber || '';
            formData.avatar = user.avatar || '';
            if (user.avatar) {
                fileList.value = [{ url: typeof user.avatar === 'string' && user.avatar.startsWith('http') ? user.avatar : baseUrl + user.avatar }];
            }
        }
        return;
    }
    try {
        console.log('从后端获取用户信息, userId:', userId);
        const res = await getUserInfoApi({ userId: userId });
        console.log('获取用户信息成功:', JSON.stringify(res));
        if (res.code === 200 && res.data) {
            formData.id = res.data.id || user.id || '';
            formData.name = res.data.name || '';
            formData.phone = res.data.phone || '';
            formData.idNumber = res.data.idNumber || '';
            formData.avatar = res.data.avatar || '';

            const newUser = { ...user, ...res.data };
            uni.setStorageSync('user', newUser);

            if (formData.avatar) {
                fileList.value = [{ url: baseUrl + formData.avatar }];
            }
        } else {
            formData.id = user.id || '';
            formData.name = user.name || '';
            formData.phone = user.phone || '';
            formData.idNumber = user.idNumber || '';
            formData.avatar = user.avatar || '';
            if (user.avatar) {
                fileList.value = [{ url: typeof user.avatar === 'string' && user.avatar.startsWith('http') ? user.avatar : baseUrl + user.avatar }];
            }
        }
    } catch (error) {
        console.error('获取用户信息失败:', error);
        if (user) {
            formData.id = user.id || '';
            formData.name = user.name || '';
            formData.phone = user.phone || '';
            formData.idNumber = user.idNumber || '';
            formData.avatar = user.avatar || '';
            if (user.avatar) {
                fileList.value = [{ url: typeof user.avatar === 'string' && user.avatar.startsWith('http') ? user.avatar : baseUrl + user.avatar }];
            }
        }
    }
};

onShow(() => {
    getUserInfoData();
});
</script>

<style lang="scss" scoped>
.title {
    color: #6f8fb1;
    font-size: 30px;
    text-align: center;
    letter-spacing: 4px;
    margin: 40rpx 0 30rpx 0;
}
</style>
