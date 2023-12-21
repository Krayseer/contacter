package ru.anykeyers.bot.impl;

import ru.anykeyers.bot.Bot;
import ru.anykeyers.bot.BotType;
import ru.anykeyers.common.ApplicationProperties;
import ru.anykeyers.common.Messages;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.exception.FileReadException;
import ru.anykeyers.factory.RepositoryFactory;
import ru.anykeyers.processor.MessageProcessor;
import ru.anykeyers.repository.UserRepository;
import ru.anykeyers.service.AuthenticationService;
import ru.anykeyers.service.UserStateService;
import ru.anykeyers.service.impl.AuthenticationServiceImpl;
import ru.anykeyers.service.impl.UserStateServiceImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

/**
 * Консольный бот
 */
public class ConsoleBot implements Bot {

    ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    private final Messages messages = Messages.getInstance();

    private final MessageProcessor messageProcessor;

    private final AuthenticationService authenticationService;

    public ConsoleBot() {
        RepositoryFactory repositoryFactory = new RepositoryFactory();
        UserRepository userRepository = repositoryFactory.createUserRepository();
        authenticationService = new AuthenticationServiceImpl(userRepository);
        UserStateService userStateService = new UserStateServiceImpl();
        messageProcessor = new MessageProcessor(this, authenticationService, userStateService, repositoryFactory);
        ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
        String username = applicationProperties.getSettingByKey("console.username");
        start(username);
    }

    /**
     * Файл копируется локально, путь выбирается по ключу "import_export.path" из настроек
     * Пользователю отправляется только путь до файла
     *
     * @param chatId идентификатор чата
     * @param file файл
     */
    @Override
    public void sendFile(Long chatId, File file) {
        String dirPath = applicationProperties.getSettingByKey("import_export.path");
        File copyFile = new File(dirPath + file.getName());
        try {
            Files.copy(file.toPath(), copyFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileReadException(file.getPath());
        }
        String message = messages.getMessageByKey("import_export.export.successful", copyFile.getAbsolutePath());
        System.out.println(message);
    }

    @Override
    public void sendText(Long chatId, String message) {
        System.out.println(message);
    }

    /**
     * Отправить сообщение на обработку
     *
     * @param username имя пользователя
     * @param message сообщение
     */
    private void handleMessage(String username, String message) {
        messageProcessor.processMessage(username, message, BotType.CONSOLE);
    }

    /**
     * Запуск консольного бота
     */
    private void start(String username) {
        initUser(username);
        String welcomeMessage = messages.getMessageByKey("bot.welcome");
        System.out.println(welcomeMessage);
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String message = scanner.nextLine();
                handleMessage(username, message);
            }
        }
    }

    /**
     * Проинициализировать пользователя
     *
     * @param username имя пользователя
     */
    private void initUser(String username) {
        if (authenticationService.existsUserByUsernameAndBotType(username, BotType.CONSOLE)) {
            return;
        }
        User user = new User(username, BotType.CONSOLE);
        user.setChatId(0L);
        authenticationService.saveOrUpdateUser(user);
    }

}
