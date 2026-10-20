package com.seecen.mall.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 基础配置
 *
 * 本类集中配置：
 *   - MySQL 分页拦截器
 *   - 创建时间 / 更新时间自动填充（MetaObjectHandler）
 *
 * 供后续所有业务表复用。
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * MySQL 分页拦截器
     *
     * 启用后，MyBatis-Plus 的 selectPage 才会真正返回 Page 对象。
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * 通用字段自动填充处理器
     *
     * 实体类中标注了 @TableField(fill = FieldFill.INSERT) 或
     * FieldFill.INSERT_UPDATE 的字段，会在这里自动写入。
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                LocalDateTime now = LocalDateTime.now();
                this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
                this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
            }
        };
    }
}
