package com.myproject.currencylog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CurrencyLogApplication {

    public static void main(String[] args) {
        SpringApplication.run(CurrencyLogApplication.class, args);
    }

}
