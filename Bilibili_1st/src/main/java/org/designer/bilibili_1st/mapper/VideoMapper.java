package org.designer.bilibili_1st.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class VideoMapper {
    private final JdbcTemplate jdbcTemplate;

    //上传视频，返回数据库中的视频ID（-1表示失败）
    public long uploadVideo(long userId, String title, String videoPath) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO video(user_id,title,video_path) VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, userId);
            ps.setString(2, title);
            ps.setString(3, videoPath);
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        return key == null ? -1 : key.longValue();
    }

    //显示视频标题列表
    public List<Map<String, Object>> selectVideoTitleList() {
        return jdbcTemplate.queryForList("SELECT id,title FROM video");
    }

    //带UP主昵称的视频列表
    private static final String SELECT_WITH_UPLOADER = """
            SELECT v.id, v.user_id, v.title, v.video_path, v.create_time, v.view_count,
                   (SELECT COUNT(*) FROM video_like vl WHERE vl.video_id = v.id) AS like_count,
                   u.virtualName AS uploader, u.avatar AS uploader_avatar
            FROM video v
            LEFT JOIN bilibili_user_log_in u ON v.user_id = u.id
            """;

    //显示全部视频
    public List<Map<String, Object>> selectAllVideo() {
        return jdbcTemplate.queryForList(SELECT_WITH_UPLOADER);
    }

    //查看某个人的视频
    public List<Map<String, Object>> selectVideoByUserId(long userId) {
        return jdbcTemplate.queryForList(SELECT_WITH_UPLOADER + " WHERE v.user_id=?", userId);
    }

    //按id查询视频
    public List<Map<String, Object>> selectVideoById(long id) {
        return jdbcTemplate.queryForList(SELECT_WITH_UPLOADER + " WHERE v.id=?", id);
    }

    //按标题模糊搜索视频
    public List<Map<String, Object>> searchVideo(String keyword) {
        return jdbcTemplate.queryForList(
                SELECT_WITH_UPLOADER + " WHERE v.title LIKE ? ORDER BY v.create_time DESC",
                "%" + keyword + "%");
    }

    //查询视频存储路径（用于播放、删除）
    public List<Map<String, Object>> selectVideoPathById(long id) {
        return jdbcTemplate.queryForList("SELECT video_path FROM video WHERE id=?", id);
    }

    //修改已上传视频的标题（只能改自己的）
    public int editVideoTitle(long userId, long videoId, String title) {
        return jdbcTemplate.update(
                "UPDATE video SET title=? WHERE id=? AND user_id=?",
                title, videoId, userId);
    }

    //删除我自己的视频
    public int deleteMyVideo(long userId, long videoId) {
        return jdbcTemplate.update(
                "DELETE FROM video WHERE id=? AND user_id=?",
                videoId, userId);
    }

    //写视频评论
    public int writeVideoComment(long videoId, long userId, String contents) {
        return jdbcTemplate.update(
                "INSERT INTO video_comment(video_id,user_id,contents) VALUES (?,?,?)",
                videoId, userId, contents);
    }

    //查看某个视频的评论（带上评论人昵称）
    public List<Map<String, Object>> selectCommentsByVideoId(long videoId) {
        return jdbcTemplate.queryForList("""
                SELECT c.id, c.video_id, c.user_id, c.contents, c.create_time,
                       u.virtualName AS name
                FROM video_comment c
                LEFT JOIN bilibili_user_log_in u ON c.user_id = u.id
                WHERE c.video_id=? ORDER BY c.create_time DESC
                """, videoId);
    }
    //搜索用户（只返回安全字段，模糊匹配昵称）
    public List<Map<String,Object>> findUserByVirtualName(String virtualName){
        return jdbcTemplate.queryForList(
                "SELECT id, virtualName FROM bilibili_user_log_in WHERE virtualName LIKE ? ORDER BY id",
                "%" + virtualName + "%");
    }

    //播放量 +1
    public int incrementView(long videoId) {
        return jdbcTemplate.update("UPDATE video SET view_count=view_count+1 WHERE id=?", videoId);
    }

    //点赞/取消点赞；返回是否已点赞
    public boolean toggleLike(long videoId, long userId) {
        List<Map<String, Object>> exits = jdbcTemplate.queryForList(
                "SELECT 1 FROM video_like WHERE video_id=? AND user_id=?", videoId, userId);
        if (exits.isEmpty()) {
            jdbcTemplate.update("INSERT INTO video_like(video_id,user_id) VALUES (?,?)", videoId, userId);
            return true;
        } else {
            jdbcTemplate.update("DELETE FROM video_like WHERE video_id=? AND user_id=?", videoId, userId);
            return false;
        }
    }

    //当前用户是否已点赞
    public boolean isLiked(long videoId, long userId) {
        return !jdbcTemplate.queryForList(
                "SELECT 1 FROM video_like WHERE video_id=? AND user_id=?",
                videoId, userId).isEmpty();
    }

    //删除某视频的全部点赞（配合删除视频，避免外键冲突）
    public int deleteLikesByVideo(long videoId) {
        return jdbcTemplate.update("DELETE FROM video_like WHERE video_id=?", videoId);
    }

    //删除某视频的全部评论（配合删除视频，避免外键冲突）
    public int deleteCommentsByVideo(long videoId) {
        return jdbcTemplate.update("DELETE FROM video_comment WHERE video_id=?", videoId);
    }

    //视频获赞总数
    public long countLikes(long videoId) {
        Long n = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM video_like WHERE video_id=?", Long.class, videoId);
        return n == null ? 0 : n;
    }
}