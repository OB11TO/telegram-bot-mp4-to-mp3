package com.ob11to.telegrambotswitch.telegram;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramFacade {

    public List<InlineKeyboardButton> getMediaFormats() {
        InlineKeyboardButton mp3Button = new InlineKeyboardButton();
        InlineKeyboardButton mp4Button = new InlineKeyboardButton();
        mp3Button.setText("mp3");
        mp3Button.setCallbackData("mp3");
        mp4Button.setText("mp4");
        mp4Button.setCallbackData("mp4");
        List<InlineKeyboardButton> buttonsRow = new ArrayList<>();
        buttonsRow.add(mp3Button);
        buttonsRow.add(mp4Button);
        return buttonsRow;
    }

    public InlineKeyboardMarkup createBlockButtons(List<InlineKeyboardButton> buttonsRow) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(buttonsRow);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }
}
