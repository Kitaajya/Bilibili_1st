package org.designer.bilibili_1st.service;

import lombok.RequiredArgsConstructor;
import org.designer.bilibili_1st.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;

    //用户主页信息
    public Map<String, Object> getProfile(long userId) {
        List<Map<String, Object>> list = userMapper.selectUserProfile(userId);
        if (list.isEmpty()) return Map.of("success", false, "message", "用户不存在");
        Map<String, Object> user = list.get(0);
        user.put("success", true);
        return user;
    }
}