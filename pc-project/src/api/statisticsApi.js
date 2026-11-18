import http from "@/http/index.js";

// 获取总体统计数据
export const getOverallStatistics = () => {
  return http.get("/statistics/overall");
};

// 获取今日统计数据
export const getTodayStatistics = () => {
  return http.get("/statistics/today");
};

// 获取订单趋势数据
export const getOrderTrend = (days = 7) => {
  return http.get("/statistics/order-trend", { params: { days } });
};

// 获取菜品销量排行
export const getDishRanking = (limit = 10) => {
  return http.get("/statistics/dish-ranking", { params: { limit } });
};

// 获取分类销量排行
export const getCategoryRanking = () => {
  return http.get("/statistics/category-ranking");
};