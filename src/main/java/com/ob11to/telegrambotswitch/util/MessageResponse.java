package com.ob11to.telegrambotswitch.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageResponse {
    public static final String START = """
            *Меня зовут Гриф 🔮
                           
            Добро пожаловать в мой сервис ⚙
            Я умею скачивать видео с [YouTube](https://youtube.com/) в удобном для вас формате
            Это поможет вам не только удобно прослушивать и просматривать медиа, но и сохранять себе их 📲
                
            От вас потребуется _прислать мне ссылку на видео_, которое вы хотите скачать 🤳
                                       
            Нажмите /info 👈 чтобы узнать больше о моих возможностях* 🕸""";
    public static final String INFO = """
            ⚡*Как пользоваться:
                        
            ▫ Вам нужно только скинуть ссылку на видео с _youtube_ 📽
            ▫ Выбрать интересующий вас формат
                 🎼 mp3 звуковая дорожка
                 🎬 mp4  полноценное видео
                 🌗 Также можно выбрать качество видео: 360 и 720
            ▫ Дождаться загрузки ⌛
                  
            ⚡Если ожидание затянулось или вы передумали, всегда
            можно остановить процесс загрузки командой /stop ⛔
                        
            Пришлите мне ссылку на _youtube_ видео* ✨
             """;
    public static final String WAIT = """
            *Идёт обработка запроса, подождите ещё немного ⏳
            Или нажмите /stop 👈 если больше не хотите ждать* 🦿""";
    public static final String CREATE = "||*Мы не знакомы, но скоро я узнаю о тебе*|| 👁";
    public static final String STOP_DOWNLOAD = "*Процесс остановлен* 🎃";
    public static final String INFO_AFTER_STOP = "*Можем попробовать еще раз, пришлите ссылку на видео* ✨";
    public static final String INFO_AGAIN = "*Сказал же, что нужна корректная ссылка на _youtube_ видео* 🎬";
    public static final String DONE = "*Держите, %s 🌚 Всё готово* 💫";
    public static final String PREPARE_TO_LOAD = "*Начинаю подготовку к скачиванию* 🧳";
    public static final String BEGIN_LOADING = "*Начал загрузку* 🚀";
    public static final String SEND_TO_TELEGRAM = "*Отправляю* 🛰";
    public static final String INVALID_INPUT = "*Что вы мне прислали? Ничего не разобрать* 🤬";
    public static final String CHOSE_FORMAT = "*Выберите предпочитаемый вариант будем слушать или смотреть?* 🙃";
    public static final String CHOSE_QUALITY = "*Какое хотите качество?*🤔";
    public static final String FILE_FOUND = "*Файл найден* ✅";
    public static final String CHOSE_ANOTHER_FORMAT = "*Произошла непредвиденная ошибка* 😒 \n" +
            "*Выберите другой формат* 🤕";
    public static final String FILE_IS_TOO_BIG = "*Ваш файл превышает допустимый размер, я его качать не буду* 👿";
    public static final String CLICK_STOP_IN_READY = "*Не получится ничего остановить, я ещё не начинал загрузку* 🌐";
    public static final String SEND_LINK = "*Пришлите мне ссылку на _youtube_ видео 📽, которое вам нужно* 🎯";


}
