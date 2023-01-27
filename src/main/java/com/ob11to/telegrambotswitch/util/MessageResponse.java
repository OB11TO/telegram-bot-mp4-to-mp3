package com.ob11to.telegrambotswitch.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageResponse {
    public static final String START = """
            Меня зовут Гриф🔮
            Моя цель - сделать тебе жизнь проще♻
            От тебя потребуется всего лишь ссылка на видео с YouTube, которое ты хочешь прослушать или просмотреть🤳
            Всё просто🙌
                        
            Для списка всех команд, которые я умею нажми на '/info'👈""";
    public static final String INFO = """
            Итак, я умею скачивать видео с YouTube в удобный для тебя формат🎑
            Тебе нужно всего лишь скинуть ссылку на видео🎬
                        
            Выбрать вариант, который хочешь:
            mp3 -> Пришлю тебе звуковую дорожку🎼
            mp4 -> Скину сам видос📽
            (Даже можешь выбрать качество - 360 или 720)
                        
            Если не сможешь долго ждать или передумал,
            то ты всегда можешь остановить процесс нажав или написав мне '/stop'🦂
                        
            Удачи✨
            """;
    public static final String WAIT = """
            Занимаюсь твоим вопросом, жди⏳
            Или нажми '/stop'👈 если устал ждать 🦿""";
    public static final String CREATE = "Мы не знакомы, но скоро я про тебя узнаю👁";
    public static final String STOP_DOWNLOAD = "Остановился🎃";
    public static final String INFO_AFTER_STOP = "Можем попробовать еще разок, кидай ссылку на видос✨";
    public static final String INFO_AGAIN = "Сказал же, что нужно КОРРЕКТНАЯ ССЫЛКА НА YOUTUBE ВИДЕО🎬";
    public static final String DONE = "Держи %s🌚 Всё готово💫";
    public static final String PREPARE_TO_LOAD = "Начинаю подготовку к скачиванию🧳";
    public static final String BEGIN_LOADING = "Начал загрузку🚀";
    public static final String INVALID_INPUT = "Что ты мне прислал ? Ничего не разобрать🤬";
    public static final String CHOSE_FORMAT = "Выбери, что тебе нужно, послушать или посмотреть🙃";
    public static final String CHOSE_QUALITY = "Что по качеству хочешь?🤔";
    public static final String FILE_FOUND = "Файл найден🤗";
    public static final String CHOSE_ANOTHER_FORMAT = "Выбери другой формат😒";
    public static final String FILE_IS_TOO_BIG = "Ты мне весь интернет качаешь ? Файл очень много весит👿";


}
