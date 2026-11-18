import { defineStore } from "pinia";
//创建store
//defineStore第一个参数：唯一的，不能重复 
export const testStore = defineStore('testStore',{
    state:()=>{ //状态     定义name属性 count
        return{
            count:10
        }
    },
    getters:{//获取值
        getCount(state){    //获取state的值
            return state.count
        }
    }, 
    actions:{ //改变state的值
        setCount(count){
            this.count = count;
        }
    }
})




/*
    public class Student {
        private String name;

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }


*/