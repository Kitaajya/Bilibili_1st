package org.designer.bilibili_1st.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface FollowMapper {

    //关注
    @Insert("INSERT INTO follow(follower_id, following_id) VALUES(#{followerId}, #{followingId})")
    int follow(@Param("followerId") long followerId, @Param("followingId") long followingId);

    //取关
    @Delete("DELETE FROM follow WHERE follower_id=#{followerId} AND following_id=#{followingId}")
    int unfollow(@Param("followerId") long followerId, @Param("followingId") long followingId);

    //是否已关注
    @Select("SELECT COUNT(*) FROM follow WHERE follower_id=#{followerId} AND following_id=#{followingId}")
    int isFollowing(@Param("followerId") long followerId, @Param("followingId") long followingId);

    //关注数
    @Select("SELECT COUNT(*) FROM follow WHERE follower_id=#{userId}")
    int countFollowing(@Param("userId") long userId);

    //粉丝数
    @Select("SELECT COUNT(*) FROM follow WHERE following_id=#{userId}")
    int countFollowers(@Param("userId") long userId);

    //我关注了谁
    @Select("SELECT u.id, u.virtualName, u.avatar FROM follow f" +
            " JOIN bilibili_user_log_in u " +
            "ON f.following_id = u.id WHERE f.follower_id = #{userId}" +
            " ORDER BY f.create_time DESC")
    List<Map<String, Object>> selectFollowing(@Param("userId") long userId);

    //谁关注了我
    @Select("SELECT u.id, u.virtualName, u.avatar " +
            "FROM follow f " +
            "JOIN bilibili_user_log_in u " +
            "ON f.follower_id = u.id WHERE f.following_id = #{userId} ORDER BY f.create_time DESC")
    List<Map<String, Object>> selectFollowers(@Param("userId") long userId);
}