package cn.magic.service.impl;

import cn.magic.entity.Orders;
import cn.magic.mapper.*;
import cn.magic.service.StatisticsService;
import cn.magic.vo.StatisticsVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    public StatisticsVO getOverallStatistics() {
        StatisticsVO statisticsVO = new StatisticsVO();

        Long totalOrders = orderMapper.selectCount(null);
        statisticsVO.setTotalOrders(totalOrders != null ? totalOrders : 0L);

        BigDecimal totalRevenue = orderMapper.sumAmount();
        statisticsVO.setTotalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        Long totalUsers = employeeMapper.selectCount(null);
        statisticsVO.setTotalUsers(totalUsers != null ? totalUsers : 0L);

        Long totalDishes = dishMapper.selectCount(null);
        statisticsVO.setTotalDishes(totalDishes != null ? totalDishes : 0L);

        Long totalSetmeals = setmealMapper.selectCount(null);
        statisticsVO.setTotalSetmeals(totalSetmeals != null ? totalSetmeals : 0L);

        return statisticsVO;
    }

    @Override
    public StatisticsVO getTodayStatistics() {
        StatisticsVO statisticsVO = new StatisticsVO();

        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.between(Orders::getOrderTime, todayStart, todayEnd);

        Long todayOrders = orderMapper.selectCount(queryWrapper);
        statisticsVO.setTodayOrders(todayOrders != null ? todayOrders : 0L);

        BigDecimal todayRevenue = orderMapper.sumAmountByTime(todayStart, todayEnd);
        statisticsVO.setTodayRevenue(todayRevenue != null ? todayRevenue : BigDecimal.ZERO);

        return statisticsVO;
    }

    @Override
    public StatisticsVO getOrderTrend(int days) {
        StatisticsVO statisticsVO = new StatisticsVO();

        List<Map<String, Object>> orderTrend = orderMapper.getOrderTrend(days);
        statisticsVO.setOrderTrend(orderTrend);

        return statisticsVO;
    }

    @Override
    public StatisticsVO getDishRanking(int limit) {
        StatisticsVO statisticsVO = new StatisticsVO();

        List<Map<String, Object>> dishRanking = orderMapper.getDishRanking(limit);
        statisticsVO.setDishRanking(dishRanking);

        return statisticsVO;
    }

    @Override
    public StatisticsVO getCategoryRanking() {
        StatisticsVO statisticsVO = new StatisticsVO();

        List<Map<String, Object>> categoryRanking = orderMapper.getCategoryRanking();
        statisticsVO.setCategoryRanking(categoryRanking);

        return statisticsVO;
    }
}