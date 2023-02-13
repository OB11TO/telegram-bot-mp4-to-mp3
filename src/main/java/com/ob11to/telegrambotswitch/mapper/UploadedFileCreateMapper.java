package com.ob11to.telegrambotswitch.mapper;

import com.ob11to.telegrambotswitch.dto.UploadedFileCreateDto;
import com.ob11to.telegrambotswitch.entity.UploadedFile;
import org.springframework.stereotype.Component;

@Component
public class UploadedFileCreateMapper implements Mapper<UploadedFileCreateDto, UploadedFile> {

    @Override
    public UploadedFile map(UploadedFileCreateDto object) {
        return UploadedFile.builder()
                .youtubeVideoId(object.getYoutubeVideoId())
                .telegramFileId(object.getTelegramFileId())
                .type(object.getType())
                .qualityVideo(object.getQualityVideo())
                .build();
    }
}
