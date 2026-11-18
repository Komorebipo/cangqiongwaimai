package cn.magic.service.impl;

import cn.magic.dto.OrderPageQueryDTO;
import cn.magic.entity.Orders;
import cn.magic.mapper.OrderMapper;
import cn.magic.service.OrderService;
import cn.magic.vo.OrderVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Page<OrderVO> pageQuery(OrderPageQueryDTO orderPageQueryDTO) {
        Page<OrderVO> page = new Page<>(
                orderPageQueryDTO.getCurPage() != null ? orderPageQueryDTO.getCurPage() : 1,
                orderPageQueryDTO.getPageSize() != null ? orderPageQueryDTO.getPageSize() : 10
        );
        return orderMapper.pageQuery(page, orderPageQueryDTO);
    }

    @Override
    public Map<String, Object> getOrderStats() {
        Map<String, Object> stats = new HashMap<>();

        Long allCount = orderMapper.countByStatus(null);
        Long pendingCount = orderMapper.countByStatus(1);
        Long acceptedCount = orderMapper.countByStatus(2);
        Long deliveringCount = orderMapper.countByStatus(3);
        Long completedCount = orderMapper.countByStatus(4);
        Long canceledCount = orderMapper.countByStatus(5);

        stats.put("all", allCount != null ? allCount : 0);
        stats.put("pending", pendingCount != null ? pendingCount : 0);
        stats.put("accepted", acceptedCount != null ? acceptedCount : 0);
        stats.put("delivering", deliveringCount != null ? deliveringCount : 0);
        stats.put("completed", completedCount != null ? completedCount : 0);
        stats.put("canceled", canceledCount != null ? canceledCount : 0);

        return stats;
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        Orders order = new Orders();
        order.setId(id);
        order.setStatus(status);
        orderMapper.updateById(order);
    }

    @Override
    public Orders getById(Long id) {
        return orderMapper.getById(id);
    }
}