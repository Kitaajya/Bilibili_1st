package org.designer.bilibili_1st.controller;

import lombok.RequiredArgsConstructor;
import org.designer.bilibili_1st.common.Result;
import org.designer.bilibili_1st.common.ResultMapper;
import org.designer.bilibili_1st.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public Result<Map<String, Object>> profile(@RequestParam long userId) {
        return ResultMapper.from(userService.getProfile(userId));
    }
}

