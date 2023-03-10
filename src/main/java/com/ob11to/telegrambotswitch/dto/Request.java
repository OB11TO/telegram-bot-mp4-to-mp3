package com.ob11to.telegrambotswitch.dto;

import com.ob11to.telegrambotswitch.entity.ContentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Request {

    private String videoId;

    private ContentType format;

    private String qualityCode;

    private boolean isProcessing = false;

    private Integer qualityVideo;

    public String getFullName() {
        return videoId + "." + format;
    }
}