package cn.magic.mapper;

import cn.magic.dto.OrderPageQueryDTO;
import cn.magic.entity.Orders;
import cn.magic.vo.OrderVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {

    Page<OrderVO> pageQuery(@Param("page") Page<OrderVO> page, @Param("orderPageQueryDTO") OrderPageQueryDTO orderPageQueryDTO);

    @Select("SELECT COUNT(*) FROM orders WHERE status = #{status}")
    Long countByStatus(Integer status);

    @Select("SELECT * FROM orders WHERE id = #{id}")
    Orders getById(Long id);

    @Select("SELECT COALESCE(SUM(amount), 0) FROM orders WHERE status = 4")
    BigDecimal sumAmount();

    @Select("SELECT COALESCE(SUM(amount), 0) FROM orders WHERE status = 4 AND order_time BETWEEN #{startTime} AND #{endTime}")
    BigDecimal sumAmountByTime(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    List<Map<String, Object>> getOrderTrend(@Param("days") int days);

    List<Map<String, Object>> getDishRanking(@Param("limit") int limit);

    List<Map<String, Object>> getCategoryRanking();
}