import http from "@/http/index.js";

export const getSetmealPage = (data) => {
  return http.get("/setmeal/page", { params: data });
};

export const deleteSetmeal = (ids) => {
  return http.delete(`/setmeal?${ids}`);
};

export const editSetmeal = (data) => {
  return http.put("/setmeal", data);
};

export const addSetmeal = (data) => {
  return http.post("/setmeal", data);
};

export const querySetmealById = (id) => {
  return http.get(`/setmeal/${id}`);
};

export const querySetmealList = (data) => {
  return http.get("/setmeal/list", { params: data });
};

export const upload = (data) => {
  return http.post("/common/upload", data);
};

export const setmealStatusByStatus = (data) => {
  return http.post(`/setmeal/status/${data.status}/${data.id}`);
};

export const getCategoryList = (data) => {
  return http.get("/category/list", { params: data });
};