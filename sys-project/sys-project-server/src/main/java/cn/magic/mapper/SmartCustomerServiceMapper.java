package cn.magic.mapper;

import cn.magic.entity.SmartCustomerService;
import cn.magic.vo.SmartCustomerServiceVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 智能客服对话记录Mapper接口
 */
public interface SmartCustomerServiceMapper extends BaseMapper<SmartCustomerService> {
    /**
     * 查询用户历史对话记录
     * @param userId 用户ID
     * @param conversationId 会话ID（为空则查询所有）
     * @return 对话记录列表
     */
    List<SmartCustomerServiceVO> queryHistoryMsg(
            @Param("userId") String userId,
            @Param("conversationId") String conversationId);

    /**
     * 标记消息为已读
     * @param userId 用户ID
     * @param conversationId 会话ID
     * @return 影响行数
     */
    int markMsgAsRead(
            @Param("userId") String userId,
            @Param("conversationId") String conversationId);
}