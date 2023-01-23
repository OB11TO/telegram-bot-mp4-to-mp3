CREATE TABLE IF NOT EXISTS users_telegram_bot
(
    id        BIGSERIAL PRIMARY KEY,
    chat_id   BIGSERIAL    NOT NULL,
    username VARCHAR(255) NOT NULL,
    bot_state VARCHAR(64)  NOT NULL
);

CREATE TABLE uploaded_files
(
    id               BIGSERIAL PRIMARY KEY,
    youtube_video_id VARCHAR(255) NOT NULL,
    telegram_file_id VARCHAR(255) NOT NULL,
    media_type       VARCHAR(64)  NOT NULL
);

