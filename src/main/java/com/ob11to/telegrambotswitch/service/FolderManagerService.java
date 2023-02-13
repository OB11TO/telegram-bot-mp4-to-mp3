package com.ob11to.telegrambotswitch.service;

import com.ob11to.telegrambotswitch.dto.Request;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Service
public class FolderManagerService {

    private final static String[] FILE_EXTENSIONS = {"MP3", "mP3", "Mp3", "mp3", "MP4", "mP4", "Mp4", "mp4", "m4a", "M4a", "m4A", "M4A"};

    private final String path;

    public FolderManagerService() {
        path = System.getProperty("user.dir") + "/youtube";
    }

    public void createDirectoryForFiles() {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdir();
            //TODO var mkdir = directory.mkdir();
        }
    }

    public String getPath() {
        return path;
    }

    public void clean(Request request, boolean isItPart) {
        if (isItPart) {
            FileUtils.listFiles(new File(getPath()), FILE_EXTENSIONS, true).stream()
                    .filter(file ->file.getName().contains(request.getVideoId()) && file.getName().contains(".part"))
                    .forEach(File::delete);
        } else {
            FileUtils.listFiles(new File(getPath()), FILE_EXTENSIONS, true).stream()
                    .filter(file -> file.getName().contains(request.getVideoId()))
                    .forEach(File::delete);
        }
    }

    public double getFileSize(Request request) {
        File path = new File(getPath());
        File[] matchingFiles = path.listFiles((dir, file) -> file.contains(request.getVideoId()));
        Optional<File> maybeFile = Arrays.stream(Objects.requireNonNull(matchingFiles)).findFirst();
        //TODO: доделать
        double size = (double) maybeFile.get().length();
        return size / 1024 / 1024;
    }
}
