<template>
    <div>
        <h1>Hello World~~~~~~~~~~~~~~~~~~··</h1>
    </div>
    <div class="button-row">
        <el-button>Default</el-button>
        <el-button type="primary">Primary</el-button>
        <el-button type="success">Success</el-button>
        <el-button type="info">Info</el-button>
        <el-button type="warning">Warning</el-button>
        <el-button type="danger">Danger</el-button>
    </div><br/>
    <el-date-picker type="date" placeholder="Pick a day日期" :size="size" /><br/>
    <div style="font-size: 20px">
        <!-- 由于SVG图标默认不携带任何属性 -->
        <!-- 你需要直接提供它们 -->
        <Edit style="width: 1em; height: 1em; margin-right: 8px" />
        <Share style="width: 1em; height: 1em; margin-right: 8px" />
        <Delete style="width: 1em; height: 1em; margin-right: 8px" />
        <Search style="width: 1em; height: 1em; margin-right: 8px" />
    </div>
    <h1> {{title  }}   </h1>
</template>

<script setup>
import { ref } from 'vue'
    const title = ref('陇东学院')
</script>

<style lang="scss" scoped></style>