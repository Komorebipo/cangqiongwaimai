<template>
     <MenuLogo></MenuLogo>
    <el-menu :default-active="activeIndex" class="el-menu-vertical-demo" :collapse="isCollapse" unique-opened router
        @open="handleOpen" @close="handleClose" background-color="#58dec7" text-color="#fff" active-text-color="#95d5b2">
        <MenuItem :menuList="menuList"></MenuItem>
    </el-menu>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import MenuItem from './MenuItem.vue';
import MenuLogo from './MenuLogo.vue' //logo
import { useRoute } from 'vue-router'; //获取路由信息
import { collapseStore } from '@/store/collapse/index';
//引入 collapseStore
const collStore = collapseStore()
//获取路由信息
const route = useRoute();

//获取激活的菜单
const activeIndex = computed(() => {
    const { path } = route;
    return path;
})

//折叠
const isCollapse = computed(()=>{
  return collStore.getCollapse
})
//菜单数据
let menuList = reactive([
    {
        path: "/dashboard",
        component: "Layout",
        name: "dashboard",
        meta: {
            title: "系统首页",
            icon: "HomeFilled",
        },
    },
    {
        path: "/workbench",
        component: "Layout",
        name: "workbench",
        meta: {
            title: "工作平台",
            icon: "Operation",
        },
    },
     {
        path: "/statistics",
        component: "Layout",
        name: "statistics",
        meta: {
            title: "数据统计",
            icon: "Histogram",
        },
    },
    {
        path: "/order",
        component: "Layout",
        name: "order",
        meta: {
            title: "订单管理",
            icon: "DocumentCopy",
        },
    },
     {
        path: "/setmeal",
        component: "Layout",
        name: "setmeal",
        meta: {
            title: "套餐管理",
            icon: "Handbag",
        },
    },
    {
        path: "/dish",
        component: "Layout",
        name: "dish",
        meta: {
            title: "菜品管理",
            icon: "KnifeFork",
        },
    },
    {
        path: "/category",
        component: "Layout",
        name: "category",
        meta: {
            title: "分类管理",
            icon: "Menu",
        },
    },
    {
        path: "/employee",
        component: "Layout",
        name: "employee",
        meta: {
            title: "员工管理",
            icon: "User",
        },
    },
    {
        path: "/smartCustomerService",
        component: "Layout",
        name: "smartCustomerService",
        meta: {
            title: "智能客服",
            icon: "Message",
        },
    }
]);

//展开
const handleOpen = (key: string, keyPath: string[]) => {
    console.log(key, keyPath)
}
//关闭
const handleClose = (key: string, keyPath: string[]) => {
    console.log(key, keyPath)
}
</script>

<style lang="scss" scoped>
.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 230px;
  min-height: 400px;
}

.el-menu {
  border-right: none;
}

:deep(.el-sub-menu .el-sub-menu__title) {
  color: #f4f4f5 !important;
}

:deep(.el-menu .el-menu-item) {
  color: #bfcbd9;
}

/* 菜单点中文字的颜色 */
:deep(.el-menu-item.is-active) {
  color: #12569f !important;
}

/* 当前打开菜单的所有子菜单颜色 */
:deep(.is-opened .el-menu-item) {
  background-color: #4882ab !important;
}

/* 鼠标移动菜单的颜色 */
:deep(.el-menu-item:hover) {
  background-color: #3b5e94 !important;
}
</style>