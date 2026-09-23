package org.designer.bilibili_1st.controller;

import lombok.RequiredArgsConstructor;
import org.designer.bilibili_1st.common.Result;
import org.designer.bilibili_1st.mapper.FollowMapper;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {
    private final FollowMapper followMapper;

    //关注/取关 toggle
    @PostMapping("/toggle")
    public Result<Map<String, Object>> toggle(@RequestParam long followerId,
                                              @RequestParam long followingId) {
        if (followerId == followingId) return Result.fail("不能关注自己");
        boolean nowFollowing;
        if (followMapper.isFollowing(followerId, followingId) > 0) {
            followMapper.unfollow(followerId, followingId);
            nowFollowing = false;
        } else {
            followMapper.follow(followerId, followingId);
            nowFollowing = true;
        }
        Map<String, Object> m = new HashMap<>();
        m.put("following", nowFollowing);
        m.put("followers", followMapper.countFollowers(followingId));
        return Result.success(m);
    }

    //某人的关注/粉丝数 + 我是否已关注
    @GetMapping("/status")
    public Result<Map<String, Object>> status(@RequestParam long userId,
                                              @RequestParam(required = false) Long viewerId) {
        Map<String, Object> m = new HashMap<>();
        m.put("followingCount", followMapper.countFollowing(userId));
        m.put("followerCount", followMapper.countFollowers(userId));
        m.put("isFollowing", viewerId != null && followMapper.isFollowing(viewerId, userId) > 0);
        return Result.success(m);
    }

    @GetMapping("/following")
    public Result<List<Map<String, Object>>> following(@RequestParam long userId) {
        return Result.success(followMapper.selectFollowing(userId));
    }

    @GetMapping("/followers")
    public Result<List<Map<String, Object>>> followers(@RequestParam long userId) {
        return Result.success(followMapper.selectFollowers(userId));
    }
}