package org.designer.bilibili_1st.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    //用户主页信息：昵称、身份、投稿数、总播放、总获赞
    @Select("""
            SELECT u.id, u.virtualName, u.role,
                   (SELECT COUNT(*) FROM video v WHERE v.user_id = u.id) AS videoCount,
                   (SELECT COALESCE(SUM(v.view_count),0) FROM video v WHERE v.user_id = u.id) AS totalView,
                   (SELECT COUNT(*) FROM video v
                     JOIN video_like vl ON vl.video_id = v.id
                     WHERE v.user_id = u.id) AS totalLike
            FROM bilibili_user_log_in u
            WHERE u.id = #{userId}
            """)
    List<Map<String, Object>> selectUserProfile(@Param("userId") long userId);

    //更新头像文件名
    @Update("UPDATE bilibili_user_log_in SET avatar = #{avatar} WHERE id = #{userId}")
    int updateAvatar(@Param("userId") long userId, @Param("avatar") String avatar);

    //查看头像
    @Select("SELECT avatar FROM bilibili_user_log_in WHERE id=#{userId}")
    String selectAvatar(@Param("userId") long userId);
}
