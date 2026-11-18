<template>
	<view class="u-p-t-40 u-p-r-60 u-p-b-30 u-p-l-60 login">
		<!-- 头像 -->
		<u-avatar size="140" src="/static/login.jpg" mode="circle"></u-avatar>

		<!-- 登录表单 -->
		<u-form class="forms" :model="formData" ref="formRef">
			<!-- 账号输入 -->
			<u-form-item left-icon="account-fill" left-icon-style="font-size:24px;color:#4ec3e6;">
				<u-input v-model="formData.name" placeholder="请输入账号" />
			</u-form-item>

			<!-- 密码输入 -->
			<u-form-item left-icon="lock" left-icon-style="font-size:24px;color:#4ec3e6;">
				<u-input v-model="formData.password" placeholder="请输入密码" :password="!showPassword" />
			</u-form-item>
		</u-form>

		<!-- 账号密码登录按钮 -->
		<u-button class="loginStyle" @click="handleAccountLogin" :loading="isSubmitting">
			账号登录
		</u-button>

		<!-- 注册按钮 -->
		<u-button type="success" class="registerStyle" @click="navigateToRegister">
			注册
		</u-button>

	</view>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { loginApi } from '../../api/userApi.js';

const formData = reactive({
	name: '',
	password: ''
});

const formRef = ref(null);
const showPassword = ref(false);
const isSubmitting = ref(false);

const handleAccountLogin = async () => {
	console.log('账号登录被点击');

	if (!formData.name.trim()) {
		uni.showToast({ title: '请输入账号', icon: 'none' });
		return;
	}
	if (!formData.password.trim()) {
		uni.showToast({ title: '请输入密码', icon: 'none' });
		return;
	}
	try {
		const res = await loginApi({
			name: formData.name,
			password: formData.password
		});
		console.log('账号登录响应:', res);

		if (res.code === 200) {
			handleLoginSuccess(res.data);
		}
	} catch (error) {
		console.error('登录异常:', error);
	}
};

const handleLoginSuccess = (userData) => {
	console.log('登录成功，用户数据:', JSON.stringify(userData));
	
	if (userData.avatar && typeof userData.avatar === 'string' && !userData.avatar.startsWith('http')) {
		userData.avatar = 'http://localhost:10086' + userData.avatar;
	}
	
	uni.setStorageSync('user', userData);

	uni.showToast({ title: '登录成功', icon: 'success' });

	setTimeout(() => {
		uni.switchTab({ url: '/pages/index/index' });
	}, 1500);
};

const navigateToRegister = () => {
	uni.navigateTo({ url: '/pages/register/register' });
};
</script>

<style lang="scss" scoped>
.login {
	height: 100%;
	display: flex;
	align-items: center;
	flex-direction: column;
	background: linear-gradient(180deg, #4ec3e6 0%, #ffffff 20%);

	.forms {
		width: 100%;
	}

	.loginStyle {
		margin-top: 40px;
		background: #4ec3e6;
		color: #FFF;
		width: 100%;
	}

	.registerStyle {
		margin-top: 30px;
		background: #3da06d;
		color: #FFF;
		width: 100%;
	}
}
</style>
