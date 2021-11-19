package com.bjsxt.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaginationConfig {

    /**
     * 设置MyBatis-Plus分页插件
     * 在Spring容器中，提供一个拦截器对象。可以实现动态分页效果。
     * @return
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {

        return new PaginationInterceptor();
    }

}
