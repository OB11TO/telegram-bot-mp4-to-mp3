package com.ob11to.telegrambotswitch.exception;

public class YouTubeSizeException extends RuntimeException {

    public static YouTubeSizeException bigFileSize() {
        return new YouTubeSizeException("Большой размер файла для отправки в телеграмм!");
    }

    public YouTubeSizeException(String message) {
        super(message);
    }
}
