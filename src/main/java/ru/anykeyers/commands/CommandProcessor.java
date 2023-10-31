package ru.anykeyers.commands;

import ru.anykeyers.services.AuthenticationService;

import java.util.HashMap;
import java.util.Map;

import static ru.anykeyers.commands.CommandNames.*;

/**
 * Класс отвечает за обработку команд, вводимых пользователем, и управление соответствующими обработчиками команд
 */
public class CommandProcessor {

    private final Map<String, CommandHandler> commandHandlers;

    private final AuthenticationService authenticationService;

    public CommandProcessor(AuthenticationService authenticationService) {
        this.commandHandlers = new HashMap<>();
        this.authenticationService = authenticationService;
        registerCommands();
    }

    /**
     * Обрабатывает введенную команду, вызывая соответствующий обработчик команды, если он существует.
     * @param command введенная пользователем команда.
     */
    public void processCommand(String command) {
        CommandHandler commandHandler = commandHandlers.get(command);
        if(commandHandler != null) {
            commandHandler.handleCommand();
        }
    }

    /**
     * Регистрация обработчиков команд
     */
    private void registerCommands() {
        commandHandlers.put(LOG_IN, authenticationService::authenticate);
        commandHandlers.put(LOG_OUT, authenticationService::logoutUser);
        commandHandlers.put(ADD_CONTACT, () -> {
            // TODO: 31.10.2023
        });
        commandHandlers.put(EDIT_CONTACT, () -> {
            // TODO: 31.10.2023
        });
        commandHandlers.put(DELETE_CONTACT, () -> {
            // TODO: 31.10.2023
        });
        commandHandlers.put(ADD_GROUP, () -> {
            // TODO: 31.10.2023
        });
        commandHandlers.put(EDIT_GROUP, () -> {
            // TODO: 31.10.2023
        });
        commandHandlers.put(DELETE_GROUP, () -> {
            // TODO: 31.10.2023
        });
        commandHandlers.put(EXIT_APP, () -> {
            authenticationService.saveUsers();
            System.exit(0);
        });
    }

}
