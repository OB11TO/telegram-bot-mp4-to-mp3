package com.ob11to.telegrambotswitch.mapper;

import com.ob11to.telegrambotswitch.dto.UploadedFileReadDto;
import com.ob11to.telegrambotswitch.entity.UploadedFile;
import org.springframework.stereotype.Component;

@Component
public class UploadedFileReadMapper implements Mapper<UploadedFile, UploadedFileReadDto> {


    @Override
    public UploadedFileReadDto map(UploadedFile object) {
        return new UploadedFileReadDto(
                object.getId(),
                object.getYoutubeVideoId(),
                object.getTelegramFileId(),
                object.getType()
        );
    }
}
