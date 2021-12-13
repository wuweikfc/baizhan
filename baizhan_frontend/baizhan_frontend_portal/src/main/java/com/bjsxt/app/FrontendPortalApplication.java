package com.bjsxt.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FrontendPortalApplication {

    public static void main(String[] args) {

        SpringApplication.run(FrontendPortalApplication.class, args);
    }

}
