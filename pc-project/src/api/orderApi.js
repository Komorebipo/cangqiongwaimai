import http from "@/http/index.js";

export const getOrderPage = (data) => {
  return http.get("/order/page", { params: data });
};

export const getOrderStats = () => {
  return http.get("/order/stats");
};

export const getOrderById = (id) => {
  return http.get(`/order/${id}`);
};

export const updateOrderStatus = (status, id) => {
  return http.put(`/order/status/${status}/${id}`);
};