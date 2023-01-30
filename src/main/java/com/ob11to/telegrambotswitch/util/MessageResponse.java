package com.ob11to.telegrambotswitch.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageResponse {
    public static final String START = """
            Меня зовут Гриф 🔮
            Моя цель - упростить твою жизнь и позволить тебе скачивать youtube-видео или извлекать из него звуковую дорожку.
            От тебя потребуется только ссылка на видео, которое ты хочешь скачать 🤳
                        
            Нажми '/info' 👈 для получения полного списка команд""";
    public static final String INFO = """
            Я умею скачивать видео с YouTube в удобном для тебя формате 🎑
            Тебе нужно только скинуть ссылку на видео и выбрать интересующий тебя вариант:
            mp3 -> Пришлю тебе звуковую дорожку 🎼
            mp4 -> Скину само видео 📽
            Ты также можешь выбрать качество видео:
            -> 360
            -> 720
                        
            Если ожидание файла затянулось или ты передумал,
            всегда можно остановить процесс командой '/stop' 🛑
                        
            Приятного пользования ✨
            """;
    public static final String WAIT = """
            Идёт обработка запроса, подожди ещё немного ⏳
            Или нажми '/stop' 👈 если больше не хочешь ждать 🦿""";
    public static final String CREATE = "Мы не знакомы, но скоро я узнаю о тебе 👁";
    public static final String STOP_DOWNLOAD = "Процесс остановлен 🎃";
    public static final String INFO_AFTER_STOP = "Можем попробовать еще раз, кидай ссылку на видео ✨";
    public static final String INFO_AGAIN = "Сказал же, что нужна корректная ссылка на youtube-видео 🎬";
    public static final String DONE = "Держи %s 🌚 Всё готово 💫";
    public static final String PREPARE_TO_LOAD = "Начинаю подготовку к скачиванию 🧳";
    public static final String BEGIN_LOADING = "Начал загрузку 🚀";
    public static final String INVALID_INPUT = "Что ты мне прислал? Ничего не разобрать 🤬";
    public static final String CHOSE_FORMAT = "Выбери предпочитаемый вариант. Будем слушать или смотреть? 🙃";
    public static final String CHOSE_QUALITY = "Какое хочешь качество? 🤔";
    public static final String FILE_FOUND = "Файл найден";
    public static final String CHOSE_ANOTHER_FORMAT = "Выбери другой формат 😒";
    public static final String FILE_IS_TOO_BIG = "Твой файл превышает допустимый размер, я его качать не буду 👿";


}
