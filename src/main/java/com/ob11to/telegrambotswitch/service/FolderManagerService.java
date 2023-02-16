package com.ob11to.telegrambotswitch.service;

import com.ob11to.telegrambotswitch.dto.Request;
import com.ob11to.telegrambotswitch.dto.Response;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Service
public class FolderManagerService {

    private final static String[] FILE_EXTENSIONS = {"MP3", "mP3", "Mp3", "mp3", "MP4", "mP4", "Mp4", "mp4", "m4a", "M4a", "m4A", "M4A"};

    @Getter
    private final String path;

    public FolderManagerService() {
        path = System.getProperty("user.dir") + "/youtube";
    }

    public void createDirectoryForFiles() {
        File directory = new File(path);
        if (!directory.exists()) {
            var mkdir = directory.mkdir();
            if (!mkdir) {
                throw new RuntimeException("No create directory for files");
            }
        }
    }

    public void clean(Request request, boolean isItPart) {
        if (isItPart) {
            FileUtils.listFiles(new File(getPath()), FILE_EXTENSIONS, true).stream()
                    .filter(file -> file.getName().contains(request.getVideoId()) && file.getName().contains(".part"))
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
        if (maybeFile.isPresent()) {
            double size = (double) maybeFile.get().length();
            return size / 1024 / 1024;
        } else {
            throw new RuntimeException("File not found");
        }
    }

    public InputFile getInputFile(Response userResponse, String type) {
        var name = userResponse.getName();
        File path = new File(getPath());
        File[] matchingFiles = path.listFiles((dir, file) -> file.contains(name) && file.endsWith(type));
        Optional<File> maybeFile = Arrays.stream(Objects.requireNonNull(matchingFiles)).findFirst();
        var file = maybeFile.orElseThrow(() -> new RuntimeException("File is not found"));
        InputFile inputFile = new InputFile();
        inputFile.setMedia(file);
        return inputFile;
    }
}
