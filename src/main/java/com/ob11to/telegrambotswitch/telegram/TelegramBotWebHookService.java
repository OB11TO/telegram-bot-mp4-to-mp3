package com.ob11to.telegrambotswitch.telegram;

import com.ob11to.telegrambotswitch.cache.RequestsStorage;
import com.ob11to.telegrambotswitch.config.TelegramBotConfig;
import com.ob11to.telegrambotswitch.dto.Request;
import com.ob11to.telegrambotswitch.dto.Response;
import com.ob11to.telegrambotswitch.dto.UploadedFileCreateDto;
import com.ob11to.telegrambotswitch.dto.UploadedFileReadDto;
import com.ob11to.telegrambotswitch.dto.UserTelegramReadDto;
import com.ob11to.telegrambotswitch.entity.ContentType;
import com.ob11to.telegrambotswitch.service.FolderManagerService;
import com.ob11to.telegrambotswitch.service.ReplyMessageService;
import com.ob11to.telegrambotswitch.service.ResponseProcessor;
import com.ob11to.telegrambotswitch.service.UploadedFileService;
import com.ob11to.telegrambotswitch.service.UserInputParser;
import com.ob11to.telegrambotswitch.service.UserTelegramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
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
import static com.ob11to.telegrambotswitch.util.MessageResponse.FILE_FOUND;
import static com.ob11to.telegrambotswitch.util.MessageResponse.HELP;
import static com.ob11to.telegrambotswitch.util.MessageResponse.INFO;
import static com.ob11to.telegrambotswitch.util.MessageResponse.INFO_AFTER_STOP;
import static com.ob11to.telegrambotswitch.util.MessageResponse.INVALID_INPUT;
import static com.ob11to.telegrambotswitch.util.MessageResponse.PREPARE_TO_LOAD;
import static com.ob11to.telegrambotswitch.util.MessageResponse.SEND_LINK;
import static com.ob11to.telegrambotswitch.util.MessageResponse.SEND_TO_TELEGRAM;
import static com.ob11to.telegrambotswitch.util.MessageResponse.START;
import static com.ob11to.telegrambotswitch.util.MessageResponse.STOP_DOWNLOAD;
import static com.ob11to.telegrambotswitch.util.MessageResponse.WAIT;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBotWebHookService extends TelegramWebhookBot {

    private final static double MAX_UPLOADED_FILE_SIZE = 50;  //TODO сделать проверку на максимальный размер файла
    private final static String MP4_360_QUALITY_CODE = "18";
    private final static String MP4_720_QUALITY_CODE = "136+140";
    private final static String MP3_QUALITY_CODE = "140";

    private static final File PATH = new File("/home/obiito/c/youtube/test"); //test

    private final TelegramBotConfig config;
    private final UserTelegramService userTelegramService;
    private final ReplyMessageService replyMessageService;
    private final UserInputParser userInputParser;
    private final TelegramFacade telegramFacade;
    private final RequestsStorage requestsStorage;
    private final UploadedFileService uploadedFileService;
    private final ResponseProcessor responseProcessor;
    private final FolderManagerService folderManagerService;

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

    private void processUpdate(UserTelegramReadDto userTelegram, Message message) {
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
                botIsReadyToProcessUrl(userTelegram.getChatId(), message);
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

    private void processCallBack(Long chatId, String message, Request userRequest, String userName) throws TelegramApiException {
        switch (message) {
            case "mp3" -> {
                log.info("Callback mp3 from chatId: " + chatId);
                userRequest.setFormat(ContentType.mp3);
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
                sendFileInFormat(chatId, MP4_360_QUALITY_CODE, userRequest, userName);
            }
            case "720p" -> {
                log.info("Callback 720p from chatId: " + chatId);
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
                Response userResponse = responseProcessor.processResponse(userRequest);
                execute(replyMessageService.getReplyMessage(chatId, SEND_TO_TELEGRAM));

                log.info("Begin loading file with id: " + userRequest.getVideoId() + " format: " + userRequest.getFormat() + " quality: " + userRequest.getQualityCode());

              /*  if (mediaCleanerService.getFileSize(userRequest) >= MAX_UPLOADED_FILE_SIZE) {
                    execute(messageService.getReplyMessage(chatId, FILE_IS_TOO_BIG));
                    mediaCleanerService.clean(userRequest, true);
                    throw new RuntimeException();
                }*/
                uploadFileInTelegram(chatId, userRequest, userResponse);
//                mediaCleanerService.clean(userRequest, false);
                execute(replyMessageService.getReplyMessage(chatId, String.format(DONE, userName)));

            }
        } catch (RuntimeException e) {
            try {
                //TODO mediaCleanerService.clean(userRequest, true);
                log.info("Can`t execute format mp4 for video : " + userRequest.getVideoId() + " with quality " + userRequest.getQualityCode());
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

    private void uploadFileInTelegram(Long chatId, Request userRequest, Response userResponse) throws TelegramApiException {
        String telegramFileId;
        if (userRequest.getFormat().equals(ContentType.mp4)) {
            SendVideo video = new SendVideo();
            video.setChatId(chatId);
            InputFile inputFile = getInputFile(userResponse, "mp4");
            video.setVideo(inputFile);
            telegramFileId = execute(video).getVideo().getFileId();
        } else {
            SendAudio audio = new SendAudio();
            audio.setChatId(chatId);
            InputFile inputFile = getInputFile(userResponse, "m4a");
            audio.setAudio(inputFile);
            telegramFileId = execute(audio).getAudio().getFileId();
        }
        log.info("Load file with id: " + userRequest.getVideoId() +
                " format: " + userRequest.getFormat() + " quality: " + userRequest.getQualityCode());
        UploadedFileCreateDto uploadedFileCreateDto =
                new UploadedFileCreateDto(userResponse.getName(), telegramFileId, userResponse.getContentType());
        var file = uploadedFileService.createFile(uploadedFileCreateDto);
        log.info("Save file with id: " + userRequest.getVideoId() +
                " format: " + userRequest.getFormat() + " quality: " + userRequest.getQualityCode());

    }

    private InputFile getInputFile(Response userResponse, String type) {
        var name = userResponse.getName();
        File path = new File(folderManagerService.getPath());
        File[] matchingFiles = path.listFiles((dir, file) -> file.contains(name) && file.endsWith(type));
        Optional<File> maybeFile = Arrays.stream(Objects.requireNonNull(matchingFiles)).findFirst();
        var file = maybeFile.orElseThrow(() -> new RuntimeException("File is not found"));
        InputFile inputFile = new InputFile();
        inputFile.setMedia(file);
        return inputFile;
    }

    private boolean checkIfFileAlreadyExist(Long chatId, Request userRequest, String userName) throws TelegramApiException {
        Optional<UploadedFileReadDto> maybeUploadedFile = uploadedFileService.getFileByVideoIdAndType(userRequest.getVideoId(), userRequest.getFormat());

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
            execute(replyMessageService.getReplyMessage(chatId, String.format(DONE, userName)));
            return true;
        }
        return false;
    }

    private void botIsReadyToProcessUrl(Long chatId, Message message) throws TelegramApiException {
        userTelegramService.changeBotStateByChatId(chatId, BUSY);
        log.info("Change bot state to BUSY for chatId: " + chatId);

        String videoId = userInputParser.getYouTubeVideoId(message.getText());

        if (videoId == null) {
            execute(replyMessageService.getReplyMessage(chatId, INVALID_INPUT));
            log.info("Invalid url from user, input: " + message.getText());
            userTelegramService.changeBotStateByChatId(chatId, READY);
            log.info("Change bot state to READY for chatId: " + chatId);
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
