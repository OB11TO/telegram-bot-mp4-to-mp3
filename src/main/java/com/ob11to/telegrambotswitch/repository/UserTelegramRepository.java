package com.ob11to.telegrambotswitch.repository;

import com.ob11to.telegrambotswitch.entity.TelegramBotState;
import com.ob11to.telegrambotswitch.entity.UserTelegram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTelegramRepository extends JpaRepository<UserTelegram, Long> {

    Optional<UserTelegram> findByChatId(Long chatId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update UserTelegram u " +
            "set u.botState = :ready " +
            "where u.chatId = :chatId"
    )
    void changeBotStateByChatId(Long chatId, TelegramBotState ready);
}
