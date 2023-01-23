package com.ob11to.telegrambotswitch.mapper;

import com.ob11to.telegrambotswitch.dto.UserTelegramCreateDto;
import com.ob11to.telegrambotswitch.entity.UserTelegram;
import org.springframework.stereotype.Component;

@Component
public class UserTelegramCreateMapper implements Mapper<UserTelegramCreateDto, UserTelegram> {


    @Override
    public UserTelegram map(UserTelegramCreateDto object) {
        return UserTelegram.builder()
                .chatId(object.getChatId())
                .username(object.getUsername())
                .botState(object.getState())
                .build();
    }
}
