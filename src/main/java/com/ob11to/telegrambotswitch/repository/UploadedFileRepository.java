package com.ob11to.telegrambotswitch.repository;

import com.ob11to.telegrambotswitch.entity.ContentType;
import com.ob11to.telegrambotswitch.entity.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {

    @Query("select uf from UploadedFile uf " +
            "where uf.youtubeVideoId = :videoId and uf.type = :format and uf.qualityVideo = :qualityVideo")
    Optional<UploadedFile> findByVideoIdAndFormat(String videoId, ContentType format, Integer qualityVideo);

    void deleteUploadedFileByTelegramFileId(String telegramFileId);
}
