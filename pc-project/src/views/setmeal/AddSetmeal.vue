<template>
    <el-card shadow="always">
        <el-form ref="itemFormRef" :model="ruleForm" :rules="rules" :inline="false" label-width="180px" class="demo-ruleForm">
            <el-form-item label="套餐名称:" prop="name">
                <el-input v-model="ruleForm.name" placeholder="请填写套餐名称" maxlength="20" style="width: 20%" />
            </el-form-item>
            <el-form-item label="套餐分类:" prop="categoryId">
                <el-select v-model="ruleForm.categoryId" placeholder="请选择套餐分类" style="width: 20%">
                    <el-option v-for="(item, index) in setmealCategoryList" :key="index" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
            <el-form-item label="套餐价格:" prop="price">
                <el-input v-model="ruleForm.price" placeholder="请设置套餐价格" style="width: 20%" />
            </el-form-item>
            <el-form-item label="套餐配置:" prop="setmealDishes">
                <el-button type="primary" v-if="dishTable.length == 0" icon="Plus" @click="openAddDish('new')">
                    添加菜品
                </el-button>
                <div v-if="dishTable.length != 0" class="content">
                    <el-button type="primary" style="margin-bottom: 20px" icon="Plus" @click="openAddDish('change')">
                        添加菜品
                    </el-button>
                    <el-table :data="dishTable" style="width: 100%">
                        <el-table-column prop="name" label="菜品名称" width="180" align="center" />
                        <el-table-column prop="price" label="原价" width="180" align="center">
                            <template #default="{ row }">
                                {{ (Number(row.price).toFixed(2) * 100) / 100 }}
                            </template>
                        </el-table-column>
                        <el-table-column prop="address" label="份数" align="center">
                            <template #default="{ row }">
                                <el-input-number v-model="row.copies" size="small" :min="1" :max="99" label="描述文字" />
                            </template>
                        </el-table-column>
                        <el-table-column prop="address" label="操作" width="180px;" align="center">
                            <template #default="{ $index }">
                                <el-button type="danger" link size="small" class="delBut non" @click="delDishHandle($index)">
                                    删除
                                </el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </div>
            </el-form-item>
            <el-form-item label="套餐图片：" prop="image">
                <el-upload action="http://localhost:10086/admin/common/upload" name="file" list-type="picture-card" :auto-upload="false" :on-change="uploadFile" :limit="1" ref="uploadRef" :file-list="fileList">
                    <el-icon>
                        <Plus />
                    </el-icon>
                </el-upload>
                <span style="margin-left: 20px;"> 图片大小不超过3M<br />仅能上传 PNG JPEG JPG GIF 类型图片</span>
            </el-form-item>
            <el-form-item label="套餐描述:" prop="region">
                <el-input v-model="ruleForm.description" type="textarea" :rows="5" maxlength="200" placeholder="套餐描述，最长200字" style="width: 50%" />
            </el-form-item>
            <el-form-item>
                <el-button @click="() => router.back()">
                    取消
                </el-button>
                <el-button type="primary" @click="submitForm">
                    保存
                </el-button>
            </el-form-item>
        </el-form>
    </el-card>

    <el-dialog v-model="dialogVisible" title="添加菜品" class="addDishList" width="60%" :before-close="handleClose">
        <el-input v-model="seachKey" class="seachDish" placeholder="请输入菜品名称进行搜索" style="width: 355px; height: 40px" size="small" clearable @input="handleSearch">
            <template #prefix>
                <el-icon>
                    <Search />
                </el-icon>
            </template>
        </el-input>

        <div class="dish-list-container">
            <div class="dish-category" v-for="category in filteredDishList" :key="category.categoryId">
                <div class="category-title">{{ category.categoryName }}</div>
                <div class="dish-items">
                    <el-checkbox-group v-model="selectedDishes">
                        <div class="dish-item" v-for="dish in category.dishes" :key="dish.id">
                            <el-checkbox :label="dish.id">
                                <div class="dish-info">
                                    <span class="dish-name">{{ dish.name }}</span>
                                    <span class="dish-price">￥{{ dish.price }}</span>
                                </div>
                            </el-checkbox>
                        </div>
                    </el-checkbox-group>
                </div>
            </div>
        </div>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="handleClose">取 消</el-button>
                <el-button type="primary" @click="addTableList">添 加</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus';
import { Plus, Search } from '@element-plus/icons-vue'
import { useRouter, useRoute } from 'vue-router'
import { upload } from "@/api/setmealApi.js";
import { getCategoryList, querySetmealById, addSetmeal, editSetmeal } from "@/api/setmealApi.js";
import { queryDishList } from "@/api/dishApi.js";

const ruleForm = reactive({
    id: '',
    name: '',
    categoryId: '',
    price: '',
    image: '',
    description: '',
    setmealDishes: [],
    status: 0,
})

const rules = {
    name: [
        { required: true, message: '请输入套餐名称', trigger: 'blur' },
        { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
    ],
    categoryId: [
        { required: true, message: '请选择套餐分类', trigger: 'change' }
    ],
    price: [
        { required: true, message: '请输入套餐价格', trigger: 'blur' },
        {
            pattern: /^([1-9]\d{0,5}|0)(\.\d{1,2})?$/,
            message: '套餐价格格式有误，请输入大于零且最多保留两位小数的金额',
            trigger: 'blur'
        }
    ],
    image: [
        { required: true, message: '请上传套餐图片', trigger: 'change' }
    ]
}

const dishTable = ref([])
const dialogVisible = ref(false)
const seachKey = ref('')
const allDishList = ref([])
const selectedDishes = ref([])

const getDishListData = async () => {
    try {
        const res = await queryDishList({})
        if (res.code === 200) {
            const groupedDishes = {}
            res.data.forEach(dish => {
                if (!groupedDishes[dish.categoryId]) {
                    groupedDishes[dish.categoryId] = {
                        categoryId: dish.categoryId,
                        categoryName: dish.categoryName || '未分类',
                        dishes: []
                    }
                }
                groupedDishes[dish.categoryId].dishes.push({
                    id: dish.id,
                    name: dish.name,
                    price: dish.price
                })
            })
            allDishList.value = Object.values(groupedDishes)
        }
    } catch (error) {
        ElMessage.error('获取菜品列表失败')
    }
}

const filteredDishList = computed(() => {
    if (!seachKey.value) {
        return allDishList.value
    }
    return allDishList.value.map(category => ({
        ...category,
        dishes: category.dishes.filter(dish =>
            dish.name.toLowerCase().includes(seachKey.value.toLowerCase())
        )
    })).filter(category => category.dishes.length > 0)
})

const openAddDish = (data) => {
    seachKey.value = ''
    selectedDishes.value = []
    dialogVisible.value = true
}

const handleClose = () => {
    dialogVisible.value = false
    seachKey.value = ''
    selectedDishes.value = []
}

const addTableList = () => {
    if (selectedDishes.value.length === 0) {
        ElMessage.warning('请选择要添加的菜品')
        return
    }

    const newDishes = []
    selectedDishes.value.forEach(dishId => {
        let dishInfo = null
        for (const category of allDishList.value) {
            const found = category.dishes.find(dish => dish.id === dishId)
            if (found) {
                dishInfo = found
                break
            }
        }

        if (dishInfo) {
            const exists = dishTable.value.some(item => item.id === dishId)
            if (!exists) {
                newDishes.push({
                    id: dishInfo.id,
                    name: dishInfo.name,
                    price: dishInfo.price,
                    copies: 1
                })
            }
        }
    })

    if (newDishes.length > 0) {
        dishTable.value = [...dishTable.value, ...newDishes]
        ElMessage.success(`成功添加 ${newDishes.length} 个菜品`)
    } else {
        ElMessage.warning('所选菜品已存在')
    }

    handleClose()
}

const delDishHandle = (index) => {
    dishTable.value.splice(index, 1)
    ElMessage.success('删除成功')
}

const actionType = ref('add')
const itemFormRef = ref(null)

const submitForm = async () => {
    if (!itemFormRef.value) return
    await itemFormRef.value.validate(async (valid) => {
        if (!valid) return

        if (dishTable.value.length === 0) {
            ElMessage.error('请至少添加一个菜品')
            return
        }

        const submitData = {
            name: ruleForm.name,
            categoryId: ruleForm.categoryId,
            price: ruleForm.price,
            image: ruleForm.image,
            description: ruleForm.description,
            status: actionType.value === 'add' ? 0 : ruleForm.status,
            setmealDishes: dishTable.value.map(item => ({
                dishId: item.id,
                name: item.name,
                price: item.price,
                copies: item.copies
            }))
        }

        if (actionType.value === 'edit') {
            submitData.id = ruleForm.id
        }

        try {
            const res = actionType.value === 'add'
                ? await addSetmeal(submitData)
                : await editSetmeal(submitData)

            if (res.code === 200) {
                ElMessage.success(res.msg)
                const page = route.query.page || 1
                router.push({ path: '/setmeal', query: { page } })
            } else {
                ElMessage.error(res.msg)
            }
        } catch (error) {
            ElMessage.error('操作失败')
        }
    })
}

const router = useRouter()
const route = useRoute()
const setmealCategoryList = ref([])

const getSetmealCategoryList = async () => {
    try {
        const res = await getCategoryList({ type: 2 })
        if (res.code === 200) {
            setmealCategoryList.value = res.data.map(item => ({
                label: item.name,
                value: item.id
            }))
        }
    } catch (error) {
        ElMessage.error('获取套餐分类失败')
    }
}

const getSetmeal = async (id) => {
    try {
        const res = await querySetmealById(id)
        if (res.code === 200) {
            ruleForm.id = res.data.id
            ruleForm.name = res.data.name
            ruleForm.categoryId = res.data.categoryId
            ruleForm.price = res.data.price
            ruleForm.status = res.data.status
            ruleForm.description = res.data.description
            ruleForm.image = res.data.image

            fileList.value = [{ name: '菜品图片', url: ruleForm.image }]

            ruleForm.setmealDishes = res.data.setmealDishes.map(item => ({
                dishId: item.dishId,
                copies: item.copies
            }))

            dishTable.value = res.data.setmealDishes.map(item => ({
                id: item.dishId,
                name: item.name,
                price: item.price,
                copies: item.copies
            }))
        }
    } catch (error) {
        ElMessage.error("获取套餐详情失败!!!")
    }
}

const uploadRef = ref();
const fileList = ref([]);

const uploadFile = async (file) => {
    const typeArr = ["image/png", "image/gif", "image/jpeg", "image/jpg"];
    const isImg = typeArr.indexOf(file.raw.type) !== -1;
    const isMore3M = file.size / 1024 / 1024 < 3;

    if (!isImg) {
        ElMessage.warning("只能上传图片类型!");
        uploadRef.value.clearFiles();
        return;
    }
    if (!isMore3M) {
        ElMessage.warning("图片大小不能超过3M!");
        uploadRef.value.clearFiles();
        return;
    }

    const formData = new FormData();
    formData.append("file", file.raw);
    const res = await upload(formData)

    if (res.code === 200) {
        ElMessage.success(res.msg)
        ruleForm.image = "http://localhost:10086" + res.data;
        fileList.value = [{ name: '套餐图片', url: ruleForm.image }];
    }
}

const dishId = ref(null);

onMounted(() => {
    getDishListData()
    getSetmealCategoryList()
    dishId.value = route.query.id
    if (route.query.id) {
        actionType.value = 'edit'
        getSetmeal(route.query.id)
    } else {
        actionType.value = 'add'
    }
})
</script>

<style lang="scss" scoped>
.dish-list-container {
    .dish-category {
        margin-bottom: 28px;

        &:last-child {
            margin-bottom: 0;
        }

        .category-title {
            font-size: 16px;
            font-weight: 600;
            color: #2c3e50;
            padding-bottom: 12px;
            margin-bottom: 16px;
            border-bottom: 2px solid #e9ecef;
            position: relative;

            &::before {
                content: '';
                position: absolute;
                bottom: -2px;
                left: 0;
                width: 40px;
                height: 2px;
                background: linear-gradient(90deg, #667eea, #764ba2);
            }
        }

        .dish-items {
            :deep(.el-checkbox-group) {
                display: grid;
                grid-template-columns: repeat(3, 1fr);
                gap: 12px;
            }

            .dish-item {
                background: white;
                border-radius: 10px;
                border: 1px solid #e9ecef;
                transition: all 0.3s ease;
                overflow: hidden;

                &:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
                    border-color: #d4d8dd;
                }

                :deep(.el-checkbox) {
                    width: 100%;
                    margin: 0;
                    padding: 12px 16px;

                    .el-checkbox__input {
                        .el-checkbox__inner {
                            width: 18px;
                            height: 18px;
                            border-radius: 4px;
                            border-color: #dee2e6;
                            transition: all 0.2s;

                            &:hover {
                                border-color: #667eea;
                            }
                        }

                        &.is-checked {
                            .el-checkbox__inner {
                                background-color: #667eea;
                                border-color: #667eea;

                                &::after {
                                    border-color: white;
                                }
                            }
                        }
                    }

                    .el-checkbox__label {
                        padding-left: 12px;
                        width: 100%;
                    }
                }

                .dish-info {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    width: 100%;

                    .dish-name {
                        font-size: 14px;
                        font-weight: 500;
                        color: #2c3e50;
                        flex: 1;
                        overflow: hidden;
                        text-overflow: ellipsis;
                        white-space: nowrap;
                    }

                    .dish-price {
                        font-size: 14px;
                        font-weight: 600;
                        color: #e74c3c;
                        margin-left: 12px;

                        &::before {
                            content: '¥';
                            font-size: 12px;
                            margin-right: 2px;
                        }
                    }
                }
            }
        }
    }
}
</style>