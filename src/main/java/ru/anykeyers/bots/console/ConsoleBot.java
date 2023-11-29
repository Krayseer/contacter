package ru.anykeyers.bots.console;

import ru.anykeyers.bots.Bot;
import ru.anykeyers.contexts.ApplicationProperties;
import ru.anykeyers.contexts.Messages;
import ru.anykeyers.domain.User;
import ru.anykeyers.factories.RepositoryFactory;
import ru.anykeyers.processors.commands.CommandProcessor;
import ru.anykeyers.repositories.UserRepository;
import ru.anykeyers.services.AuthenticationService;

import java.util.Scanner;

import static ru.anykeyers.bots.BotType.CONSOLE;

/**
 * Консольный бот
 */
public class ConsoleBot implements Bot {

    private final Messages messages;

    private final CommandProcessor commandProcessor;

    /**
     * Имя пользователя, обрабатываемого в консоли
     */
    private final String username;

    public ConsoleBot() {
        RepositoryFactory repositoryFactory = new RepositoryFactory();
        UserRepository userRepository = repositoryFactory.createUserRepository();
        AuthenticationService authenticationService = new AuthenticationService(userRepository);

        ApplicationProperties applicationProperties = new ApplicationProperties();
        username = applicationProperties.getSetting("console.username");
        if (!authenticationService.existsUser(username)) {
            User user = new User(username);
            authenticationService.saveOrUpdateUser(user);
        }

        commandProcessor = new CommandProcessor(this, authenticationService);
        messages = new Messages();
    }

    @Override
    public void sendMessage(Long chatId, String message) {
        System.out.println(message);
    }

    @Override
    public void receiveMessage(String username, String message) {
        commandProcessor.processMessage(username, message, CONSOLE);
    }

    @Override
    public void start() {
        System.out.println(messages.getMessageByKey("application.welcome"));
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String message = scanner.nextLine();
                receiveMessage(username, message);
            }
        }
    }

}
