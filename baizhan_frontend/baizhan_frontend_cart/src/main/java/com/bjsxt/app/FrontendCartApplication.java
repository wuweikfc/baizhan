package com.bjsxt.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FrontendCartApplication {

    public static void main(String[] args) {

        SpringApplication.run(FrontendCartApplication.class, args);
    }

}

