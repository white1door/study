package org.example.controller;

import jakarta.annotation.Resource;
import org.example.entity.UserAccount;
import org.example.service.UserAccountService;
import org.example.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    @Resource
    private UserAccountService userAccountService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam String username, @RequestParam String password) {
        UserAccount user = userAccountService.login(username, password);
        Map<String, Object> result = new HashMap<>();
        if (user != null) {
            String token = JwtUtil.generateToken(user.getUsername());
            result.put("token", token);
        } else {
            result.put("error", "用户名或密码错误");
        }
        return result;
    }
}
