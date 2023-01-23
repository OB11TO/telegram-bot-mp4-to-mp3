package com.ob11to.telegrambotswitch.mapper;

import com.ob11to.telegrambotswitch.dto.UserTelegramReadDto;
import com.ob11to.telegrambotswitch.entity.UserTelegram;
import org.springframework.stereotype.Component;

@Component
public class UserTelegramReadMapper implements Mapper<UserTelegram, UserTelegramReadDto> {

    @Override
    public UserTelegramReadDto map(UserTelegram object) {
        return new UserTelegramReadDto(
                object.getId(),
                object.getChatId(),
                object.getUsername(),
                object.getBotState()
        );
    }
}
