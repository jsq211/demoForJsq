package com.jsq.demo;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@MapperScan("com.jsq.demo.dao")
@SpringBootApplication
public class DemoApplication {
    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void main(String[] args) {
        DemoApplication.applicationContext = SpringApplication.run(DemoApplication.class, args);
    }

}
