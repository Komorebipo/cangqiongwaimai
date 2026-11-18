import { createRouter, createWebHistory } from "vue-router";
// import Layout from '@/components/HelloWorld.vue'
import Layout from "@/layout/Index.vue";
// 路由配置

const routes = [
  {
    path: "/login",
    component: () => import("@/views/Login.vue"),
    meta: { title: "登录" },
  },
  {
    path: "/",
    component: Layout,
    redirect: "/dashboard",
    children: [
      {
        path: "/dashboard",
        component: () => import("@/views/dashboard/Index.vue"),
        name: "dashboard",
        meta: {
          title: "系统首页",
          icon: "HomeFilled",
        },
      },
      {
        path: "/workbench",
        component: () => import("@/views/workbench/Index.vue"),
        meta: {
          title: "工作平台",
          icon: "Operation",
        },
      },
      {
        path: "/statistics",
        component: () => import("@/views/statistics/Index.vue"),
        meta: {
          title: "数据统计",
          icon: "Histogram",
        },
      },
      {
        path: "/order",
        component: () => import("@/views/order/Index.vue"),
        meta: {
          title: "订单管理",
          icon: "DocumentCopy",
        },
      },
      {
        path: "/setmeal",
        component: () => import("@/views/setmeal/Index.vue"),
        meta: {
          title: "套餐管理",
          icon: "Handbag",
        },
      },
      {
        path: "/dish",
        component: () => import("@/views/dish/Index.vue"),
        meta: {
          title: "菜品管理",
          icon: "KnifeFork",
        },
      },
      {
        path: "/category",
        component: () => import("@/views/category/Index.vue"),
        meta: {
          title: "分类管理",
          icon: "Menu",
        },
      },
      {
        path: "/employee",
        component: () => import("@/views/employee/Index.vue"),
        meta: {
          title: "员工管理",
          icon: "User",
        },
      },
      {
        path: "/smartCustomerService",
        component: () => import("@/views/smartCustomerService/Index.vue"),
        meta: {
          title: "智能客服",
          icon: "Message",
        },
      },
      // 在路由数组中添加
{
        path: "/dish/add",
        component: () => import("@/views/dish/AddDish.vue"),
        meta: { title: "新增菜品", icon: "Plus" },
      },
      {
        path: "/setmeal/addorupdate",
        component: () => import("@/views/setmeal/AddSetmeal.vue"),
        meta: { title: "新增套餐&修改套餐", icon: "Handbag" },
      }
    ],
  },
];
// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 路由守卫
router.beforeEach((to, from, next) => {
  // 获取用户登录状态
  const user = sessionStorage.getItem('user');
  
  // 如果访问的是登录页面，直接放行
  if (to.path === '/login') {
    next();
    return;
  }
  
  // 如果用户未登录，跳转到登录页面
  if (!user) {
    next('/login');
    return;
  }
  
  // 用户已登录，放行
  next();
});

// 导出路由实例
export default router;