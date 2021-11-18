package com.bjsxt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 2592000)
public class SessionConfig {

    @Bean
    public HeaderHttpSessionIdResolver headerHttpSessionIdResolver() {

        return new HeaderHttpSessionIdResolver("Token");
    }

}
