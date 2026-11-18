<template>
    <div class="logo">
        <img :src="MenuLogo1" v-if="show" class="logo-png1" alt="logo" />
        <img :src="MenuLogo2" v-if="!show" class="logo-png2" alt="logo" />
        <span class="logo-title" v-if="!store.getCollapse">{{ title }}</span>
    </div>
</template>
<script setup lang="ts">
import MenuLogo1 from '@/assets/logo.jpg'
import MenuLogo2 from '@/assets/logo.jpg'
import { ref, watch } from "vue";
import { collapseStore } from '@/store/collapse/index';
// 引入 collapseStore
const store = collapseStore()

const title = ref("智捷外卖");
const show = ref(true)// 显示logo
// 监听折叠状态
watch(
    () => store.getCollapse,
    (collapsed) => {
        if (!collapsed) {
            setTimeout(() => {
                show.value = !collapsed;//延迟执行
            }, 300);
        } else {
            show.value = !collapsed;
        }
    }
)
</script>
<style lang="scss" scoped>
.logo {
    display: flex;
    width: 100%;
    height: 60px;
    line-height: 60px;
    background-color: #37b396;
    text-align: center;
    cursor: pointer;
    align-items: center;
    margin-left: 10px;

    .logo-png1 {
        // width: 47px;
        // height: 47px;
        width: 47px;
        height: 50px;
        margin-right: 10px;
        margin-left: 0px;
    }

    .logo-png2 {
        width: 47px;
        height: 47px;
        margin-right: 10px;
        margin-left: 0px;
    }

    .logo-title {
        color: #fff;
        font-weight: bold;
        line-height: 60px;
        font-size: 20px;
        /* font-family: "楷体"; */
        letter-spacing: 3px;
    }
}
</style>