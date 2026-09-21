package org.designer.bilibili_1st.controller;

import lombok.RequiredArgsConstructor;
import org.designer.bilibili_1st.common.Result;
import org.designer.bilibili_1st.common.ResultMapper;
import org.designer.bilibili_1st.entity.LogInRequest;
import org.designer.bilibili_1st.service.LogInService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/log/in")
@RequiredArgsConstructor
public class LogInController {
    private final LogInService logInService;

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody LogInRequest request) {
        return ResultMapper.from(logInService.register(request));
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LogInRequest request) {
        return ResultMapper.from(logInService.login(request));
    }
}

