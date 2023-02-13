package com.ob11to.telegrambotswitch.dto;

import com.ob11to.telegrambotswitch.entity.ContentType;
import lombok.Value;

@Value
public class UploadedFileCreateDto {
    String youtubeVideoId;
    String telegramFileId;
    ContentType type;
    Integer qualityVideo;
}
