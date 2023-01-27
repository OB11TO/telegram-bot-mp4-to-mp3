package com.ob11to.telegrambotswitch.service.youtube;

import com.ob11to.telegrambotswitch.dto.Request;
import com.ob11to.telegrambotswitch.entity.ContentType;
import com.ob11to.telegrambotswitch.service.FolderManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class YouTubeDownloaderService {

    private final static String YOUTUBE_VIDEO_URL = "https://www.youtube.com/watch?v=%s";
    private final static String STANDARD_DL_COMMAND_WITH_FORMAT_OPTIONS = "youtube-dl -f %s ";
    private final static String STANDARD_DL_PATH_OPTION = " -o %s/%s.%s ";

    private final static String TERMINAL = "/bin/sh";
    private final static String END_COMMAND = "-c";

    private Process process;

    private final FolderManagerService folderManagerService;

    @Autowired
    public YouTubeDownloaderService(FolderManagerService folderManagerService) {
        this.folderManagerService = folderManagerService;
        folderManagerService.createDirectoryForFiles();
    }

    public void download(Request request) {
        String[] commands = new String[3];
        int exitCode;
        try {
            ProcessBuilder builder = new ProcessBuilder();
            log.info("Start executing command to cmd.exe ");
            commands[0] = TERMINAL;
            commands[1] = END_COMMAND;
            commands[2] = buildCommand(request);
            builder.command(commands);
            builder.inheritIO();
            builder.redirectErrorStream(true);

            process = builder.start();

            log.info("Run process: " + process.info());

            try {
                exitCode = process.waitFor();
            } catch (InterruptedException e) {
                log.error("Error in process: " + process.info(), e);
                throw new RuntimeException("Couldn`t send wanted quality: " + request.getQualityCode());
            }

            if (exitCode > 0) {
                log.error("Error in process: " + process.info());
                stopDownloading();
                throw new RuntimeException("Problem occurred while processing input stream");
            }

        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    // IN PROGRESS
    public void stopDownloading() {
        process.destroy();
    }

    private String buildCommand(Request request) {
        String finalFormat;
        if (request.getFormat().equals(ContentType.mp3)) {
            finalFormat = String.format(STANDARD_DL_COMMAND_WITH_FORMAT_OPTIONS, "bestaudio");
        } else {
            finalFormat = String.format(STANDARD_DL_COMMAND_WITH_FORMAT_OPTIONS, request.getQualityCode());
        }
        return finalFormat +
                String.format(STANDARD_DL_PATH_OPTION,
                        folderManagerService.getPath(), request.getVideoId(), request.getFormat().name())
                + String.format(YOUTUBE_VIDEO_URL, request.getVideoId());
    }
}
