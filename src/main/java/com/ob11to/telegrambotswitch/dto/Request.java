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

    private int qualityCode;

    private boolean isProcessing = false;

    public String getFullName() {
        return videoId + "." + format;
    }
}