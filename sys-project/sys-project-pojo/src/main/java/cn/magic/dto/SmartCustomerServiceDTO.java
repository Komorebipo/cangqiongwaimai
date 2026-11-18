package cn.magic.dto;

import lombok.Data;

/**
 * 智能客服消息发送请求DTO
 */
@Data
public class SmartCustomerServiceDTO {
    // 用户ID（必填）
    private String userId;
    //消息内容（必填）
    private String messageContent;
    //会话ID（首次为空，后续携带）
    private String conversationId;
}