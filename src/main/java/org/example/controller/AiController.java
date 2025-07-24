package org.example.controller;

import org.example.service.AliAiService;
import org.example.service.ChatHistoryService;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/ai")
@CrossOrigin
public class AiController {

    @Resource
    private AliAiService aliAiService;

    @Resource
    private ChatHistoryService chatHistoryService;

    @GetMapping("/ask")
    public String ask(@RequestParam String userId, @RequestParam String prompt) {
        return aliAiService.askAliLLM(userId, prompt);
    }

    @GetMapping("/clear")
    public String clear(@RequestParam String userId) {
        chatHistoryService.clearHistory(userId);
        return "已清空用户对话历史：" + userId;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamAsk(@RequestParam String userId, @RequestParam String prompt) {
        SseEmitter emitter = new SseEmitter(0L);
        new Thread(() -> {
            try {
                String answer = aliAiService.askAliLLM(userId, prompt);
                for (char c : answer.toCharArray()) {
                    emitter.send(SseEmitter.event().data(String.valueOf(c)));
                    Thread.sleep(50);
                }
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();
        return emitter;
    }
}
