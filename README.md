# Bilibili_1st

> 一个基于 Spring Boot 的 B 站仿站后端练习项目，实现了视频、动态、评论、点赞、商城等核心功能，并配有原生 HTML/CSS/JS 前端。
> 注：此说明书是deepseek写的，有什么不对的地方可以直接向我询问。

![Java](https://img.shields.io/badge/Java-17+-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-brightgreen)
![MySQL](https://img.shields.io/badge/MySQL-8.4-blue)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

---

## 📖 项目简介

`Bilibili_1st` 是一个仿哔哩哔哩的 Web 后端项目，目标是**通过完整实现一个视频社区的业务闭环来练习 Spring Boot 企业级开发**。

项目采用经典的 **Controller → Service → Mapper → Entity** 四层架构，使用 `JdbcTemplate` 操作数据库，前端为免构建的原生三件套（HTML + CSS + JS），开箱即用。

### 核心特性

- 🎬 **视频系统**：上传、播放（支持 HTTP Range 拖动进度条）、搜索、标题修改、删除
- ❤️ **互动系统**：点赞/取消点赞、评论、播放量统计
- 💬 **动态系统**：发布、编辑、删除个人动态
- 🛒 **商城系统**：商品增删改查、按价格筛选、订单与评价
- 👤 **用户系统**：注册 / 登录（BCrypt 密码加密）、个人主页
- 🧑‍💻 **游客模式**：未登录可浏览视频，但点赞、评论等操作需登录
- 📦 **统一响应体**：所有接口返回标准化 `Result<T>` 结构
- ⚠️ **全局异常处理**：统一捕获异常，避免堆栈外泄

---

## 🛠 技术栈

| 分类 | 技术 |
|---|---|
| 语言 | Java 17+ |
| 框架 | Spring Boot 4.1.1、Spring MVC |
| 数据访问 | Spring JDBC (`JdbcTemplate`)、Hibernate (JPA) |
| 数据库 | MySQL 8.4 |
| 密码加密 | Spring Security Crypto (BCrypt) |
| 工具库 | Lombok |
| 构建 | Maven |
| 前端 | 原生 HTML / CSS / JavaScript |

---

## 📁 项目结构

```
Bilibili_1st
├── db/
│   └── init.sql                    # 数据库初始化脚本
├── src/main/java/org/designer/bilibili_1st/
│   ├── Bilibili1stApplication.java # 启动类
│   ├── common/                     # 通用组件
│   │   ├── Result.java             # 统一响应体
│   │   ├── ResultMapper.java       # Map -> Result 适配器
│   │   ├── BusinessException.java  # 业务异常
│   │   └── GlobalExceptionHandler.java
│   ├── controller/                 # 表现层
│   │   ├── LogInController.java
│   │   ├── UserController.java
│   │   ├── VideoController.java
│   │   ├── DynamicController.java
│   │   └── PurchaseController.java
│   ├── service/                    # 业务层
│   ├── mapper/                     # 数据访问层 (JdbcTemplate)
│   └── entity/                     # 实体类
└── src/main/resources/
    ├── application.properties      # 配置文件
    └── static/                     # 前端静态资源
        ├── index.html
        ├── css/style.css
        └── js/app.js
```

---

## 🚀 快速开始

### 环境要求

- JDK 17 或更高（推荐 21 / 24）
- Maven 3.6+
- MySQL 8.0+

### 1. 克隆项目

```bash
git clone https://github.com/<your-username>/Bilibili_1st.git
cd Bilibili_1st
```

### 2. 初始化数据库

```bash
mysql -u root -p < db/init.sql
```

该脚本会创建：
- 数据库 `bilibili_database`
- 用户 `bilibili_sql`（密码 `123456`）
- 所有业务表（用户、视频、点赞、评论、动态、商城等）

### 3. 修改配置

编辑 `src/main/resources/application.properties`：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bilibili_database?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8
spring.datasource.username=bilibili_sql
spring.datasource.password=123456

# 视频上传目录（按需修改）
file.upload.path=D:/videos/Video_Bilibili_Practice/videos/
```

### 4. 启动项目

```bash
# Windows
mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

启动成功后访问：**http://localhost:8080**

---

## 🔌 接口概览

所有接口统一返回如下结构：

```json
{
  "code": 200,
  "success": true,
  "message": "操作成功",
  "data": { }
}
```

### 登录 / 用户

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/log/in/register` | 用户注册 |
| POST | `/api/log/in/login` | 用户登录 |
| GET | `/api/user/profile?userId=` | 用户主页信息 |

### 视频

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/video/upload` | 上传视频 |
| GET | `/api/video/all` | 全部视频 |
| GET | `/api/video/byUser?userId=` | 某用户的视频 |
| GET | `/api/video/search?keyword=` | 搜索视频 |
| GET | `/api/video/play?id=` | 播放视频（支持 Range） |
| POST | `/api/video/edit/title` | 修改标题 |
| DELETE | `/api/video/delete` | 删除视频 |
| POST | `/api/video/like/toggle` | 点赞 / 取消点赞 |
| POST | `/api/video/comment/write` | 发表评论 |
| GET | `/api/video/comment/list?videoId=` | 查看评论 |

### 动态 / 商城

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/dynamic/all` | 全部动态 |
| POST | `/api/dynamic/write` | 发布动态 |
| GET | `/api/purchase/controller/select/all/product` | 商品列表 |
| POST | `/api/purchase/controller/add/product` | 添加商品 |

> 更多接口请查看 `controller/` 目录下的源码。

---

## 🗄 数据库设计

| 表名 | 说明 |
|---|---|
| `bilibili_user_log_in` | 用户表（含角色：USER / MERCHANT） |
| `video` | 视频表 |
| `video_like` | 视频点赞（联合主键防重复） |
| `video_comment` | 视频评论 |
| `dynamic` | 用户动态 |
| `bilibili_purchase` | 商城订单 |

表间通过外键关联，删除视频时会级联清理点赞与评论。

---

## 🎯 核心实现亮点

- **HTTP Range 视频流**：`VideoController.play()` 手动解析 `Range` 请求头，通过 `RandomAccessFile` 定位读取，支持拖动进度条、快进快退。
- **越权防护**：修改 / 删除视频的 SQL 均带 `AND user_id = ?`，用户只能操作自己的资源。
- **密码安全**：使用 `BCryptPasswordEncoder` 加密存储，绝不存明文。
- **SQL 注入防护**：全部使用 `?` 占位符参数化查询。

---

## 🔮 后续规划

- [ ] JWT / Session 鉴权（当前 userId 由前端传入，存在伪造风险）
- [ ] 用户头像上传
- [ ] 收藏夹 / 关注 UP 主
- [ ] 评论二级回复
- [ ] 视频弹幕（WebSocket）
- [ ] 列表分页与懒加载
- [ ] 视频上传自动压缩（ffmpeg）

---

## ⚠️ 注意事项

- 本项目为**学习练习项目**，默认配置中的数据库密码为弱密码，**请勿直接用于生产环境**。
- 生产部署前请务必将密码改为环境变量或外部配置。
- 视频上传目录需确保有写权限。

---

## 📄 License

本项目基于 [MIT License](LICENSE) 开源，仅供学习交流使用。

---

## 🙏 致谢

感谢 B 站的产品设计带来的灵感，以及 Spring 社区的优秀开源生态。

> 本项目为个人学习作品，与哔哩哔哩官方无任何关联。
