package com.ob11to.telegrambotswitch.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class ReplyMessageService {

    public SendMessage getReplyMessage(Long chatId, String replyMessage) {
        var id = String.valueOf(chatId);
//        return new SendMessage(id, localeMessageService.getMessage(replyMessage));
        return new SendMessage(id, replyMessage);
    }
}
