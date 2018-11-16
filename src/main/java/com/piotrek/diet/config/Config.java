package com.piotrek.diet.config;

import org.decimal4j.util.DoubleRounder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public DoubleRounder doubleRounder() {
        return new DoubleRounder(2);
    }
}
