package cn.magic.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsVO {

    private Long totalOrders;

    private BigDecimal totalRevenue;

    private Long totalUsers;

    private Long totalDishes;

    private Long totalSetmeals;

    private Long todayOrders;

    private BigDecimal todayRevenue;

    private Long todayNewUsers;

    private List<Map<String, Object>> orderTrend;

    private List<Map<String, Object>> dishRanking;

    private List<Map<String, Object>> categoryRanking;
}