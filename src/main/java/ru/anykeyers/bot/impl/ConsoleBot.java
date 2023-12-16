package ru.anykeyers.bot.impl;

import ru.anykeyers.bot.Bot;
import ru.anykeyers.bot.BotType;
import ru.anykeyers.common.ApplicationProperties;
import ru.anykeyers.common.Messages;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.factory.RepositoryFactory;
import ru.anykeyers.processor.MessageProcessor;
import ru.anykeyers.repository.UserRepository;
import ru.anykeyers.service.AuthenticationService;
import ru.anykeyers.service.UserStateService;
import ru.anykeyers.service.impl.AuthenticationServiceImpl;
import ru.anykeyers.service.impl.UserStateServiceImpl;

import java.util.Scanner;

/**
 * Консольный бот
 */
public class ConsoleBot implements Bot {

    private final Messages messages = Messages.getInstance();

    private final MessageProcessor messageProcessor;

    /**
     * Имя пользователя, обрабатываемого в консоли
     */
    private final String username;

    public ConsoleBot() {
        RepositoryFactory repositoryFactory = new RepositoryFactory();
        UserRepository userRepository = repositoryFactory.createUserRepository();
        AuthenticationService authenticationService = new AuthenticationServiceImpl(userRepository);
        UserStateService userStateService = new UserStateServiceImpl();
        messageProcessor = new MessageProcessor(this, authenticationService, userStateService, repositoryFactory);

        ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
        username = applicationProperties.getSettingByKey("console.username");
        if (!authenticationService.existsUserByUsernameAndBotType(username, BotType.CONSOLE)) {
            User user = new User(username, BotType.CONSOLE);
            user.setChatId(0L);
            authenticationService.saveOrUpdateUser(user);
        }

        start();
    }

    @Override
    public void sendMessage(Long chatId, String message) {
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
    private void start() {
        String welcomeMessage = messages.getMessageByKey("bot.welcome");
        System.out.println(welcomeMessage);
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String message = scanner.nextLine();
                handleMessage(username, message);
            }
        }
    }

}
