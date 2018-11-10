package com.piotrek.diet;

import org.decimal4j.util.DoubleRounder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DietApplication {

    public static void main(String[] args) {
        SpringApplication.run(DietApplication.class, args);
    }

    @Bean
    public DoubleRounder doubleRounder() {
        return new DoubleRounder(2);
    }

}
