package cn.magic.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 智能客服消息响应VO
 */
@Data
public class SmartCustomerServiceVO {
    // 消息ID
    private Long id;
    //用户ID
    private String userId;
    //消息内容
    private String messageContent;
    //消息类型 1-用户消息 2-AI回复
    private Integer messageType;
    //发送/回复时间
    private LocalDateTime sendTime;
    //是否已读
    private Integer isRead;
    // 会话ID
    private String conversationId;
    //前端展示用：是否为用户发送的消息
    private Boolean isSelf;
}