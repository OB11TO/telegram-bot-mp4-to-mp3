package com.ob11to.telegrambotswitch.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageResponse {
    public static final String CREATE = "||*🔮: Мы еще не знакомы, но скоро запомнишь моё имя*||👁";
    public static final String START = """
            🔮: *Добро пожаловать*
                        
            _📍__Что я умею?___
            📍_Скачивать видео с *[YouTube](https://youtube.com/)* в разных форматах_ 📲
            _Premium Youtube больше не нужен_ 🔑
                        
            _📍__Что мне нужно от тебя?___
            _Просто пришли мне ссылку на видео, которое хочешь скачать 🔗_
                                       
            *Нажми /help 👈 чтобы узнать больше о моих возможностях*""";
    public static final String HELP = """
            ⚡*Как пользоваться*

             📍 _Пришли ссылку на видео с YouTube 🔗
             📍 Выбери нужный формат\s
                   ✔️ mp3: только звуковая дорожка
                   ✔️ mp4: видео в 360px или 720px
             📍 Дождись загрузки ⌛

            Если ожидание затянулось или ты передумал,
            просто останови процесс загрузки командой
            /stop_ ⛔

            *Пришли мне ссылку на _YouTube_ видео* ✨
            """;
    public static final String INFO = """
            ⚡*Часто задаваемые вопросы*
                        
            📍*__Как воспроизводить контент в офлайн режиме?__*
            _Вы можете скачать ролик или композицию на устройство и воспроизводить их без подключения к Интернету_
                        
            📍*__Что такое фоновый режим?__*
            _Это функция, которая позволяет слушать музыку во время работы с другими приложениями или когда экран телефона выключен_
                        
            📍*__Как скачать видео или музыку?__*
            _Нужно прислать боту ссылку на YouTube видео, которое хотите скачать_
             """;
    public static final String WAIT = """
            *🔮: Идёт обработка запроса, подожди ещё немного ⏳
            Или нажми /stop 👈 если больше не хочешь ждать* 🧘""";
    public static final String STOP_DOWNLOAD = "*🔮: Процесс остановлен* ⛔";
    public static final String INFO_AFTER_STOP = "*🔮: Попробуем еще раз? Пришли ссылку на видео* ✨";
    public static final String ERROR_SIZE_TRY_AGAIN = "*🔮: Давай по новой\\! Пришли ссылку на видео* ✨";
    public static final String DONE = "🔮: *Держи, %s 🎁 Всё готово* 💫";
    public static final String PREPARE_TO_LOAD = "*🔮: Готовлюсь к скачиванию* 🧳";
    public static final String BEGIN_LOADING = "*🔮: Начал загрузку* 🚀";
    public static final String SEND_TO_TELEGRAM = "*🔮: Отправляю* 🛰";
    public static final String INVALID_INPUT = "*🔮: Что это? Ничего не разобрать, проверь ссылку на _Youtube_ видео 📽* ";
    public static final String CHOSE_FORMAT = "*🔮: Выбери нужный формат, будем слушать или смотреть?* 🙃";
    public static final String CHOSE_QUALITY = "*🔮: Какое хочешь качество?*🤔";
    public static final String FILE_FOUND = "*🔮: Нашёл файл* ✅";
    public static final String CHOSE_ANOTHER_FORMAT = "*🔮: Произошла непредвиденная ошибка* 🚫 \n" +
            "*Выбери другой формат и скинь ссылку заново* 🤕";
    public static final String FILE_IS_TOO_BIG = "*🔮: Твой файл превышает допустимый размер, я его качать не буду* 👿";
    public static final String CLICK_STOP_IN_READY = "*🔮: Не получится ничего остановить, я ещё не начинал загрузку* 🌐";
    public static final String SEND_LINK = "*🔮: Пришли мне ссылку на _YouTube_ видео, которое нужно* 🎯";


}
