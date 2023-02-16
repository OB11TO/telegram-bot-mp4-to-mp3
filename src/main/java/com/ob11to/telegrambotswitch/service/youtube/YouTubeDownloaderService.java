package com.ob11to.telegrambotswitch.service.youtube;

import com.ob11to.telegrambotswitch.dto.Request;
import com.ob11to.telegrambotswitch.service.FolderManagerService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
public class YouTubeDownloaderService {

    private final static String YOUTUBE_VIDEO_URL = "https://www.youtube.com/watch?v=%s";
    private final static String STANDARD_DL_COMMAND_WITH_FORMAT_OPTIONS = "yt-dlp -f %s ";
//    private final static String STANDARD_DL_COMMAND_WITH_FORMAT_OPTIONS_TIK_TOK = "yt-dlp -o '%(id)s.%(ext)s' ";
    private final static String STANDARD_DL_COMMAND_WITH_FORMAT_OPTIONS_TIK_TOK = "yt-dlp -o %s.mp4 ";
//    private final static String STANDARD_DL_PATH_OPTION = " -o %s/%(title)s.%(ext)s ";

    private final static String TERMINAL_BASH = "/bin/sh";
    private final static String TERMINAL_CMD = "cmd.exe";
    private final static String END_COMMAND_BASH = "-c";
    private final static String END_COMMAND_CMD = "/c";

    @Setter
    private String LINK;

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
            commands[0] = System.getProperty("os.name").equals("Linux") ? TERMINAL_BASH : TERMINAL_CMD;
            commands[1] = System.getProperty("os.name").equals("Linux") ? END_COMMAND_BASH : END_COMMAND_CMD;
            commands[2] = buildCommandTest(request);
            builder.command(commands);
            builder.directory(new File(folderManagerService.getPath()));
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

    //TODO: ПРОВЕРИТЬ РАБОТУ
    public void stopDownloading() {
        if (process.isAlive()) {
            String nameImageLinux = "yt-dlp";
            String nameImageWin = "yt-dlp";
            ProcessBuilder pb = new ProcessBuilder();
            Process start;
            try {
                if (System.getProperty("os.name").equals("Linux")) {
                    pb.command(TERMINAL_BASH, END_COMMAND_BASH, String.format("killall %s", nameImageLinux));
                } else {
                    pb.command(TERMINAL_CMD, END_COMMAND_CMD, String.format("taskkill /F /IM  %s", nameImageWin));
                }
                start = pb.start();
                start.waitFor();
                log.info("Kill process: " + start.info());
                process.destroy();
                start.destroy();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            log.warn("Process already killed!");
        }
    }

    private String buildCommandTest(Request request) {
        String finalFormat;
        if(request.getQualityCode().equals("tiktok")) {
            finalFormat = String.format(STANDARD_DL_COMMAND_WITH_FORMAT_OPTIONS_TIK_TOK, request.getVideoId());
        } else {
            finalFormat = String.format(STANDARD_DL_COMMAND_WITH_FORMAT_OPTIONS, request.getQualityCode());
        }
        return finalFormat
                + LINK;
    }
}
