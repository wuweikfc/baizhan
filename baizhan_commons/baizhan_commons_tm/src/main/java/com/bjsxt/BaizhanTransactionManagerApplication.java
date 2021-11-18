package com.bjsxt;

import com.codingapi.txlcn.tm.config.EnableTransactionManagerServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableTransactionManagerServer
public class BaizhanTransactionManagerApplication {

    public static void main(String[] args) {

        SpringApplication.run(BaizhanTransactionManagerApplication.class, args);
    }

}
