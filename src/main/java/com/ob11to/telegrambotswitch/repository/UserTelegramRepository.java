package com.ob11to.telegrambotswitch.repository;

import com.ob11to.telegrambotswitch.entity.UserTelegram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTelegramRepository extends JpaRepository<UserTelegram, Long> {

    Optional<UserTelegram> findByChatId(Long chatId);
}
