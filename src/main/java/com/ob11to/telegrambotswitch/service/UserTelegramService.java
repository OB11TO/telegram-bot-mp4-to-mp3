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

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<UserTelegramReadDto> getAllUserReadyBot() {
        return userTelegramRepository.findAllUserReady().stream()
                .map(userTelegramMapper::map)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserTelegramReadDto createUserTelegram(Long chatId, String userName) {
        UserTelegramCreateDto newUser = new UserTelegramCreateDto(chatId, userName, TelegramBotState.READY);

        return Optional.of(newUser)
                .map(userTelegramCreateMapper::map)
                .map(userTelegramRepository::save)
                .map(userTelegramMapper::map)
                .orElseThrow();
    }

    @Transactional
    public void changeBotStateByChatId(Long chatId, TelegramBotState ready) {
        var newTime = Instant.now();
        userTelegramRepository.changeBotStateByChatId(chatId, ready, newTime);
    }
}

