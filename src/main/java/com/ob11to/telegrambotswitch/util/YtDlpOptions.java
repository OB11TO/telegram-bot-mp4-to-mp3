package com.ob11to.telegrambotswitch.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class YtDlpOptions {
    /**
     * Checking for the maximum allowed file size 50 Mb!
     */
    public final static double MAX_UPLOADED_FILE_SIZE = 50;

    /**
     * The quality that we choose when downloading YouTube videos.
     */
    public final static String MP4_360_QUALITY_CODE = "18";
    public final static String MP4_720_QUALITY_CODE = "136+140";
    public final static String MP3_QUALITY_CODE = "140";

    /**
     * Quality video
     */
    public final static Integer MP4_360 = 360;
    public final static Integer MP4_720 = 720;
    public final static Integer MP3 = 0;
    public final static Integer MP4_FOR_TIKTOK = 111;
}
