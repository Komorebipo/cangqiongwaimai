package cn.magic.controller.admin;

import cn.magic.dto.SmartCustomerServiceDTO;
import cn.magic.service.SmartCustomerServiceService;
import cn.magic.utils.ResultVo;
import cn.magic.vo.SmartCustomerServiceVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 智能客服控制器
 * 提供消息发送、历史查询、标记已读接口
 */
@RestController
@RequestMapping("/admin/msg")
@CrossOrigin
@RequiredArgsConstructor
public class SmartCustomerServiceController {

    private final SmartCustomerServiceService conversationService;

    /**
     * 发送消息并获取AI回复
     * POST /admin/msg/send
     */
    @PostMapping("/send")
    public ResultVo sendMsg(@Valid @RequestBody SmartCustomerServiceDTO request) {
        SmartCustomerServiceVO response = conversationService.sendMsgAndGetAIReply(request);
        return ResultVo.success(response);
    }

    /**
     * 查询历史对话记录
     * GET /admin/msg/history?userId=xxx&conversationId=xxx
     */
    @GetMapping("/history")
    public ResultVo queryHistoryMsg(
            @RequestParam String userId,
            @RequestParam(required = false) String conversationId) {
        List<SmartCustomerServiceVO> msgList = conversationService.queryHistoryMsg(userId, conversationId);
        return ResultVo.success(msgList);
    }

    /**
     * 标记消息为已读
     * POST /admin/msg/mark-read?userId=xxx&conversationId=xxx
     */
    @PostMapping("/mark-read")
    public ResultVo markMsgAsRead(
            @RequestParam String userId,
            @RequestParam String conversationId) {
        Boolean result = conversationService.markMsgAsRead(userId, conversationId);
        return ResultVo.success(result);
    }
}