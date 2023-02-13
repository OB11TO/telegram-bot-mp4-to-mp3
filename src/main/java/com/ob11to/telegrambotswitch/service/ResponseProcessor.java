package com.ob11to.telegrambotswitch.service;

import com.ob11to.telegrambotswitch.dto.Request;
import com.ob11to.telegrambotswitch.dto.Response;
import com.ob11to.telegrambotswitch.entity.ContentType;
import com.ob11to.telegrambotswitch.service.youtube.YouTubeDownloaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResponseProcessor {

    private final YouTubeDownloaderService youtubeDownloader;
    private final FolderManagerService folderManagerService;

    public Response processResponse(Request request) {
        boolean isAudio = ContentType.mp3.equals(request.getFormat());
        String videoId = request.getVideoId();

        ContentType type;

        youtubeDownloader.download(request);

        type = isAudio
                ? ContentType.mp3
                : ContentType.mp4;

        return new Response(
                type,
                videoId,
                folderManagerService.getPath(),
                request.getQualityVideo()
        );
    }

    public void stopDownload() {
        youtubeDownloader.stopDownloading();
    }
}
