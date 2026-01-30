package com.pcbuilder.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PcbuilderApplication {

    public static void main(String[] args) {
        SpringApplication.run(PcbuilderApplication.class, args);
    }

}
