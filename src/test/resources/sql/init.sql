CREATE TABLE IF NOT EXISTS bot_states
(
    id    INT PRIMARY KEY,
    state VARCHAR(128) NOT NULL
);

CREATE TABLE IF NOT EXISTS users_telegram_bot
(
    id        BIGSERIAL PRIMARY KEY,
    chat_id   BIGSERIAL    NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    bot_state INT          NOT NULL REFERENCES bot_states (id)
);

CREATE TABLE media_type
(
    id   INT PRIMARY KEY,
    type VARCHAR(64) NOT NULL
);

CREATE TABLE uploaded_files
(
    id               BIGSERIAL PRIMARY KEY,
    youtube_video_id VARCHAR(255) NOT NULL,
    telegram_file_id text         NOT NULL,
    media_type       int          NOT NULL REFERENCES media_type (id)
);

