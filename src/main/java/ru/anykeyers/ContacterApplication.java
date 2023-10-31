package ru.anykeyers;

import ru.anykeyers.commands.CommandProcessor;
import ru.anykeyers.services.AuthenticationService;
import ru.anykeyers.services.ConsoleService;

/**
 * Класс, позволяющий запустить приложение
 */
public class ContacterApplication {

    private final ConsoleService consoleService;

    private final CommandProcessor commandProcessor;

    public ContacterApplication() {
        ApplicationProperties applicationProperties = new ApplicationProperties();
        this.consoleService = new ConsoleService();
        AuthenticationService authenticationService = new AuthenticationService(consoleService, applicationProperties);
        this.commandProcessor = new CommandProcessor(authenticationService);
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
