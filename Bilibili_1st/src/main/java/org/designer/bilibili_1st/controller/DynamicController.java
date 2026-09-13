package org.designer.bilibili_1st.controller;

import lombok.RequiredArgsConstructor;
import org.designer.bilibili_1st.mapper.DynamicMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dynamic")
@RequiredArgsConstructor
public class DynamicController {
    private final DynamicMapper dynamicMapper;

    //写动态
    @PostMapping("/write")
    public Map<String, Object> writeDynamic(@RequestParam long userId,
                                            @RequestParam(required = false) String title,
                                            @RequestParam String contents) {
        int k = dynamicMapper.writeDynamic(userId, title, contents);
        if (k == 0) return Map.of("success", false, "message", "发布失败");
        return Map.of("success", true, "message", "发布成功");
    }

    //编辑动态
    @PostMapping("/edit")
    public Map<String, Object> editDynamic(@RequestParam long userId,
                                           @RequestParam long dynamicId,
                                           @RequestParam(required = false) String title,
                                           @RequestParam String contents) {
        int k = dynamicMapper.editDynamic(userId, dynamicId, title, contents);
        if (k == 0) return Map.of("success", false, "message", "修改失败");
        return Map.of("success", true, "message", "修改成功");
    }

    //删除动态
    @DeleteMapping("/delete")
    public Map<String, Object> deleteDynamic(@RequestParam long userId,
                                             @RequestParam long dynamicId) {
        int k = dynamicMapper.deleteMyDynamic(userId, dynamicId);
        if (k == 0) return Map.of("success", false, "message", "删除失败");
        return Map.of("success", true, "message", "删除成功");
    }

    //查看全部动态
    @GetMapping("/all")
    public List<Map<String, Object>> selectAllDynamic() {
        return dynamicMapper.selectAllDynamic();
    }

    //查看某个人的动态
    @GetMapping("/byUser")
    public List<Map<String, Object>> selectDynamicByUserId(@RequestParam long userId) {
        return dynamicMapper.selectDynamicByUserId(userId);
    }
}