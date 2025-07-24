package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.entity.UserAccount;

@Mapper
public interface UserAccountMapper extends BaseMapper<UserAccount> {
}
