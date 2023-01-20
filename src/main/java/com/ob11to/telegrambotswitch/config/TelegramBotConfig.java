package com.ob11to.telegrambotswitch.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "telegram")
public class TelegramBotConfig {

    private String botUsername;
    private String botToken;
    private String botPath;

}
