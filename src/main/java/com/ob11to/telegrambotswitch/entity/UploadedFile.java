package com.ob11to.telegrambotswitch.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "uploaded_files")
public class UploadedFile implements BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "youtube_video_id", nullable = false)
    private String youtubeVideoId;

    @Column(name = "telegram_file_id",nullable = false, unique = true )
    private String telegramFileId;

    @Column(name = "media_type",nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentType type;
}
