package cn.magic.controller.admin;

import cn.magic.dto.OrderPageQueryDTO;
import cn.magic.entity.Orders;
import cn.magic.service.OrderService;
import cn.magic.utils.ResultVo;
import cn.magic.vo.OrderVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/order")
@Tag(name = "订单相关接口")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/page")
    @Operation(summary = "分页查询订单")
    public ResultVo page(OrderPageQueryDTO orderPageQueryDTO) {
        Page<OrderVO> pageResult = orderService.pageQuery(orderPageQueryDTO);
        return ResultVo.success(pageResult);
    }

    @GetMapping("/stats")
    @Operation(summary = "获取订单统计数据")
    public ResultVo getStats() {
        Map<String, Object> stats = orderService.getOrderStats();
        return ResultVo.success(stats);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据id查询订单")
    public ResultVo getById(@PathVariable Long id) {
        Orders order = orderService.getById(id);
        return ResultVo.success(order);
    }

    @PutMapping("/status/{status}/{id}")
    @Operation(summary = "更新订单状态")
    public ResultVo updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        orderService.updateStatus(id, status);
        String msg = status == 2 ? "接单成功" : status == 3 ? "发货成功" : "更新成功";
        return ResultVo.success(msg);
    }
}