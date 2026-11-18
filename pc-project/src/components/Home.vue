<template>
    <div>数据：{{ count }}</div>
    <button @click="addBut">加+1</button>
</template>
<script setup>
import { ref, computed } from 'vue';
import { testStore } from '@/store/test/index' //   引入store

//const count = ref(0)
//获取store
const store = testStore();

console.log("---->",store.count)



//从store里面获取count
const count = computed(() => {
    return store.getCount //获取值
})
//改变store里面的值
const addBut = () => {
    //store.count++
    store.setCount(++store.count)
}
</script>

<style scoped></style>