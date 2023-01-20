package com.ob11to.telegrambotswitch.service;

import com.ob11to.telegrambotswitch.config.TelegramBotConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBotWebHookService extends TelegramWebhookBot {

    private static final String INFO = "INFO ?";
    private static final String WAIT = "WAIT ?";
    private static final String STOP_DOWNLOAD = "STOP_DOWNLOAD ?";
    private static final String INFO_AGAIN = "INFO_AGAIN ?";
    private static final String PREPARE_TO_LOAD = "PREPARE_TO_LOAD ?";
    private static final String INVALID_INPUT = "INVALID_INPUT ?";
    private static final String CHOSE_FORMAT = "CHOSE_FORMAT ?";
    private static final String BEGIN_LOADING = "BEGIN_LOADING ?";
    private static final String CHOSE_QUALITY = "CHOSE_QUALITY ?";
    private static final String FILE_FOUND = "FILE_FOUND ?";
    private static final String CHOSE_ANOTHER_FORMAT = "CHOSE_ANOTHER_FORMAT ?";
    private static final String FILE_IS_TOO_BIG = "FILE_IS_TOO_BIG ?";

    private final static double MAX_UPLOADED_FILE_SIZE = 50;
    private final static int MP4_360_QUALITY_CODE = 18;
    private final static int MP4_720_QUALITY_CODE = 22;
    private final static int NONE_QUALITY_CODE = 0;

    private static final File PATH = new File("/home/obiito/c/youtube/test");

    private final TelegramBotConfig config;

    @Override
    public String getBotUsername() {
        return config.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if(update.getMessage() != null && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            log.info("Process update from chatId: " + chatId);

        }
    }


    @Override
    public String getBotPath() {
        return config.getBotPath();
    }

}
