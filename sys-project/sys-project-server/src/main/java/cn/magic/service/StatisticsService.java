package cn.magic.service;

import cn.magic.vo.StatisticsVO;

public interface StatisticsService {

    StatisticsVO getOverallStatistics();

    StatisticsVO getTodayStatistics();

    StatisticsVO getOrderTrend(int days);

    StatisticsVO getDishRanking(int limit);

    StatisticsVO getCategoryRanking();
}