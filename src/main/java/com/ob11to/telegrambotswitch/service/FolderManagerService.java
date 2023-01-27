package com.ob11to.telegrambotswitch.service;

import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class FolderManagerService {

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
}
