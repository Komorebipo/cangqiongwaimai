package cn.magic.service;

import cn.magic.dto.OrderPageQueryDTO;
import cn.magic.entity.Orders;
import cn.magic.vo.OrderVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface OrderService extends IService<Orders> {

    Page<OrderVO> pageQuery(OrderPageQueryDTO orderPageQueryDTO);

    Map<String, Object> getOrderStats();

    void updateStatus(Long id, Integer status);

    Orders getById(Long id);
}