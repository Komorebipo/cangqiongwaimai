// 引入封装好的axios
import http from "@/http/index.js";

// 菜品管理

// 查询列表接口
export const getDishPage = (data) => {
  return http.get("/dish/page", { params: data });
};

// 删除接口
export const deleteDish = (ids) => {
  console.log(ids);
  const idArray = Array.isArray(ids) ? ids : [ids];
  const queryString = idArray.map((id) => `ids=${id}`).join('&');
  return http.delete(`/dish?${queryString}`);
};

// 修改接口
export const editDish = (data) => {
  return http.put("/dish", data);
};

// 新增接口
export const addDish = (data) => {
  return http.post("/dish", data);
};

// 查询详情
export const queryDishById = (id) => {
  return http.get(`/dish/${id}`);
};

// 查菜品列表的接口
export const queryDishList = (data) => {
  return http.get("/dish/list", { params: data });
};

// 上传图片
export const upload = (data) => {
  return http.post("/common/upload", data);
};

// 起售停售——批量起售停售接口
export const dishStatusByStatus = (data) => {
  return http.post(`/dish/status/${data.status}/${data.id}`);
};

// 获取菜品分类列表
export const getCategoryList = (data) => {
  return http.get("/category/list", { params: data });
};