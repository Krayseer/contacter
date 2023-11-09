package ru.anykeyers;

import ru.anykeyers.commands.CommandProcessor;
import ru.anykeyers.repositories.UserRepository;
import ru.anykeyers.services.AuthenticationService;
import ru.anykeyers.services.ConsoleService;

/**
 * Класс, позволяющий запустить приложение
 */
public class ContacterApplication {

    private final ConsoleService consoleService;

    private final CommandProcessor commandProcessor;

    public ContacterApplication() {
        String propertiesFilePath = "src/main/resources/application.properties";
        ApplicationProperties applicationProperties = new ApplicationProperties(propertiesFilePath);
        UserRepository userRepository = new UserRepository(applicationProperties.getSetting("saved-users-file-path"));
        consoleService = new ConsoleService();
        AuthenticationService authenticationService = new AuthenticationService(userRepository);
        commandProcessor = new CommandProcessor(authenticationService, userRepository);
    }

    /**
     * Запуск приложения
     */
    public void start() {
        System.out.println("Добро пожаловать в менеджер контаков!");
        while (true) {
            String command = consoleService.readCommand();
            commandProcessor.processCommand(command);
        }
    }

}
