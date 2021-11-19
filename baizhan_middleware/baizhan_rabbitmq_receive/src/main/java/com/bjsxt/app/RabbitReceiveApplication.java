package com.bjsxt.app;

import com.codingapi.txlcn.tc.config.EnableDistributedTransaction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDistributedTransaction
@EnableFeignClients
public class RabbitReceiveApplication {

    public static void main(String[] args) {

        SpringApplication.run(RabbitReceiveApplication.class, args);
    }

}
