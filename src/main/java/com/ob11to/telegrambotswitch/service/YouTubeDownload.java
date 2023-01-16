/*
package com.ob11to.telegrambotswitch.service;

import io.github.gaeqs.javayoutubedownloader.JavaYoutubeDownloader;
import io.github.gaeqs.javayoutubedownloader.decoder.MultipleDecoderMethod;
import io.github.gaeqs.javayoutubedownloader.stream.StreamOption;
import io.github.gaeqs.javayoutubedownloader.stream.YoutubeVideo;
import io.github.gaeqs.javayoutubedownloader.stream.download.StreamDownloader;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.util.Comparator;

@Service
public class YouTubeDownload {

    public static void main(String[] args) {
//        String url = "https://www.youtube.com/watch?v=NG9vYdpeI_0";
//        String path = "/home/obiito/c/youtube/test";
//        File file = new File(path);
//        System.out.println(download(url, file));


    }

    public InputFile download(String url, File folder) {
        String prefix = "https://www.youtube.com";
        InputFile inputFile = new InputFile();

        if(!url.startsWith(prefix)) {
            inputFile.setMedia("/home/obiito/c/youtube/test/1.mp4");
            return inputFile;
        }
        //Extracts and decodes all streams.
        YoutubeVideo video = JavaYoutubeDownloader.decodeOrNull(url, MultipleDecoderMethod.AND, "html", "embedded");
        if (video == null) return inputFile;
        //Gets the option with the greatest quality that has video and audio.
        StreamOption option = video.getStreamOptions().stream()
                .filter(target -> target.getType().hasVideo() && target.getType().hasAudio())
                .min(Comparator.comparingInt(o -> o.getType().getVideoQuality().ordinal())).orElse(null);
        //If there is no option, returns false.
        if (option == null) return inputFile;
        //Prints the option type.
        System.out.println(option.getType());
        //Creates the file. folder/title.extension
        File file = new File(folder, video.getTitle() + "." + option.getType().getContainer().toString().toLowerCase());
        //Creates the downloader.
        StreamDownloader downloader = new StreamDownloader(option, file, null);
        //Runs the downloader.
        new Thread(downloader).start();
        InputFile f = new InputFile(file);
        return f;
    }

}
*/
