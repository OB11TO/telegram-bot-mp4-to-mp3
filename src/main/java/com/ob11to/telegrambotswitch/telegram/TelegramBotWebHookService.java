package com.ob11to.telegrambotswitch.telegram;

import com.ob11to.telegrambotswitch.config.TelegramBotConfig;
import com.ob11to.telegrambotswitch.dto.Request;
import com.ob11to.telegrambotswitch.dto.UserTelegramReadDto;
import com.ob11to.telegrambotswitch.service.ReplyMessageService;
import com.ob11to.telegrambotswitch.service.UserInputParser;
import com.ob11to.telegrambotswitch.service.UserTelegramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.Optional;

import static com.ob11to.telegrambotswitch.entity.TelegramBotState.BUSY;
import static com.ob11to.telegrambotswitch.entity.TelegramBotState.READY;
import static com.ob11to.telegrambotswitch.util.MessageResponse.CHOSE_FORMAT;
import static com.ob11to.telegrambotswitch.util.MessageResponse.FILE_FOUND;
import static com.ob11to.telegrambotswitch.util.MessageResponse.INFO;
import static com.ob11to.telegrambotswitch.util.MessageResponse.INFO_AGAIN;
import static com.ob11to.telegrambotswitch.util.MessageResponse.INVALID_INPUT;
import static com.ob11to.telegrambotswitch.util.MessageResponse.STOP_DOWNLOAD;
import static com.ob11to.telegrambotswitch.util.MessageResponse.WAIT;

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
    private final UserInputParser userInputParser;
    private final TelegramFacade telegramFacade;

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
                    var userTelegram =
                            userTelegramService.createUserTelegram(chatId, update.getMessage().getChat().getUserName());
                    log.info("Create user with chatId:" + userTelegram.getChatId());
                    execute(replyMessageService.getReplyMessage(userTelegram.getChatId(), INFO));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                    log.error("Error while executing message ", e);
                }
            }
        }
        return null;
    }

    private void processUpdate(UserTelegramReadDto userTelegram, Message message) {
        try {
            if (message.getText().equals("/start") || message.getText().equals("/info")) {
                execute(replyMessageService.getReplyMessage(userTelegram.getChatId(), INFO));
            } else if (userTelegram.getState() == READY && !message.getText().isEmpty()) {
                botIsReadyToProcessUrl(userTelegram.getChatId(), message);
            } else if (userTelegram.getState() == BUSY && message.getText().equals("/stop")) {
                execute(replyMessageService.getReplyMessage(userTelegram.getChatId(), STOP_DOWNLOAD));
                execute(replyMessageService.getReplyMessage(userTelegram.getChatId(), INFO_AGAIN));
                userTelegramService.changeBotStateByChatId(userTelegram.getChatId(), READY);
                log.info("Change bot state to READY for chatId: " + userTelegram.getChatId());

                //TODO: stop download

            } else if (userTelegram.getState() == BUSY) {
                execute(replyMessageService.getReplyMessage(userTelegram.getChatId(), WAIT));
            }

        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }

    private void botIsReadyToProcessUrl(Long chatId, Message message) throws TelegramApiException {
        userTelegramService.changeBotStateByChatId(chatId, BUSY);
        log.info("Change bot state to BUSY for chatId: " + chatId);

        String videoId = userInputParser.getYouTubeVideoId(message.getText());

        if(videoId == null) {
            execute(replyMessageService.getReplyMessage(chatId, INVALID_INPUT));
            execute(replyMessageService.getReplyMessage(chatId, INFO_AGAIN));
            log.info("Invalid url from user, input: " + message.getText());
            userTelegramService.changeBotStateByChatId(chatId, READY);
            log.info("Change bot state to READY for chatId: " + chatId);
        } else {
            execute(replyMessageService.getReplyMessage(chatId, FILE_FOUND));
            Request userRequest = new Request();
            userRequest.setVideoId(videoId);
            // TODO: storage.updateRequest(chatId, userRequest);
            log.info("Put request to map with chatId: " + chatId + " videoId: " + videoId);
            SendMessage choseFormatMessage = new SendMessage(chatId.toString(), replyMessageService.getReplyText(CHOSE_FORMAT));
            choseFormatMessage.setReplyMarkup(telegramFacade.createBlockButtons(telegramFacade.getMediaFormats()));
            execute(choseFormatMessage);
        }
    }




}
