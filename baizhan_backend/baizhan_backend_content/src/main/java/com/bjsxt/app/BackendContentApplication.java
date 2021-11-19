package com.bjsxt.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BackendContentApplication {

    public static void main(String[] args) {

        SpringApplication.run(BackendContentApplication.class, args);
    }

}
