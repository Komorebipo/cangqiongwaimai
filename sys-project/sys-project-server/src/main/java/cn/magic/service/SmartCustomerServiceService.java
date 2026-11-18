package cn.magic.service;

import cn.magic.dto.SmartCustomerServiceDTO;
import cn.magic.entity.SmartCustomerService;
import cn.magic.vo.SmartCustomerServiceVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 智能客服对话服务接口
 */
public interface SmartCustomerServiceService extends IService<SmartCustomerService> {
    /**
     * 发送用户消息并调用DeepSeek AI回复
     * @param request 发送请求
     * @return AI回复结果
     */
    SmartCustomerServiceVO sendMsgAndGetAIReply(SmartCustomerServiceDTO request);

    /**
     * 查询用户历史对话记录
     * @param userId 用户ID
     * @param conversationId 会话ID
     * @return 对话记录列表
     */
    List<SmartCustomerServiceVO> queryHistoryMsg(String userId, String conversationId);

    /**
     * 标记消息为已读
     * @param userId 用户ID
     * @param conversationId 会话ID
     * @return 标记结果
     */
    Boolean markMsgAsRead(String userId, String conversationId);
}