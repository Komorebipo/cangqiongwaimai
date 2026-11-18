package cn.magic.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

/**
 * DeepSeek大模型配置类
 * 读取配置文件中的DeepSeek API配置
 */
@Data
@Configuration
public class DeepSeekConfig {

    // DeepSeek API密钥（从DeepSeek控制台获取）
    private String apiKey = "sk-f4c723699dbc41cb9c704a31d10f36bc";

    //API请求地址（DeepSeek通用对话接口）
    private String apiUrl = "https://api.deepseek.com/v1/chat/completions";

    //模型名称（如deepseek-chat/deepseek-coder）
    private String model = "deepseek-chat";

    //温度参数（0-1，值越高回复越随机）
    private Float temperature = 0.7f;

    //最大生成token数
    private Integer maxTokens = 2048;

    // 外卖场景系统提示词
    private String systemPrompt = "你是外卖平台智能助手，负责：\n" +
            "1. 推荐餐厅和菜品（询问口味、预算、位置）\n" +
            "2. 处理订单问题（查单、催单、修改、取消）\n" +
            "3. 解决售后投诉（漏送、迟延、错送）\n" +
            "4. 介绍优惠活动和会员权益\n" +
            "规则：态度友好、解答准确、不越权操作、敏感问题转人工\n" +
            "回复格式：共情+解决方案+行动建议+适当emoji";
}