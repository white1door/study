package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AliAiService {

    private static final Logger logger = LoggerFactory.getLogger(AliAiService.class);

    @Value("${ali.api-key}")
    private String apiKey;

    @Value("${ali.endpoint}")
    private String endpoint;

    private final RestTemplate restTemplate = new RestTemplate();

    @Resource
    private ChatHistoryService chatHistoryService;

    public String askAliLLM(String userId, String prompt, String model) {
        try {
            List<Map<String, String>> messages = new ArrayList<>();

            messages.add(Map.of("role", "system", "content", "你是一个能记住上下文且可以联网查询的的助手。"));

            List<Map<String, String>> history = chatHistoryService.getHistory(userId);
            if (history != null && !history.isEmpty()) {
                messages.addAll(history);
            }

            messages.add(Map.of("role", "user", "content", prompt));

            chatHistoryService.saveMessage(userId, "user", prompt);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            // 🚩 阿里云 DashScope 要求正确格式
            Map<String, Object> input = new HashMap<>();
            input.put("messages", messages);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("result_format", "message");
            parameters.put("enable_search",true);
            parameters.put("forced_search",true);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("input", input);
            requestBody.put("parameters", parameters);


            logger.info("发送请求体：{}", new ObjectMapper().writeValueAsString(requestBody));

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(endpoint, requestEntity, String.class);

            logger.info("API原始响应：{}", response.getBody());

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), Map.class);

            String content = null;
            if (responseBody.containsKey("output")) {
                Map<String, Object> output = (Map<String, Object>) responseBody.get("output");
                List<Map<String, Object>> choices = (List<Map<String, Object>>) output.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, String> message = (Map<String, String>) choice.get("message");
                    content = message.get("content");
                    chatHistoryService.saveMessage(userId, "assistant", content);
                }
            }

            return content != null ? content : "AI 没返回内容。";
        } catch (Exception e) {
            logger.error("请求出错", e);
            return "出错了：" + e.getMessage();
        }
    }

    public String askAliLLM(String userId, String prompt) {
        return askAliLLM(userId, prompt, "qwen-plus");
    }
}
