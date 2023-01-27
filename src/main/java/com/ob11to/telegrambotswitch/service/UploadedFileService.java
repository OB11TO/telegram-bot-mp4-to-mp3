package com.ob11to.telegrambotswitch.service;

import com.ob11to.telegrambotswitch.dto.UploadedFileReadDto;
import com.ob11to.telegrambotswitch.entity.ContentType;
import com.ob11to.telegrambotswitch.mapper.UploadedFileReadMapper;
import com.ob11to.telegrambotswitch.repository.UploadedFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UploadedFileService {

    private final UploadedFileRepository uploadedFileRepository;
    private final UploadedFileReadMapper uploadedFileReadMapper;

    public Optional<UploadedFileReadDto> getFileByVideoIdAndType(String videoId, ContentType format) {
        return uploadedFileRepository.findByVideoIdAndFormat(videoId, format)
                .map(uploadedFileReadMapper::map);

    }

    public void deleteFile(String telegramFileId) {
        uploadedFileRepository.deleteUploadedFileByTelegramFileId(telegramFileId);
    }

}