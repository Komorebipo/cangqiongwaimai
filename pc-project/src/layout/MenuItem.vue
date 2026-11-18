<template>
    <template v-for="menu in menuList" :key="menu.path">
        <el-sub-menu v-if="menu.children && menu.children.length > 0" :index="menu.path">
            <template #title>
                <el-icon>
                    <!-- 动态组件 -->
                    <component :is="getIconComponent(menu.meta.icon)"></component>
                </el-icon>
                <span>{{ menu.meta.title }}</span>
            </template>
            <!-- 递归调用生成下级菜单  递归：自己调用自己-->
            <menu-item :menuList="menu.children"></menu-item>
        </el-sub-menu>
        <el-menu-item v-else :index="menu.path">
            <el-icon>
                <!-- 动态组件 -->
                <component :is="getIconComponent(menu.meta.icon)"></component>
            </el-icon>
            <template #title>
                {{ menu.meta.title }}
            </template>
        </el-menu-item>
    </template>
</template>

<script setup lang="ts">
//接收父组件传递过来的参数
defineProps(['menuList'])

// 导入Element Plus图标
import { 
  HomeFilled, 
  Operation, 
  Histogram, 
  DocumentCopy, 
  Handbag, 
  KnifeFork, 
  Menu, 
  User,
  Plus,
  Message
} from '@element-plus/icons-vue'

// 图标映射表
const iconMap: Record<string, any> = {
  HomeFilled,
  Operation,
  Histogram,
  DocumentCopy,
  Handbag,
  KnifeFork,
  Menu,
  User,
  Plus,
  Message
}

// 获取图标组件
const getIconComponent = (iconName: string) => {
  return iconMap[iconName] || User
}
</script>

<style scoped>
.el-menu-item{
  color: white;
}
</style>