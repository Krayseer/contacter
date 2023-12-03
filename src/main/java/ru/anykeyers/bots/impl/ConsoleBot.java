package ru.anykeyers.bots.impl;

import ru.anykeyers.bots.Bot;
import ru.anykeyers.bots.BotType;
import ru.anykeyers.contexts.ApplicationProperties;
import ru.anykeyers.contexts.Messages;
import ru.anykeyers.domain.User;
import ru.anykeyers.factories.RepositoryFactory;
import ru.anykeyers.processors.MessageProcessor;
import ru.anykeyers.repositories.UserRepository;
import ru.anykeyers.services.AuthenticationService;
import ru.anykeyers.services.impl.AuthenticationServiceImpl;

import java.util.Scanner;

/**
 * Консольный бот
 */
public class ConsoleBot implements Bot {

    private final Messages messages;

    private final MessageProcessor messageProcessor;

    /**
     * Имя пользователя, обрабатываемого в консоли
     */
    private final String username;

    public ConsoleBot() {
        RepositoryFactory repositoryFactory = new RepositoryFactory();
        UserRepository userRepository = repositoryFactory.createUserRepository();
        AuthenticationService authenticationService = new AuthenticationServiceImpl(userRepository);
        ApplicationProperties applicationProperties = new ApplicationProperties();
        messages = new Messages();
        messageProcessor = new MessageProcessor(this, authenticationService);

        username = applicationProperties.getSetting("console.username");
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
     * Обработать сообщение
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
