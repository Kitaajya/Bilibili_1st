package org.designer.bilibili_1st;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "org.designer.bilibili_1st.mapper",
            annotationClass = Mapper.class)
public class Bilibili1stApplication {
    static Logger log = LoggerFactory.getLogger(Bilibili1stApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(Bilibili1stApplication.class, args);
        log.info("测试代码");
    }
}
