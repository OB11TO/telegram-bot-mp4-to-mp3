package com.ob11to.telegrambotswitch.service;

import com.ob11to.telegrambotswitch.dto.UploadedFileCreateDto;
import com.ob11to.telegrambotswitch.dto.UploadedFileReadDto;
import com.ob11to.telegrambotswitch.entity.ContentType;
import com.ob11to.telegrambotswitch.mapper.UploadedFileCreateMapper;
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
    private final UploadedFileCreateMapper uploadedFileCreateMapper;

    public Optional<UploadedFileReadDto> getFileByVideoIdAndType(String videoId, ContentType format, Integer qualityVideo) {
        return uploadedFileRepository.findByVideoIdAndFormat(videoId, format, qualityVideo)
                .map(uploadedFileReadMapper::map);

    }

    public void deleteFile(String telegramFileId) {
        uploadedFileRepository.deleteUploadedFileByTelegramFileId(telegramFileId);
    }


    @Transactional
    public UploadedFileReadDto createFile(UploadedFileCreateDto uploadedFile) {
        return Optional.of(uploadedFile)
                .map(uploadedFileCreateMapper::map)
                .map(uploadedFileRepository::save)
                .map(uploadedFileReadMapper::map)
                .orElseThrow();
    }
}
