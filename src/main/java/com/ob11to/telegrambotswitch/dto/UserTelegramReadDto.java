package com.ob11to.telegrambotswitch.dto;

import com.ob11to.telegrambotswitch.entity.TelegramBotState;
import lombok.Value;

@Value
public class UserTelegramReadDto {
    Long id;
    Long chatId;
    String username;
    TelegramBotState state;
}
