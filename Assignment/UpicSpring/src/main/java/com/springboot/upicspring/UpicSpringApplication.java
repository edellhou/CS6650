package com.springboot.upicspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping("/skiers")
public class UpicSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(UpicSpringApplication.class, args);
    }

}
