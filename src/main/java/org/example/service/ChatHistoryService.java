package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChatHistoryService {

    @Resource
    private StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String buildKey(String userId) {
        return "chat:history:" + userId;
    }

    public void saveMessage(String userId, String role, String content) {
        Map<String, String> message = Map.of("role", role, "content", content);
        try {
            String json = objectMapper.writeValueAsString(message);
            redisTemplate.opsForList().rightPush(buildKey(userId), json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Map<String, String>> getHistory(String userId) {
        List<String> historyJson = redisTemplate.opsForList().range(buildKey(userId), 0, -1);
        List<Map<String, String>> history = new ArrayList<>();
        try {
            for (String json : historyJson) {
                history.add(objectMapper.readValue(json, Map.class));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return history;
    }

    public void clearHistory(String userId) {
        redisTemplate.delete(buildKey(userId));
    }
}
