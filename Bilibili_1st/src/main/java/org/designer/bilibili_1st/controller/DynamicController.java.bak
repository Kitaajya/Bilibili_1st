package org.designer.bilibili_1st.controller;

import lombok.RequiredArgsConstructor;
import org.designer.bilibili_1st.common.Result;
import org.designer.bilibili_1st.mapper.DynamicMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dynamic")
@RequiredArgsConstructor
public class DynamicController {
    private final DynamicMapper dynamicMapper;

    @PostMapping("/write")
    public Result<Void> writeDynamic(@RequestParam long userId,
                                     @RequestParam(required = false) String title,
                                     @RequestParam String contents) {
        int k = dynamicMapper.writeDynamic(userId, title, contents);
        if (k == 0) return Result.fail("发布失败");
        return Result.successMessage("发布成功");
    }

    @PostMapping("/edit")
    public Result<Void> editDynamic(@RequestParam long userId,
                                    @RequestParam long dynamicId,
                                    @RequestParam(required = false) String title,
                                    @RequestParam String contents) {
        int k = dynamicMapper.editDynamic(userId, dynamicId, title, contents);
        if (k == 0) return Result.fail("修改失败");
        return Result.successMessage("修改成功");
    }

    @DeleteMapping("/delete")
    public Result<Void> deleteDynamic(@RequestParam long userId,
                                      @RequestParam long dynamicId) {
        int k = dynamicMapper.deleteMyDynamic(userId, dynamicId);
        if (k == 0) return Result.fail("删除失败");
        return Result.successMessage("删除成功");
    }

    @GetMapping("/all")
    public Result<List<Map<String, Object>>> selectAllDynamic() {
        return Result.success(dynamicMapper.selectAllDynamic());
    }

    @GetMapping("/byUser")
    public Result<List<Map<String, Object>>> selectDynamicByUserId(@RequestParam long userId) {
        return Result.success(dynamicMapper.selectDynamicByUserId(userId));
    }
}

