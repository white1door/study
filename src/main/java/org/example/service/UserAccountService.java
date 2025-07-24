package org.example.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.example.entity.UserAccount;
import org.example.mapper.UserAccountMapper;
import org.springframework.stereotype.Service;

@Service
public class UserAccountService {

    @Resource
    private UserAccountMapper userAccountMapper;

    public UserAccount login(String username, String password) {
        return userAccountMapper.selectOne(new QueryWrapper<UserAccount>()
                .eq("username", username)
                .eq("password", password)); // 实际场景中需加密比对
    }

    public void register(String username, String password) {
        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setPassword(password); // 实际应做加密
        userAccountMapper.insert(user);
    }
}
