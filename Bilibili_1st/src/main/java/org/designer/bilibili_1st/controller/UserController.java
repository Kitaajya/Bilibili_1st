package org.designer.bilibili_1st.controller;

import lombok.RequiredArgsConstructor;
import org.designer.bilibili_1st.common.Result;
import org.designer.bilibili_1st.common.ResultMapper;
import org.designer.bilibili_1st.mapper.UserMapper;
import org.designer.bilibili_1st.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/profile")
    public Result<Map<String, Object>> profile(@RequestParam long userId) {
        return ResultMapper.from(userService.getProfile(userId));
    }
    //更新头像
    @PostMapping("/update/avatar")
    public Result<Map<String,Object>> updateAvatar(
            @RequestParam long userId,
            @RequestParam("file") MultipartFile file) {
        return Result.success(userService.updateAvatar(userId, file));
    }

    //读取头像图片：/api/user/avatar/xxx.png
    @GetMapping("/avatar/{fileName}")
    public ResponseEntity<Resource> getAvatar(@PathVariable String fileName) {
        File f = userService.getAvatarFile(fileName);
        if (f == null) return ResponseEntity.notFound().build();
        String ct = fileName.endsWith(".png") ? "image/png"
                : fileName.endsWith(".gif") ? "image/gif"
                : fileName.endsWith(".webp") ? "image/webp" : "image/jpeg";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(ct))
                .header(HttpHeaders.CACHE_CONTROL, "max-age=86400")
                .body(new FileSystemResource(f));
    }
    //改网名
    @PostMapping("/edit/virtualName")
    public Result<Map<String,Object>> editVirtualName(long id,String virtualName){
        return Result.success(userService.editVirtualName(id, virtualName));
    }
    //注销账号（软删除）
    @DeleteMapping("/delete/account")
    public Result<Map<String,Object>> deleteAccount(@RequestParam long userId){
        if(userService.deleteAccount(userId)==0)
            return Result.fail("注销失败（用户不存在）");
        return Result.successMessage("注销成功");
    }
}

