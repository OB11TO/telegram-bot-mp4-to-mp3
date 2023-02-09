package com.ob11to.telegrambotswitch.service;

import com.ob11to.telegrambotswitch.dto.Request;
import com.ob11to.telegrambotswitch.entity.ContentType;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UserInputParser {

    private static final String YOUTUBE_URL_PATTERN_V1 = "(?<=watch\\?v=|/videos/|embed/|youtu.be/|/v/|/e/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#&?\\n]*";
    private static final String YOUTUBE_URL_PATTERN_V2 = "https?://(?:[0-9A-Z-]+\\.)?(?:youtu\\.be/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|</a>))[?=&+%\\w]*";
    private final Pattern PATTERN_V1;
    private final Pattern PATTERN_V2;

    public UserInputParser() {
        PATTERN_V1 = Pattern.compile(YOUTUBE_URL_PATTERN_V1,
                Pattern.CASE_INSENSITIVE);
        PATTERN_V2 = Pattern.compile(YOUTUBE_URL_PATTERN_V2,
                Pattern.CASE_INSENSITIVE);
    }

    public String getYouTubeVideoId(String input) {
        var youtubeUrlMatcherV1 = PATTERN_V1.matcher(input);
        var youtubeUrlMatcherV2 = PATTERN_V2.matcher(input);
        if (youtubeUrlMatcherV1.find())
            return youtubeUrlMatcherV1.group();
        if (youtubeUrlMatcherV2.find())
            return youtubeUrlMatcherV2.group();
        return null;
    }

    public Request processInputMessage(String message, ContentType type, String code) {
        return new Request(getYouTubeVideoId(message),
                type,
                code,
                false);

    }
}

