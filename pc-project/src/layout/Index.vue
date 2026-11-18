<template>
    <el-container class="container">
        <el-aside width="auto" class="aside">
            <Menu></Menu>
        </el-aside>
        <el-container>
            <el-header class="header">
                <div class="header-left">
                    <Collapse></Collapse>
                    <BreadCrumb></BreadCrumb>
                </div>
                <LoginOut></LoginOut>
            </el-header>
            <el-main class="main">
                <router-view></router-view>
            </el-main>
        </el-container>
    </el-container>
</template>

<script setup lang="ts">
import Menu from './Menu.vue';
import Collapse from './Collapse.vue'; // 引入折叠按钮
import BreadCrumb from './BreadCrumb.vue' // 引入面包屑
import LoginOut from './LoginOut.vue' // 引入退出登录
</script>

<style lang="scss" scoped>
.container {
    height: 100%;

    .aside {
        background-color: #49cfcf;
    }

    .header {
        background-color: #34ce9b;
        display: flex;
        align-items: center;
        color: #FFF;
        justify-content: space-between;

        .header-left {
            display: flex;
            align-items: center;
        }
    }

    .main {
        background-color: #e7eaec;
    }
}
</style>