package com.ob11to.telegrambotswitch.dto;

import com.ob11to.telegrambotswitch.entity.ContentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Getter
@Setter
@AllArgsConstructor
public class Response {

    private ContentType contentType;

    private String name;

    private String path;

    private Integer qualityVideo;

    public InputStream getContentStream(){
        try {
            return new FileInputStream(path + "/" + name + "." + contentType);

        } catch (FileNotFoundException e) {
            return null;
        }
    }

}
