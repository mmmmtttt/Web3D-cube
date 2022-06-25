package com.company.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement //打开事务管理
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

