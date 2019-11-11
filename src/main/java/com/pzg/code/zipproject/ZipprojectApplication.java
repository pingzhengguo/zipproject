package com.pzg.code.zipproject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.pzg.code.zipproject.mapper")
public class ZipprojectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZipprojectApplication.class, args);
    }

}
