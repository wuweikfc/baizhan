package com.bjsxt.app;

import com.codingapi.txlcn.tc.config.EnableDistributedTransaction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDistributedTransaction
public class BackendItemApplication {

    public static void main(String[] args) {

        SpringApplication.run(BackendItemApplication.class, args);
    }

}
