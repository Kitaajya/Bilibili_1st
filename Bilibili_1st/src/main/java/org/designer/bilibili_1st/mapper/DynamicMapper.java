package org.designer.bilibili_1st.mapper;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class DynamicMapper {
    private Logger log = LoggerFactory.getLogger(DynamicMapper.class);
    private final JdbcTemplate jdbcTemplate;

    //写动态
    public int writeDynamic(long userId, @Nullable String title, String contents) {
        if (contents == null || contents.isEmpty()) log.info("内容不得为空");
        return jdbcTemplate.update(
                "INSERT INTO dynamic(user_id,title,contents) VALUES (?,?,?)",
                userId, title, contents);
    }

    //编辑写过的动态
    public int editDynamic(long userId, long dynamicId, @Nullable String title, String contents) {
        return jdbcTemplate.update(
                "UPDATE dynamic SET title=?, contents=? WHERE id=? AND user_id=?",
                title, contents, dynamicId, userId);
    }

    //删除我自己的动态
    public int deleteMyDynamic(long userId, long dynamicId) {
        return jdbcTemplate.update(
                "DELETE FROM dynamic WHERE id=? AND user_id=?",
                dynamicId, userId);
    }

    //查看动态
    public List<Map<String, Object>> selectAllDynamic() {
        return jdbcTemplate.queryForList("SELECT * FROM dynamic");
    }

    //查看某个人的动态
    public List<Map<String, Object>> selectDynamicByUserId(long userId) {
        return jdbcTemplate.queryForList("SELECT * FROM dynamic WHERE user_id=?", userId);
    }
}
