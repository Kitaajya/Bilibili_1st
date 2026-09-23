CREATE DATABASE IF NOT EXISTS bilibili_database;

USE bilibili_database;
CREATE USER IF NOT EXISTS 'bilibili_sql'@'localhost' IDENTIFIED BY '123456';
GRANT ALL PRIVILEGES ON bilibili_database.* TO 'bilibili_sql'@'localhost';
FLUSH PRIVILEGES;

CREATE TABLE IF NOT EXISTS bilibili_user_log_in(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    password     varchar(60),            -- NULL 表示未设密码
    realName     varchar(5)  NOT NULL,
    virtualName  varchar(14) NOT NULL,
    qq_email     varchar(100) NOT NULL UNIQUE,
    phoneNumber  varchar(11) UNIQUE,     -- 可空，但不可重复
    role         varchar(20) NOT NULL DEFAULT 'USER',  -- USER 普通用户 / MERCHANT 商家
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 购物系统
CREATE TABLE IF NOT EXISTS bilibili_purchase(
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    contents text,            -- 用户评价
    quantity INT DEFAULT 1,
    total DECIMAL(10,2) NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES bilibili_user_log_in(id)
);

-- 动态
CREATE TABLE IF NOT EXISTS dynamic(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    title       varchar(20),
    contents    text,                                  -- 动态内容
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,    -- 发帖时间
    FOREIGN KEY (user_id) REFERENCES bilibili_user_log_in(id)
);

-- 视频（供 VideoMapper 使用）
CREATE TABLE IF NOT EXISTS video(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    title       varchar(200) NOT NULL,
    video_path  varchar(500),
    view_count  INT NOT NULL DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES bilibili_user_log_in(id)
);

-- 视频点赞
CREATE TABLE IF NOT EXISTS video_like(
    user_id     BIGINT NOT NULL,
    video_id    BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, video_id),
    FOREIGN KEY (video_id) REFERENCES video(id),
    FOREIGN KEY (user_id) REFERENCES bilibili_user_log_in(id)
);

-- 视频评论
CREATE TABLE IF NOT EXISTS video_comment(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    video_id    BIGINT NOT NULL,
    user_id     BIGINT NOT NULL,
    contents    TEXT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (video_id) REFERENCES video(id),
    FOREIGN KEY (user_id) REFERENCES bilibili_user_log_in(id)
);

-- 动态评论（parent_id 为 NULL 表示顶级评论，否则是回复某条评论）
CREATE TABLE IF NOT EXISTS dynamic_comment(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    dynamic_id  BIGINT NOT NULL,
    user_id     BIGINT NOT NULL,
    parent_id   BIGINT DEFAULT NULL,
    contents    TEXT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (dynamic_id) REFERENCES dynamic(id),
    FOREIGN KEY (user_id) REFERENCES bilibili_user_log_in(id)
);
SELECT * FROM bilibili_user_log_in;
SELECT v.*,
       (SELECT COUNT(*) FROM video_like vl WHERE vl.video_id = v.id) AS like_count
FROM video v
ORDER BY like_count DESC;
-- 关注关系
CREATE TABLE IF NOT EXISTS follow(
    follower_id  BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    create_time  DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (follower_id, following_id),
    FOREIGN KEY (follower_id)  REFERENCES bilibili_user_log_in(id),
    FOREIGN KEY (following_id) REFERENCES bilibili_user_log_in(id)
);
-- 观看历史
CREATE TABLE IF NOT EXISTS video_history(
    user_id        BIGINT   NOT NULL,
    video_id       BIGINT   NOT NULL,
    progress       INT      NOT NULL DEFAULT 0,   -- 看到第几秒，做继续观看
    last_view_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,  -- 重复观看自动刷新时间
    PRIMARY KEY (user_id, video_id),
    INDEX idx_user_time (user_id, last_view_time DESC),
    FOREIGN KEY (user_id)  REFERENCES bilibili_user_log_in(id) ON DELETE CASCADE,
    FOREIGN KEY (video_id) REFERENCES video(id) ON DELETE CASCADE
);