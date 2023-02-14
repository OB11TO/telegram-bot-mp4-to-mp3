package com.ob11to.telegrambotswitch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TelegramBotSwitchApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelegramBotSwitchApplication.class, args);
    }
}
