package com.seecen.mall.core;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@MapperScan({
        "com.seecen.mall.core.admin.mapper",
        "com.seecen.mall.core.auth.mapper",
        "com.seecen.mall.ai.mapper",
})
@SpringBootApplication(scanBasePackages = {
        "com.seecen.mall.core",
        "com.seecen.mall.common",
        "com.seecen.mall.ai",
})
public class MallCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallCoreApplication.class, args);
    }

}
