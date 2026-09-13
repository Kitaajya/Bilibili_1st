package org.designer.bilibili_1st.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class UserMapper {
    private final JdbcTemplate jdbcTemplate;

    //用户主页信息：昵称、身份、投稿数、总播放、总获赞
    public List<Map<String, Object>> selectUserProfile(long userId) {
        return jdbcTemplate.queryForList("""
                SELECT u.id, u.virtualName, u.role,
                       (SELECT COUNT(*) FROM video v WHERE v.user_id = u.id) AS videoCount,
                       (SELECT COALESCE(SUM(v.view_count),0) FROM video v WHERE v.user_id = u.id) AS totalView,
                       (SELECT COUNT(*) FROM video v
                         JOIN video_like vl ON vl.video_id = v.id
                         WHERE v.user_id = u.id) AS totalLike
                FROM bilibili_user_log_in u
                WHERE u.id = ?
                """, userId);
    }
}