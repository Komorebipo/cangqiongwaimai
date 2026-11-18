package cn.magic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("orders")
public class Orders implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String number;

    @TableField("user_id")
    private Long userId;

    @TableField("address_book_id")
    private Long addressBookId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("order_time")
    private LocalDateTime orderTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("checkout_time")
    private LocalDateTime checkoutTime;

    @TableField("pay_method")
    private Integer payMethod;

    @TableField("pay_status")
    private Integer payStatus;

    private BigDecimal amount;

    private String remark;

    private String phone;

    private String address;

    @TableField("user_name")
    private String userName;

    private String consignee;

    private Integer status;

    @TableField("delivery_status")
    private Integer deliveryStatus;

    @TableField("cancel_reason")
    private String cancelReason;

    @TableField("rejection_reason")
    private String rejectionReason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("cancel_time")
    private LocalDateTime cancelTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("estimated_delivery_time")
    private LocalDateTime estimatedDeliveryTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("delivery_time")
    private LocalDateTime deliveryTime;

    @TableField("pack_amount")
    private Integer packAmount;

    @TableField("tableware_number")
    private Integer tablewareNumber;

    @TableField("tableware_status")
    private Integer tablewareStatus;
}