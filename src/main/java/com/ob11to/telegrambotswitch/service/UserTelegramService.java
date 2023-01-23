package com.ob11to.telegrambotswitch.service;

import com.ob11to.telegrambotswitch.dto.UserTelegramCreateDto;
import com.ob11to.telegrambotswitch.dto.UserTelegramReadDto;
import com.ob11to.telegrambotswitch.entity.TelegramBotState;
import com.ob11to.telegrambotswitch.mapper.UserTelegramCreateMapper;
import com.ob11to.telegrambotswitch.mapper.UserTelegramReadMapper;
import com.ob11to.telegrambotswitch.repository.UserTelegramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserTelegramService {

    private final UserTelegramRepository userTelegramRepository;
    private final UserTelegramReadMapper userTelegramMapper;
    private final UserTelegramCreateMapper userTelegramCreateMapper;

    public Optional<UserTelegramReadDto> getUserBotChatId(Long chatId) {
        return userTelegramRepository.findByChatId(chatId)
                .map(userTelegramMapper::map);
    }

    @Transactional
    public void createUserTelegram(Long chatId, String userName) {
        UserTelegramCreateDto newUser = new UserTelegramCreateDto(chatId, userName, TelegramBotState.READY);

        var userTelegramReadDto = Optional.of(newUser)
                .map(userTelegramCreateMapper::map)
                .map(userTelegramRepository::save)
                .map(userTelegramMapper::map)
                .orElseThrow();
    }
}

