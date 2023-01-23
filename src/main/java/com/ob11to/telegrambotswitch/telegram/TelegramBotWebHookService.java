package com.ob11to.telegrambotswitch.telegram;

import com.ob11to.telegrambotswitch.config.TelegramBotConfig;
import com.ob11to.telegrambotswitch.dto.UserTelegramReadDto;
import com.ob11to.telegrambotswitch.service.ReplyMessageService;
import com.ob11to.telegrambotswitch.service.UserTelegramService;
import com.ob11to.telegrambotswitch.util.MessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBotWebHookService extends TelegramWebhookBot {

    private final static double MAX_UPLOADED_FILE_SIZE = 50;
    private final static int MP4_360_QUALITY_CODE = 18;
    private final static int MP4_720_QUALITY_CODE = 22;
    private final static int NONE_QUALITY_CODE = 0;

    private static final File PATH = new File("/home/obiito/c/youtube/test");

    private final TelegramBotConfig config;
    private final UserTelegramService userTelegramService;
    private final ReplyMessageService replyMessageService;

    @Override
    public String getBotUsername() {
        return config.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public String getBotPath() {
        return config.getBotPath();
    }

    @SneakyThrows
    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (update.getMessage() != null && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            log.info("Process update from chatId: " + chatId);
            Optional<UserTelegramReadDto> findUserTelegram = userTelegramService.getUserBotChatId(chatId);
            if (findUserTelegram.isPresent()) {
                UserTelegramReadDto userTelegram = findUserTelegram.get();
                processUpdate(userTelegram, update.getMessage());
            } else {
                try {
                    var userTelegram = userTelegramService.createUserTelegram(chatId, update.getMessage().getChat().getUserName());
                    log.info("Create user with chatId:" + userTelegram.getChatId());
                    execute(replyMessageService.getReplyMessage(userTelegram.getChatId(), MessageResponse.INFO));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                    log.error("Error while executing message ", e);
                }
            }
        }
        return null;
    }

    private void processUpdate(UserTelegramReadDto userTelegram, Message message) throws TelegramApiException {
        if (message.getText().equals("/start") || message.getText().equals("/info")) {
            execute(replyMessageService.getReplyMessage(userTelegram.getChatId(), MessageResponse.INFO));
        } else {
            execute(replyMessageService.getReplyMessage(userTelegram.getChatId(), MessageResponse.INVALID_INPUT));
        }


    }
}
