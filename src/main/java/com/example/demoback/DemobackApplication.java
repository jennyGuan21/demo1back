package com.example.demoback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class DemobackApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemobackApplication.class, args);
    }

}
