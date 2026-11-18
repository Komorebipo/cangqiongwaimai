package cn.magic.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.magic.dto.SmartCustomerServiceDTO;
import cn.magic.entity.SmartCustomerService;
import cn.magic.hander.DeepSeekHandler;
import cn.magic.mapper.SmartCustomerServiceMapper;
import cn.magic.service.SmartCustomerServiceService;
import cn.magic.vo.SmartCustomerServiceVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 智能客服对话服务实现类
 * 核心：集成DeepSeek AI调用，实现AI回复
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmartCustomerServiceServiceImpl extends ServiceImpl<SmartCustomerServiceMapper, SmartCustomerService> implements SmartCustomerServiceService {     

    private final DeepSeekHandler deepSeekUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SmartCustomerServiceVO sendMsgAndGetAIReply(SmartCustomerServiceDTO request) {
        try {
            String conversationId = request.getConversationId() == null || request.getConversationId().isEmpty()
                    ? IdUtil.fastSimpleUUID() : request.getConversationId();    

            SmartCustomerService userMsg = new SmartCustomerService();
            userMsg.setUserId(request.getUserId());
            userMsg.setMessageContent(request.getMessageContent());
            userMsg.setMessageType(1);
            userMsg.setSendTime(LocalDateTime.now());
            userMsg.setIsRead(1);
            userMsg.setConversationId(conversationId);
            this.save(userMsg);

            String aiReplyContent = deepSeekUtil.getAIReply(request.getMessageContent());
            log.info("DeepSeek AI回复：{}", aiReplyContent);

            SmartCustomerService aiMsg = new SmartCustomerService();
            aiMsg.setUserId(request.getUserId());
            aiMsg.setMessageContent(aiReplyContent);
            aiMsg.setMessageType(2);
            aiMsg.setSendTime(LocalDateTime.now());
            aiMsg.setIsRead(0);
            aiMsg.setConversationId(conversationId);
            this.save(aiMsg);

            SmartCustomerServiceVO response = new SmartCustomerServiceVO();     
            response.setId(aiMsg.getId());
            response.setUserId(aiMsg.getUserId());
            response.setMessageContent(aiMsg.getMessageContent());
            response.setMessageType(aiMsg.getMessageType());
            response.setSendTime(aiMsg.getSendTime());
            response.setIsRead(aiMsg.getIsRead());
            response.setConversationId(conversationId);
            response.setIsSelf(false);
            return response;
        } catch (Exception e) {
            log.error("处理智能客服消息失败：", e);
            String conversationId = request.getConversationId() == null ? IdUtil.fastSimpleUUID() : request.getConversationId();
            SmartCustomerService errorMsg = new SmartCustomerService();
            errorMsg.setUserId(request.getUserId());
            errorMsg.setMessageContent("抱歉，智能客服暂时无法回复您的问题，请稍后再试！");
            errorMsg.setMessageType(2);
            errorMsg.setSendTime(LocalDateTime.now());
            errorMsg.setIsRead(0);
            errorMsg.setConversationId(conversationId);
            this.save(errorMsg);

            SmartCustomerServiceVO response = new SmartCustomerServiceVO();     
            response.setMessageContent(errorMsg.getMessageContent());
            response.setMessageType(2);
            response.setSendTime(errorMsg.getSendTime());
            response.setConversationId(errorMsg.getConversationId());
            response.setIsSelf(false);
            return response;
        }
    }

    @Override
    public List<SmartCustomerServiceVO> queryHistoryMsg(String userId, String conversationId) {
        List<SmartCustomerServiceVO> msgList = baseMapper.queryHistoryMsg(userId, conversationId);
        return msgList.stream().map(msg -> {
            msg.setIsSelf(msg.getMessageType() == 1);
            return msg;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean markMsgAsRead(String userId, String conversationId) {        
        int affectRows = baseMapper.markMsgAsRead(userId, conversationId);      
        return affectRows > 0;
    }
}