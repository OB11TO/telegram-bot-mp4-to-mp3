package com.ob11to.telegrambotswitch.telegram;

import com.ob11to.telegrambotswitch.cache.RequestsStorage;
import com.ob11to.telegrambotswitch.config.TelegramBotConfig;
import com.ob11to.telegrambotswitch.dto.Request;
import com.ob11to.telegrambotswitch.dto.Response;
import com.ob11to.telegrambotswitch.dto.UploadedFileCreateDto;
import com.ob11to.telegrambotswitch.dto.UploadedFileReadDto;
import com.ob11to.telegrambotswitch.dto.UserTelegramReadDto;
import com.ob11to.telegrambotswitch.entity.ContentType;
import com.ob11to.telegrambotswitch.exception.YouTubeSizeException;
import com.ob11to.telegrambotswitch.service.FolderManagerService;
import com.ob11to.telegrambotswitch.service.ReplyMessageService;
import com.ob11to.telegrambotswitch.service.ResponseProcessor;
import com.ob11to.telegrambotswitch.service.UploadedFileService;
import com.ob11to.telegrambotswitch.service.UserInputParser;
import com.ob11to.telegrambotswitch.service.UserTelegramService;
import com.ob11to.telegrambotswitch.service.youtube.YouTubeDownloaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

import static com.ob11to.telegrambotswitch.entity.TelegramBotState.BUSY;
import static com.ob11to.telegrambotswitch.entity.TelegramBotState.READY;
import static com.ob11to.telegrambotswitch.util.MessageResponse.BEGIN_LOADING;
import static com.ob11to.telegrambotswitch.util.MessageResponse.CHOSE_ANOTHER_FORMAT;
import static com.ob11to.telegrambotswitch.util.MessageResponse.CHOSE_FORMAT;
import static com.ob11to.telegrambotswitch.util.MessageResponse.CHOSE_QUALITY;
import static com.ob11to.telegrambotswitch.util.MessageResponse.CLICK_STOP_IN_READY;
import static com.ob11to.telegrambotswitch.util.MessageResponse.CREATE;
import static com.ob11to.telegrambotswitch.util.MessageResponse.DONE;
import static com.ob11to.telegrambotswitch.util.MessageResponse.ERROR_SIZE_TRY_AGAIN;
import static com.ob11to.telegrambotswitch.util.MessageResponse.FILE_FOUND;
import static com.ob11to.telegrambotswitch.util.MessageResponse.FILE_IS_TOO_BIG;
import static com.ob11to.telegrambotswitch.util.MessageResponse.HELP;
import static com.ob11to.telegrambotswitch.util.MessageResponse.INFO;
import static com.ob11to.telegrambotswitch.util.MessageResponse.INFO_AFTER_STOP;
import static com.ob11to.telegrambotswitch.util.MessageResponse.INVALID_INPUT;
import static com.ob11to.telegrambotswitch.util.MessageResponse.PREPARE_TO_LOAD;
import static com.ob11to.telegrambotswitch.util.MessageResponse.SEND_LINK;
import static com.ob11to.telegrambotswitch.util.MessageResponse.SEND_MESSAGE_SCHEDULE;
import static com.ob11to.telegrambotswitch.util.MessageResponse.SEND_TO_TELEGRAM;
import static com.ob11to.telegrambotswitch.util.MessageResponse.START;
import static com.ob11to.telegrambotswitch.util.MessageResponse.STOP_DOWNLOAD;
import static com.ob11to.telegrambotswitch.util.MessageResponse.WAIT;
import static com.ob11to.telegrambotswitch.util.MessageResponse.WAIT_DOWNLOAD;
import static com.ob11to.telegrambotswitch.util.YtDlpOptions.MAX_UPLOADED_FILE_SIZE;
import static com.ob11to.telegrambotswitch.util.YtDlpOptions.MP3;
import static com.ob11to.telegrambotswitch.util.YtDlpOptions.MP3_QUALITY_CODE;
import static com.ob11to.telegrambotswitch.util.YtDlpOptions.MP4_360;
import static com.ob11to.telegrambotswitch.util.YtDlpOptions.MP4_360_QUALITY_CODE;
import static com.ob11to.telegrambotswitch.util.YtDlpOptions.MP4_720;
import static com.ob11to.telegrambotswitch.util.YtDlpOptions.MP4_720_QUALITY_CODE;
import static com.ob11to.telegrambotswitch.util.YtDlpOptions.MP4_FOR_TIKTOK;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBotWebHookService extends TelegramWebhookBot {

    private final TelegramBotConfig config;
    private final UserTelegramService userTelegramService;
    private final ReplyMessageService replyMessageService;
    private final UserInputParser userInputParser;
    private final TelegramFacade telegramFacade;
    private final RequestsStorage requestsStorage;
    private final UploadedFileService uploadedFileService;
    private final ResponseProcessor responseProcessor;
    private final FolderManagerService folderManagerService;
    private final YouTubeDownloaderService youTubeDownloaderService;

    @Override
    public String getBotUsername() {
        return config.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public String getBotPath() {
        return config.getBotPath();
    }

    @Scheduled(cron = "22 22 22 1 * *")
    public void scheduleSendTelegramMessage() {
        var userReadyBot = userTelegramService.getAllUserReadyBot();
        for (UserTelegramReadDto user : userReadyBot) {
            try {
                execute(replyMessageService.getReplyMessage(user.getChatId(), SEND_MESSAGE_SCHEDULE));
            } catch (TelegramApiException e) {
                e.printStackTrace();
                log.error("Error while executing message ", e);
            }
        }
    }


    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (update.getMessage() != null && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            var username = update.getMessage().getChat().getUserName();
            var firstname = update.getMessage().getChat().getFirstName();
            log.info("Process update from chatId: " + chatId);
            Optional<UserTelegramReadDto> findUserTelegram = userTelegramService.getUserBotChatId(chatId);
            if (findUserTelegram.isPresent()) {
                UserTelegramReadDto userTelegram = findUserTelegram.get();
                processUpdate(userTelegram, update.getMessage());
            } else {
                try {
                    var name = username == null ? firstname : username;
                    var userTelegram = userTelegramService.createUserTelegram(chatId, name);
                    log.info("Create user with chatId:" + userTelegram.getChatId());
                    execute(replyMessageService.getReplyMessage(userTelegram.getChatId(), CREATE));
                    execute(replyMessageService.getReplyMessage(userTelegram.getChatId(), START));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                    log.error("Error while executing message ", e);
                }
            }
        } else if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            String answer = update.getCallbackQuery().getData();
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            Optional<UserTelegramReadDto> findUserTelegram = userTelegramService.getUserBotChatId(chatId);
            if (findUserTelegram.isPresent()) {
                var userTelegram = findUserTelegram.get();
                try {
                    Request currentRequest = requestsStorage.getCurrentRequest(chatId);
                    if (requestsStorage.isRequestPresent(chatId)) {
                        if (!currentRequest.isProcessing()) {
                            deleteMessage(chatId, messageId);
                            processCallBack(chatId, answer, currentRequest, userTelegram.getUsername());
                        }
                    }
                } catch (TelegramApiException e) {
                    log.error("Error while executing message ", e);
                }
            }
        }
        return null;
    }

    private void processUpdate(UserTelegramReadDto userTelegram, @NotNull Message message) {
        try {
            if (message.getText().equals("/start")) {
                execute(replyMessageService.getReplyMessage(userTelegram.getChatId(), START));
            } else if (message.getText().equals("/help")) {
                execute(replyMessageService.getReplyMessage(userTelegram.getChatId(), HELP));
            } else if (message.getText().equals("/info")) {
                execute(replyMessageService.getReplyMessage(userTelegram.getChatId(), INFO));
            } else if (userTelegram.getState() == READY && message.getText().equals("/stop")) {
                execute(replyMessageService.getReplyMessage(userTelegram.getChatId(), CLICK_STOP_IN_READY));
                execute(replyMessageService.getReplyMessage(userTelegram.getChatId(), SEND_LINK));
            } else if (userTelegram.getState() == READY && !message.getText().isEmpty()) {
                botIsReadyToProcessUrl(userTelegram.getChatId(), message, userTelegram.getUsername());
            } else if (userTelegram.getState() == BUSY && message.getText().equals("/stop")) {
                execute(replyMessageService.getReplyMessage(userTelegram.getChatId(), STOP_DOWNLOAD));
                execute(replyMessageService.getReplyMessage(userTelegram.getChatId(), INFO_AFTER_STOP));
                userTelegramService.changeBotStateByChatId(userTelegram.getChatId(), READY);
                log.info("Change bot state to READY for chatId: " + userTelegram.getChatId());
                responseProcessor.stopDownload();
            } else if (userTelegram.getState() == BUSY) {
                execute(replyMessageService.getReplyMessage(userTelegram.getChatId(), WAIT));
            }

        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }

    private void botIsReadyToProcessUrl(Long chatId, @NotNull Message message, String username) throws TelegramApiException {
        userTelegramService.changeBotStateByChatId(chatId, BUSY);
        log.info("Change bot state to BUSY for chatId: " + chatId);

        youTubeDownloaderService.setLINK(message.getText());
        String videoId = userInputParser.getYouTubeVideoId(message.getText());

        if (videoId == null) {
            execute(replyMessageService.getReplyMessage(chatId, INVALID_INPUT));
            log.info("Invalid url from user, input: " + message.getText());
            userTelegramService.changeBotStateByChatId(chatId, READY);
            log.info("Change bot state to READY for chatId: " + chatId);
        } else if (message.getText().contains("tiktok")) {
            execute(replyMessageService.getReplyMessage(chatId, FILE_FOUND));
            Request userRequest = new Request();
            userRequest.setVideoId(videoId);
            userRequest.setFormat(ContentType.mp4);
            userRequest.setQualityVideo(MP4_FOR_TIKTOK);
            requestsStorage.addRequest(chatId, userRequest);
            log.info("Put request to map with chatId: " + chatId + " videoId: " + videoId);
            sendFileInFormat(chatId, "tiktok", userRequest, username);
        } else {
            execute(replyMessageService.getReplyMessage(chatId, FILE_FOUND));
            Request userRequest = new Request();
            userRequest.setVideoId(videoId);
            requestsStorage.addRequest(chatId, userRequest);
            log.info("Put request to map with chatId: " + chatId + " videoId: " + videoId);
            SendMessage choseFormatMessage = replyMessageService.getReplyText(chatId, CHOSE_FORMAT);
            choseFormatMessage.setReplyMarkup(telegramFacade.createBlockButtons(telegramFacade.getMediaFormats()));
            execute(choseFormatMessage);
        }
    }

    private void processCallBack(Long chatId, @NotNull String message, Request userRequest, String userName) throws TelegramApiException {
        switch (message) {
            case "mp3" -> {
                log.info("Callback mp3 from chatId: " + chatId);
                userRequest.setFormat(ContentType.mp3);
                userRequest.setQualityVideo(MP3);
                requestsStorage.updateRequest(chatId, userRequest);
                sendFileInFormat(chatId, MP3_QUALITY_CODE, userRequest, userName);
            }
            case "mp4" -> {
                log.info("Callback mp4 from chatId: " + chatId);
                SendMessage choseFormatMessage = replyMessageService.getReplyText(chatId, CHOSE_QUALITY);
                userRequest.setFormat(ContentType.mp4);
                requestsStorage.updateRequest(chatId, userRequest);
                choseFormatMessage.setReplyMarkup(telegramFacade.createBlockButtons(telegramFacade.getVideoFormatsButtons()));
                execute(choseFormatMessage);
            }
            case "360p" -> {
                log.info("Callback 360p from chatId: " + chatId);
                userRequest.setQualityVideo(MP4_360);
                sendFileInFormat(chatId, MP4_360_QUALITY_CODE, userRequest, userName);
            }
            case "720p" -> {
                log.info("Callback 720p from chatId: " + chatId);
                userRequest.setQualityVideo(MP4_720);
                sendFileInFormat(chatId, MP4_720_QUALITY_CODE, userRequest, userName);
            }
        }
    }

    private void sendFileInFormat(Long chatId, String code, Request userRequest, String userName) throws TelegramApiException {
        try {
            if (requestsStorage.getCurrentRequest(chatId) == null) {
                throw new RuntimeException();
            }
            if (!checkIfFileAlreadyExist(chatId, userRequest, userName)) {
                log.info("Video with id: " + userRequest.getVideoId() + " doesn`t exist in db, start creating");
                userRequest.setQualityCode(code);
                userRequest.setProcessing(true);

                requestsStorage.updateRequest(chatId, userRequest);
                execute(replyMessageService.getReplyMessage(chatId, PREPARE_TO_LOAD));
                execute(replyMessageService.getReplyMessage(chatId, BEGIN_LOADING));
                execute(replyMessageService.getReplyMessage(chatId, WAIT_DOWNLOAD));
                Response userResponse = responseProcessor.processResponse(userRequest);
                execute(replyMessageService.getReplyMessage(chatId, SEND_TO_TELEGRAM));

                log.info("Begin loading file with id: " + userRequest.getVideoId() + " format: " + userRequest.getFormat() + " quality: " + userRequest.getQualityCode());

                if (folderManagerService.getFileSize(userRequest) >= MAX_UPLOADED_FILE_SIZE) {
                    execute(replyMessageService.getReplyMessage(chatId, FILE_IS_TOO_BIG));
                    folderManagerService.clean(userRequest, true);
                    throw new YouTubeSizeException("Большой размер файла для отправки в телеграмм!");
                }
                uploadFileInTelegram(chatId, userRequest, userResponse);
                folderManagerService.clean(userRequest, false);
                execute(replyMessageService.getReplyMessageUsername(chatId, String.format(DONE, userName)));

            }
        } catch (YouTubeSizeException exception) {
            log.info("Size Can`t execute format mp4 for video : " + userRequest.getVideoId() + " with quality " + userRequest.getQualityCode());
            folderManagerService.clean(userRequest, false);
            execute(replyMessageService.getReplyMessage(chatId, ERROR_SIZE_TRY_AGAIN));

        } catch (RuntimeException e) {
            try {
                log.info("Can`t execute format mp4 for video : " + userRequest.getVideoId() + " with quality " + userRequest.getQualityCode());
                folderManagerService.clean(userRequest, false);
                if (requestsStorage.getCurrentRequest(chatId).isProcessing())
                    execute(replyMessageService.getReplyMessage(chatId, CHOSE_ANOTHER_FORMAT));
            } catch (TelegramApiException telegramApiException) {
                telegramApiException.printStackTrace();
            }
        }
        requestsStorage.removeRequest(chatId);
        userTelegramService.changeBotStateByChatId(chatId, READY);
        log.info("Change bot state to READY for chatId: " + chatId);
    }

    private void uploadFileInTelegram(Long chatId, @NotNull Request userRequest, Response userResponse) throws TelegramApiException {
        String telegramFileId;
        if (userRequest.getFormat().equals(ContentType.mp3)) {
            SendAudio audio = new SendAudio();
            audio.setChatId(chatId);
            InputFile inputFile = folderManagerService.getInputFile(userResponse, "m4a");
            audio.setAudio(inputFile);
            telegramFileId = execute(audio).getAudio().getFileId();
        } else {
            SendVideo video = new SendVideo();
            video.setChatId(chatId);
            InputFile inputFile = folderManagerService.getInputFile(userResponse, "mp4");
            video.setVideo(inputFile);
            telegramFileId = execute(video).getVideo().getFileId();
        }
        log.info("Load file with id: " + userRequest.getVideoId() +
                " format: " + userRequest.getFormat() + " quality: " + userRequest.getQualityCode());
        UploadedFileCreateDto uploadedFileCreateDto =
                new UploadedFileCreateDto(
                        userResponse.getName(),
                        telegramFileId,
                        userResponse.getContentType(),
                        userResponse.getQualityVideo());
        uploadedFileService.createFile(uploadedFileCreateDto);
        log.info("Save file with id: " + userRequest.getVideoId() +
                " format: " + userRequest.getFormat() + " quality: " + userRequest.getQualityCode());

    }

    private boolean checkIfFileAlreadyExist(Long chatId, @NotNull Request userRequest, String userName) throws TelegramApiException {
        Optional<UploadedFileReadDto> maybeUploadedFile = uploadedFileService.getFileByVideoIdAndType(userRequest.getVideoId(), userRequest.getFormat(), userRequest.getQualityVideo());

        if (maybeUploadedFile.isPresent()) {
            var uploadedFile = maybeUploadedFile.get();
            log.info("Finding file in db with: " + userRequest.getVideoId() + " format: " + userRequest.getFormat() + " quality: " + userRequest.getQualityCode());

            execute(replyMessageService.getReplyMessage(chatId, BEGIN_LOADING));
            execute(replyMessageService.getReplyMessage(chatId, SEND_TO_TELEGRAM));
            try {
                if (uploadedFile.getType() == ContentType.mp3) {
                    SendAudio audio = new SendAudio();
                    audio.setChatId(chatId);
                    var telegramFileId = uploadedFile.getTelegramFileId();
                    InputFile inputFile = new InputFile();
                    inputFile.setMedia(telegramFileId);
                    audio.setAudio(inputFile);
                    execute(audio);
                } else {
                    SendVideo video = new SendVideo();
                    video.setChatId(chatId);
                    var telegramFileId = uploadedFile.getTelegramFileId();
                    InputFile inputFile = new InputFile();
                    inputFile.setMedia(telegramFileId);
                    video.setVideo(inputFile);
                    execute(video);
                }
            } catch (TelegramApiException e) {
                uploadedFileService.deleteFile(uploadedFile.getTelegramFileId());
                return false;
            }
            log.info("Load file in telegram file with id: " + userRequest.getVideoId() + " format: " + userRequest.getFormat() + " quality: " + userRequest.getQualityCode());
            execute(replyMessageService.getReplyMessageUsername(chatId, String.format(DONE, userName)));
            return true;
        }
        return false;
    }

    public void deleteMessage(long chatId, int messageId) {
        try {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(messageId);
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
