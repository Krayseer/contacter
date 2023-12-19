package ru.anykeyers.bot.impl;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.anykeyers.bot.Bot;
import ru.anykeyers.bot.BotType;
import ru.anykeyers.common.ApplicationProperties;
import ru.anykeyers.common.Messages;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.factory.RepositoryFactory;
import ru.anykeyers.processor.MessageProcessor;
import ru.anykeyers.processor.command.Command;
import ru.anykeyers.repository.UserRepository;
import ru.anykeyers.service.AuthenticationService;
import ru.anykeyers.service.UserStateService;
import ru.anykeyers.service.impl.AuthenticationServiceImpl;
import ru.anykeyers.service.impl.UserStateServiceImpl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Телеграм бот
 */
public class TelegramBot extends TelegramLongPollingBot implements Bot {

    private final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    private final Messages messages = Messages.getInstance();

    private final MessageProcessor messageProcessor;

    private final AuthenticationService authenticationService;

    public TelegramBot() {
        RepositoryFactory repositoryFactory = new RepositoryFactory();
        UserRepository userRepository = repositoryFactory.createUserRepository();
        authenticationService = new AuthenticationServiceImpl(userRepository);
        UserStateService userStateService = new UserStateServiceImpl();
        messageProcessor = new MessageProcessor(this, authenticationService, userStateService, repositoryFactory);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            org.telegram.telegrambots.meta.api.objects.User telegramUser = message.getFrom();
            String username = telegramUser.getUserName() != null
                    ? telegramUser.getUserName()
                    : telegramUser.getId().toString();
            Long chatId = message.getChatId();
            String text;
            if (message.hasDocument()) {
                java.io.File file = copyFile(message.getDocument());
                text = file.getPath();
            } else if (message.hasText()) {
                String messageText = message.getText();
                if(messageText.equals("/start")) {
                    initUser(username, chatId);
                    String welcomeMessage = messages.getMessageByKey("bot.welcome");
                    sendText(chatId, welcomeMessage);
                    setCommands();
                    return;
                }
                text = messageText;
            } else {
                String errorMessage = messages.getMessageByKey("bot.telegram.exception.invalid-request-message");
                throw new RuntimeException(errorMessage);
            }
            handleMessage(username, text);
        }
    }

    @Override
    public String getBotToken() {
        return applicationProperties.getSettingByKey("telegram.token");
    }

    @Override
    public String getBotUsername() {
        return applicationProperties.getSettingByKey("telegram.username");
    }

    @Override
    public void sendFile(Long chatId, File file) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId);
        sendDocument.setDocument(new InputFile(file));
        try {
            execute(sendDocument);
        } catch (TelegramApiException e) {
            String errorMessage = messages.getMessageByKey("bot.telegram.exception.send-error");
            throw new RuntimeException(errorMessage);
        }
    }

    @Override
    public void sendText(Long chatId, String message) {
        SendMessage msg = SendMessage.builder().chatId(chatId).text(message).build();
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            String errorMessage = messages.getMessageByKey("bot.telegram.exception.send-error");
            throw new RuntimeException(errorMessage);
        }
    }

    /**
     * Отправить сообщение на обработку
     *
     * @param username имя пользователя
     * @param message сообщение
     */
    private void handleMessage(String username, String message) {
        messageProcessor.processMessage(username, message, BotType.TELEGRAM_BOT);
    }

    /**
     * Регистрация пользователя
     *
     * @param username имя пользователя
     * @param chatId идентификатор чата
     */
    private void initUser(String username, Long chatId) {
        User user = authenticationService.existsUserByUsernameAndBotType(username, BotType.TELEGRAM_BOT)
                ? authenticationService.getUserByUsernameAndBotType(username, BotType.TELEGRAM_BOT)
                : new User(username, BotType.TELEGRAM_BOT);
        user.setChatId(chatId);
        authenticationService.saveOrUpdateUser(user);
    }

    /**
     * Создать копию файла из документа
     *
     * @param document принимаемый документ
     */
    private File copyFile(Document document) {
        String fileId = document.getFileId();
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        String format = document.getFileName().split("\\.")[1];
        try {
            org.telegram.telegrambots.meta.api.objects.File file = execute(getFile);
            java.io.File downloadedFile = downloadFile(file);
            Path tempFilePath = Files.createTempFile("temp_", String.format(".%s", format));
            Files.copy(downloadedFile.toPath(), tempFilePath, StandardCopyOption.REPLACE_EXISTING);
            return tempFilePath.toFile();
        } catch (Exception e) {
            String errorMessage = messages.getMessageByKey("bot.telegram.exception.copy-file-error");
            throw new RuntimeException(errorMessage);
        }
    }

    /**
     * Установить в меню возможные команды в приложении
     */
    private void setCommands() {
        SetMyCommands setMyCommands = new SetMyCommands();
        List<BotCommand> commands = new ArrayList<>();
        for (Command command : Command.values()) {
            BotCommand botCommand = new BotCommand(
                    command.getCommandValue(),
                    messages.getMessageByKey(command.getDescriptionKey())
            );
            commands.add(botCommand);
        }
        setMyCommands.setCommands(commands);
        try {
            execute(setMyCommands);
        } catch (TelegramApiException e) {
            String errorMessage = messages.getMessageByKey("bot.telegram.exception.set-commands-error");
            throw new RuntimeException(errorMessage);
        }
    }

}