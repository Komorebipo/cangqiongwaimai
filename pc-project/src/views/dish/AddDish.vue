<template>
  <el-card shadow="always">
    <el-form
      ref="itemFormRef"
      :model="ruleForm"
      :rules="rules"
      :inline="false"
      label-width="180px"
      class="demo-ruleForm"
    >
      <el-form-item label="菜品名称：" prop="name">
        <el-input
          v-model="ruleForm.name"
          placeholder="请填写菜品名称"
          maxlength="20"
          style="width: 20%"
        />
      </el-form-item>

      <el-form-item label="菜品分类：" prop="categoryId">
        <el-select
          v-model="ruleForm.categoryId"
          placeholder="请选择菜品分类"
          style="width: 20%"
        >
          <el-option
            v-for="(item, index) in dishList"
            :key="index"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="菜品价格：" prop="price">
        <el-input
          v-model="ruleForm.price"
          placeholder="请设置菜品价格"
          style="width: 20%"
        />
      </el-form-item>

      <el-form-item label="口味做法配置">
        <el-button
          type="primary"
          v-if="canAddFlavor"
          icon="Plus"
          @click="addFlavor"
        >
          添加口味
        </el-button>

        <div v-if="dishFlavors.length > 0" class="flavor">
          <div class="title">
            <span>口味名（3个字内）</span>
            <span style="margin-left: 20px;">口味标签</span>
            <span style="margin-left: 100px;">操作</span>
          </div>
          <div class="cont">
            <div
              v-for="(item, index) in dishFlavors"
              :key="item.id || index"
              class="items"
            >
              <div class="itTit">
                <el-select
                  v-model="item.name"
                  placeholder="请选择口味名称"
                  style="width: 120px;"
                >
                  <el-option
                    v-for="flavor in availableFlavors"
                    :key="flavor.name"
                    :label="flavor.name"
                    :value="flavor.name"
                  />
                </el-select>
              </div>
              <div
                class="lbltems"
                style="display: flex; flex-wrap: wrap; gap: 8px; margin: 0 20px;"
              >
                <el-check-tag
                  v-for="(tag, ind) in getFlavorTags(item.name)"
                  :key="ind"
                  :checked="item.value.includes(tag)"
                  @click="toggleTag(index, tag)"
                  style="margin-right: 8px;"
                >
                  {{ tag }}
                </el-check-tag>
              </div>
              <el-button type="danger" size="small" @click="delFlavor(index)">
                删除
              </el-button>
            </div>
          </div>
        </div>
      </el-form-item>

      <el-form-item label="菜品图片：" prop="image">
        <el-upload
          action="#"
          list-type="picture-card"
          :auto-upload="false"
          :on-change="uploadFile"
          :limit="1"
          ref="uploadRef"
          :file-list="fileList"
        >
          <el-icon><Plus /></el-icon>
        </el-upload>
        <span style="margin-left: 20px;">
          图片大小不超过3M<br />
          仅能上传 PNG JPEG JPG GIF 类型图片<br />
          建议上传200*200或300*300尺寸的图片
        </span>
      </el-form-item>

      <el-form-item label="菜品描述：" prop="region">
        <el-input
          v-model="ruleForm.description"
          type="textarea"
          :rows="5"
          maxlength="200"
          placeholder="菜品描述，最长200字"
          style="width: 50%"
        />
      </el-form-item>

      <el-form-item>
        <el-button @click="() => router.back()">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { useRouter, useRoute } from "vue-router";
import { Plus } from "@element-plus/icons-vue";
import { upload } from "@/api/dishApi.js";
import {
  getCategoryList,
  queryDishById,
  addDish,
  editDish,
} from "@/api/dishApi.js";

const dishList = ref([]); // 菜品分类列表
const dishFlavors = ref([]); // 口味做法列表 [{ name: "辣度", value: ["微辣", "中辣"] }]
const dishFlavorsData = ref([]); // 口味做法数据列表

// 路由
const router = useRouter();
const route = useRoute();

// 表单数据
const ruleForm = reactive({
  name: "",
  id: "",
  price: "",
  code: "",
  image: "",
  description: "",
  flavors: [],
  status: true,
  categoryId: "",
});

// 表单验证规则
const rules = reactive({
  name: [{ required: true, message: "请输入菜品名称", trigger: "change" }],
  categoryId: [
    { required: true, message: "请选择菜品分类", trigger: "change" },
  ],
  image: [{ required: true, message: "菜品图片不能为空" }],
  price: [
    { required: true, message: "请输入菜品价格", trigger: "change" },
    {
      pattern: /^\d+(\.\d{1,2})?$/,
      message: "请输入有效的价格，最多两位小数",
      trigger: "change",
    },
  ],
});

// 图片上传组件的 ref 属性
const uploadRef = ref();
const fileList = ref([]);

// 判断是添加还是编辑
const actionType = ref("add");
const itemFormRef = ref(null);
const dishId = ref(null);

// 计算属性：剩余可选口味
const availableFlavors = computed(() => {
  const selectedNames = dishFlavors.value.map((f) => f.name);
  return dishFlavorsData.value.filter(
    (f) => !selectedNames.includes(f.name)
  );
});

// 计算属性：是否可以添加口味
const canAddFlavor = computed(() => {
  return availableFlavors.value.length > 0;
});

// 获取口味标签列表
const getFlavorTags = (flavorName) => {
  const flavor = dishFlavorsData.value.find((f) => f.name === flavorName);
  return flavor ? flavor.value : [];
};

// 切换标签选中状态
const toggleTag = (index, tag) => {
  const currentValues = dishFlavors.value[index].value;
  const tagIndex = currentValues.indexOf(tag);

  if (tagIndex > -1) {
    // 已选中，移除
    currentValues.splice(tagIndex, 1);
  } else {
    // 未选中，添加
    currentValues.push(tag);
  }
};

// 添加口味
const addFlavor = () => {
  if (availableFlavors.value.length > 0) {
    const newFlavor = {
      id: Date.now(),
      name: availableFlavors.value[0].name,
      value: [],
    };
    dishFlavors.value.push(newFlavor);
  }
};

// 删除口味
const delFlavor = (index) => {
  dishFlavors.value.splice(index, 1);
};

// 上传图片
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
  const res = await upload(formData);
  if (res.code) {
    ElMessage.success(res.msg);
    ruleForm.image = "http://localhost:10086" + res.data;
  }
};

// 获取菜品分类
const getDishList = async () => {
  const res = await getCategoryList({ type: 1 });
  if (res.code === 200) {
    dishList.value = res.data;
  } else {
    ElMessage.error(res.msg || "获取菜品列表失败");
  }
};

// 获取菜品信息
const getDishInfo = async (id) => {
  const res = await queryDishById(id);
  if (res.code === 200) {
    const dishInfo = res.data;
    ruleForm.name = dishInfo.name;
    ruleForm.price = dishInfo.price;
    ruleForm.code = dishInfo.code;
    ruleForm.image = dishInfo.image;
    ruleForm.description = dishInfo.description;
    ruleForm.categoryId = dishInfo.categoryId;

    // 后端返回：[{name: "辣度", value: "微辣"},{name: "辣度", value: "中辣"}]
    // 前端需要：[{name: "辣度", value: ["微辣", "中辣"]}]
    const flavorMap = new Map();
    if (dishInfo.flavors && Array.isArray(dishInfo.flavors)) {
      dishInfo.flavors.forEach((flavor) => {
        if (!flavorMap.has(flavor.name)) {
          flavorMap.set(flavor.name, []);
        }
        flavorMap.get(flavor.name).push(flavor.value);
      });
    }
    ruleForm.flavors = Array.from(flavorMap.entries()).map(([name, values]) => ({
      name: name,
      value: values,
    }));

    fileList.value = [{ name: "菜品图片", url: dishInfo.image }];
  } else {
    ElMessage.error(res.msg || "获取菜品信息失败");
  }
};

// 获取口味列表
const getFlavorListHand = () => {
  dishFlavorsData.value = [
    { name: "甜味", value: ["无糖", "少糖", "半糖", "多糖", "全糖"] },
    { name: "温度", value: ["热饮", "常温", "去冰", "少冰", "多冰"] },
    { name: "忌口", value: ["不要葱", "不要蒜", "不要香菜", "不要辣"] },
    { name: "辣度", value: ["不辣", "微辣", "中辣", "重辣"] },
  ];
};

// 提交表单
const submitForm = async () => {
  console.log("提交表单数据：", ruleForm, dishFlavors.value);
  if (!itemFormRef.value) return;

  await itemFormRef.value.validate(async (valid) => {
    if (valid) {
      // 前端格式：[{name: "辣度", value: ["微辣", "中辣"]}]
      // 后端期望：[{name: "辣度", value: "微辣"},{name: "辣度", value: "中辣"}]
      const flavorsArr = [];
      dishFlavors.value.forEach((flavor) => {
        if (flavor.name && flavor.value && flavor.value.length > 0) {
          flavor.value.forEach((tag) => {
            if (tag && tag.trim()) {
              flavorsArr.push({
                name: flavor.name,
                value: tag.trim(),
              });
            }
          });
        }
      });

      const baseData = {
        name: ruleForm.name,
        categoryId: ruleForm.categoryId,
        price: parseFloat(ruleForm.price),
        image: ruleForm.image,
        description: ruleForm.description,
        status: 1,
        flavors: flavorsArr,
      };

      console.log("提交的口味数据：", baseData);

      const res = dishId.value
        ? await editDish({ ...baseData, id: dishId.value })
        : await addDish(baseData);

      if (res.code === 200) {
        ElMessage.success(dishId.value ? "修改成功" : "添加成功");
        router.back();
      } else {
        ElMessage.error(res.msg || "操作失败");
      }
    } else {
      ElMessage.error("表单校验失败");
    }
  });
};

// 重置表单
const resetForm = () => {
  ruleForm.name = "";
  ruleForm.price = "";
  ruleForm.code = "";
  ruleForm.image = "";
  ruleForm.description = "";
  ruleForm.categoryId = "";
  dishFlavors.value = [];
  fileList.value = [];
  if (uploadRef.value) {
    uploadRef.value.clearFiles();
  }
  if (itemFormRef.value) {
    itemFormRef.value.resetFields();
  }
};

// 初始化加载
onMounted(() => {
  getDishList();
  getFlavorListHand();

  dishId.value = route.query.id;

  if (route.query.id) {
    actionType.value = "edit";
    getDishInfo(route.query.id);
  } else {
    actionType.value = "add";
  }
});
</script>

<style lang="scss" scoped>
.flavor {
  width: 100%;
  .title {
    display: flex;
    margin-bottom: 10px;
    font-weight: bold;
  }
  .cont {
    .items {
      display: flex;
      align-items: center;
      margin-bottom: 15px;
      padding: 10px;
      background: #f5f7fa;
      border-radius: 4px;
      .itTit {
        min-width: 100px;
      }
      .lbltems {
        flex: 1;
      }
    }
  }
}
</style>