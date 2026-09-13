package org.designer.bilibili_1st.controller;

import lombok.RequiredArgsConstructor;
import org.designer.bilibili_1st.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    //用户主页信息（昵称、身份、投稿数、总播放、总获赞）
    @GetMapping("/profile")
    public Map<String, Object> profile(@RequestParam long userId) {
        return userService.getProfile(userId);
    }
}