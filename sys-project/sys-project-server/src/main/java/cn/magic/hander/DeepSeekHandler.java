package cn.magic.hander;

import cn.magic.config.DeepSeekConfig;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * DeepSeek大模型调用工具类处理
 * 手动实现HTTP调用，避免Spring AI依赖冲突
 */
@Slf4j
@Component
public class DeepSeekHandler {
    private final DeepSeekConfig deepSeekConfig;
    private final WebClient webClient;

    public DeepSeekHandler(DeepSeekConfig deepSeekConfig) {
        this.deepSeekConfig = deepSeekConfig;
        this.webClient = WebClient.create();
    }

    /**
     * 调用DeepSeek API获取AI回复
     * @param userQuestion 用户问题
     * @return AI回复内容
     */
    public String getAIReply(String userQuestion) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", deepSeekConfig.getModel());
            requestBody.put("temperature", deepSeekConfig.getTemperature());    
            requestBody.put("max_tokens", deepSeekConfig.getMaxTokens());       

            JSONArray messages = new JSONArray();

            JSONObject systemMsg = new JSONObject();
            systemMsg.put("role", "system");
            systemMsg.put("content", deepSeekConfig.getSystemPrompt());
            messages.add(systemMsg);

            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", userQuestion);
            messages.add(userMsg);

            requestBody.put("messages", messages);

            String response = webClient.post()
                    .uri(deepSeekConfig.getApiUrl())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + deepSeekConfig.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody.toJSONString())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JSONObject responseObj = JSON.parseObject(response);
            JSONArray choices = responseObj.getJSONArray("choices");
            if (choices != null && !choices.isEmpty()) {
                JSONObject choice = choices.getJSONObject(0);
                JSONObject message = choice.getJSONObject("message");
                return message.getString("content").trim();
            }

            log.error("DeepSeek API返回结果异常：{}", response);
            return "抱歉，我暂时无法理解您的问题，请换一种方式提问。";
        } catch (Exception e) {
            log.error("调用DeepSeek API失败：", e);
            return "抱歉，智能客服暂时无法回复您的问题，请稍后再试！";
        }
    }
}