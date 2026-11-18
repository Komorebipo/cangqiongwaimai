package cn.magic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 智能客服对话记录实体类
 * 对应数据表smart_customer_service
 */
@Data
@TableName("smart_customer_service")
public class SmartCustomerService {
    // 主键ID
    @TableId(type = IdType.AUTO)
    private Long id;
    // 发起咨询的用户ID
    private String userId;
    // 消息内容
    private String messageContent;
    //消息类型 1-用户消息 2-AI回复
    private Integer messageType;
    //发送/回复时间
    private LocalDateTime sendTime;
    //是否已读 0-未读 1-已读
    private Integer isRead;
    //会话ID（关联同一次对话）
    private String conversationId;
}