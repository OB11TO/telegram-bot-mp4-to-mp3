package com.ob11to.telegrambotswitch.dto;

import com.ob11to.telegrambotswitch.entity.ContentType;
import lombok.Value;

@Value
public class UploadedFileReadDto {
    Long id;
    String youtubeVideoId;
    String telegramFileId;
    ContentType type;
}
