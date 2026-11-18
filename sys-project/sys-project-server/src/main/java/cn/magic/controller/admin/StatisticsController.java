package cn.magic.controller.admin;

import cn.magic.service.StatisticsService;
import cn.magic.utils.ResultVo;
import cn.magic.vo.StatisticsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/statistics")
@Tag(name = "数据统计相关接口")
@Slf4j
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/overall")
    @Operation(summary = "获取总体统计数据")
    public ResultVo getOverallStatistics() {
        StatisticsVO statistics = statisticsService.getOverallStatistics();
        return ResultVo.success(statistics);
    }

    @GetMapping("/today")
    @Operation(summary = "获取今日统计数据")
    public ResultVo getTodayStatistics() {
        StatisticsVO statistics = statisticsService.getTodayStatistics();
        return ResultVo.success(statistics);
    }

    @GetMapping("/order-trend")
    @Operation(summary = "获取订单趋势数据")
    public ResultVo getOrderTrend(@RequestParam(defaultValue = "7") int days) {
        StatisticsVO statistics = statisticsService.getOrderTrend(days);
        return ResultVo.success(statistics.getOrderTrend());
    }

    @GetMapping("/dish-ranking")
    @Operation(summary = "获取菜品销量排行")
    public ResultVo getDishRanking(@RequestParam(defaultValue = "10") int limit) {
        StatisticsVO statistics = statisticsService.getDishRanking(limit);
        return ResultVo.success(statistics.getDishRanking());
    }

    @GetMapping("/category-ranking")
    @Operation(summary = "获取分类销量排行")
    public ResultVo getCategoryRanking() {
        StatisticsVO statistics = statisticsService.getCategoryRanking();
        return ResultVo.success(statistics.getCategoryRanking());
    }
}