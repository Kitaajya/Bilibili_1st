package org.designer.bilibili_1st;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Bilibili1stApplication {
    /**
     模板：
     com.example.myproject          # 根包（通常使用反向域名）
     ├── Application.java           # 主启动类，放在根包下
     ├── controller/                # 表现层，处理 HTTP 请求 [citation:3][citation:7]
     │   └── UserController.java
     ├── service/                   # 业务逻辑层 [citation:3][citation:7]
     │   ├── UserService.java       # 业务接口
     │   └── impl/
     │       └── UserServiceImpl.java # 业务实现
     ├── mapper/或dao/              # 数据访问层 [citation:3][citation:7]
     │   └── UserMapper.java
     └── entity/或domain/           # 实体类，通常与数据库表对应 [citation:3]
         └── User.java
     * **/
    static Logger log= LoggerFactory.getLogger(Bilibili1stApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(Bilibili1stApplication.class, args);
        log.info("测试代码");
    }

}
