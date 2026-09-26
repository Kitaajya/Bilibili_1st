package org.designer.bilibili_1st.service;

import lombok.RequiredArgsConstructor;
//import lombok.Value;这个Value与下面的Value同名重复了
import org.designer.bilibili_1st.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;

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
    @Value("${file.upload.path}")
    private String uploadPath;
    //收文件
    public Map<String, Object> updateAvatar(long userId, MultipartFile file) {
        if (file == null || file.isEmpty())
            return Map.of("success", false, "message", "请选择头像图片");
        try {
            File dir = new File(uploadPath, "avatars");
            if (!dir.exists() && !dir.mkdirs())
                return Map.of("success", false, "message", "头像目录创建失败");
            String original = file.getOriginalFilename();

            if (original==null||original.isEmpty()) original = "avatar.png";
            //字符检验
            String ext = original.contains(".")
                    ? original.substring(original.lastIndexOf('.')).toLowerCase() : ".png";
            String savedName = "u" + userId + "_" + UUID.randomUUID() + ext;

            file.transferTo(new File(dir, savedName));
            userMapper.updateAvatar(userId, savedName);

            return Map.of("success", true, "message", "头像更新成功", "avatar", savedName);
        } catch (IOException e) {
            return Map.of("success", false, "message", "上传失败：" + e.getMessage());
        }
    }
    //查看头像
    public String selectAvatar(long userId){
        return userMapper.selectAvatar(userId);
    }

    //读取头像文件（用于 GET 接口返回图片），防目录穿越
    public File getAvatarFile(String fileName) {
        if (fileName == null || fileName.isBlank()) return null;
        String safe = new File(fileName).getName();
        File f = new File(new File(uploadPath, "avatars"), safe);
        return f.exists() && f.isFile() ? f : null;
    }
    //修改B站用户名
    public Map<String,Object> editVirtualName(long id,String virtualName){
        if(virtualName==null||virtualName.isBlank())
            return Map.of("success",false,"message","用户名不能为空");
        if(userMapper.editVirtualName(id, virtualName)==0)
            return Map.of("success",false,"message","更名失败");
        return Map.of("success",true,"message","更名成功");
    }
    //注销自己的账号（软删除）
    public int deleteAccount(long userId){
        return userMapper.deleteAccount(userId);
    }
}