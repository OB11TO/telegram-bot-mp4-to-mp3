package com.ob11to.telegrambotswitch.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.telegram.telegrambots.meta.api.methods.ParseMode.MARKDOWN;
import static org.telegram.telegrambots.meta.api.methods.ParseMode.MARKDOWNV2;

@Service
public class ReplyMessageService {

    public SendMessage getReplyMessage(Long chatId, String replyMessage) {
        var id = String.valueOf(chatId);
//        return new SendMessage(id, localeMessageService.getMessage(replyMessage));

        return SendMessage.builder()
                .chatId(chatId)
                .text(replyMessage)
                .parseMode(MARKDOWNV2)
                .build();
    }

    public SendMessage getReplyMessageUsername(Long chatId, String replyMessage) {
        var id = String.valueOf(chatId);
//        return new SendMessage(id, localeMessageService.getMessage(replyMessage));

        return SendMessage.builder()
                .chatId(chatId)
                .text(replyMessage)
                .parseMode(MARKDOWN)
                .build();
    }

    public SendMessage getReplyText(Long chatId, String replyMessage) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(replyMessage)
                .parseMode(MARKDOWN)
                .build();
    }
}
