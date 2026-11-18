package cn.magic.vo;

import cn.magic.entity.Orders;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderVO extends Orders {
    private String categoryName;
}