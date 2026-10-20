package com.seecen.mall.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 *
 * 把磁盘上的上传目录映射成 HTTP 静态资源，让数据库里保存的
 * 相对 URL（以 /api/uploads/images/ 开头）能直接被浏览器访问。
 *
 * 默认 upload.path = D:\\upload\\
 * 访问示例：GET /api/uploads/images/2026/09/02/product-1.svg
 *           实际返回：D:\\upload\\images\\2026\\09\\02\\product-1.svg
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 上传目录根路径，对应 application.yml 中的 upload.path
     */
    @Value("${upload.path}")
    private String uploadPath;

    /**
     * 把 /api/uploads/images/** 映射到 upload.path + "images/" 目录。
     * 注意 Windows 路径要用 file:D:/upload/images/ 这种斜杠写法，
     * 否则 Resource 解析会报 "file is not a directory"。
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = "file:" + uploadPath.replace("\\\\", "/") + "images/";
        registry.addResourceHandler("/api/uploads/images/**")
                .addResourceLocations(location);
    }
}

