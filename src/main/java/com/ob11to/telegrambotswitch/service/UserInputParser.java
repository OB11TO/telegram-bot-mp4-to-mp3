package com.ob11to.telegrambotswitch.service;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UserInputParser {

    private static final String YOUTUBE_URL_PATTERN_V1 = "(?<=watch\\?v=|/videos/|embed/|shorts/|youtu.be/|/v/|/e/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#&?\\n]*";
    private static final String YOUTUBE_URL_PATTERN_V2 = "https?://(?:[0-9A-Z-]+\\.)?(?:youtu\\.be/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|</a>))[?=&+%\\w]*";
    private static final String TIK_TOK_V1 = "\\bhttps?:\\/\\/(?:m|www|vm)\\.tiktok\\.com\\/\\S*?\\b(?:(?:(?:usr|v|embed|user|video)\\/|\\?shareId=|\\&item_id=)(\\d+)|(?=\\w{7})(\\w*?[A-Z\\d]\\w*)(?=\\s|\\/$))\\b";
    private static final String TIK_TOK_V2 = "https?:\\/\\/(?:m|www|vm|vt)\\.tiktok\\.com\\/\\S*?(\\w*?[A-Z\\d]\\w*)";
    private final Pattern PATTERN_V1;
    private final Pattern PATTERN_V2;
    private final Pattern PATTERN_V3;
    private final Pattern PATTERN_V4;

    public UserInputParser() {
        PATTERN_V1 = Pattern.compile(YOUTUBE_URL_PATTERN_V1,
                Pattern.CASE_INSENSITIVE);
        PATTERN_V2 = Pattern.compile(YOUTUBE_URL_PATTERN_V2,
                Pattern.CASE_INSENSITIVE);
        PATTERN_V3 = Pattern.compile(TIK_TOK_V1,
                Pattern.CASE_INSENSITIVE);
        PATTERN_V4 = Pattern.compile(TIK_TOK_V2,
                Pattern.CASE_INSENSITIVE);
    }

    public String getYouTubeVideoId(String input) {
        var youtubeUrlMatcherV1 = PATTERN_V1.matcher(input);
        var youtubeUrlMatcherV2 = PATTERN_V2.matcher(input);
        var tiktokV1 = PATTERN_V3.matcher(input);
        var tiktokV2 = PATTERN_V4.matcher(input);
        if (youtubeUrlMatcherV1.find())
            return youtubeUrlMatcherV1.group();
        if (youtubeUrlMatcherV2.find())
            return youtubeUrlMatcherV2.group();
        if (tiktokV1.find())
            return tiktokV1.group(1);
        if (tiktokV2.find())
            return tiktokV2.group(1);
        return null;
    }
}

