package org.designer.bilibili_1st.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.designer.bilibili_1st.entity.LogInEntity;
import org.designer.bilibili_1st.entity.LogInRequest;
import org.designer.bilibili_1st.mapper.LogInMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogInService {

    private final LogInMapper logInMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Map<String, Object> register(LogInRequest request) {
        if (request.getQqEmail() == null || request.getQqEmail().isBlank()
                || request.getPhoneNumber() == null || request.getPhoneNumber().isBlank()) {
            return Map.of("success", false, "message", "账号（邮箱或手机号）、昵称、密码不能为空");
        }
        if (request.getVirtualName() == null || request.getVirtualName().isBlank()) {
            return Map.of("success", false, "message", "昵称不能为空");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return Map.of("success", false, "message", "密码不能为空");
        }
        if (logInMapper.existsByQqEmail(request.getQqEmail())) {
            return Map.of("success", false, "message", "该邮箱已注册");
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()
                && logInMapper.existsByPhoneNumber(request.getPhoneNumber())) {
            return Map.of("success", false, "message", "该手机号已注册");
        }

        LogInEntity entity = new LogInEntity();
        entity.setQqEmail(request.getQqEmail());
        entity.setPhoneNumber(request.getPhoneNumber());
        entity.setRealName(request.getRealName());
        entity.setVirtualName(request.getVirtualName());
        entity.setPassword(passwordEncoder.encode(request.getPassword()));
        entity.setRole("USER");

        logInMapper.save(entity);
        log.info("新用户注册: {}", entity.getQqEmail());
        return Map.of("success", true, "message", "注册成功",
                "userId", entity.getId(), "virtualName", entity.getVirtualName(),
                "role", entity.getRole());
    }

    public Map<String, Object> login(LogInRequest request) {
        if (request.getQqEmail() == null || request.getQqEmail().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {
            return Map.of("success", false, "message", "邮箱或密码不能为空");
        }

        LogInEntity user = logInMapper.findByQqEmail(request.getQqEmail()).orElse(null);
        if (user == null) {
            return Map.of("success", false, "message", "用户不存在");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return Map.of("success", false, "message", "密码错误");
        }
        log.info("用户登录: {}", request.getQqEmail());
        return Map.of("success", true, "message", "登录成功",
                "userId", user.getId(), "virtualName", user.getVirtualName(),
                "role", user.getRole());
    }
}
