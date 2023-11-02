package ru.anykeyers.commands;

import ru.anykeyers.repositories.UserRepository;
import ru.anykeyers.services.AuthenticationService;
import ru.anykeyers.services.ConsoleService;

import java.util.HashMap;
import java.util.Map;

import static ru.anykeyers.commands.Command.*;

/**
 * Класс отвечает за обработку команд, вводимых пользователем, и управление соответствующими обработчиками команд
 */
public class CommandProcessor {

    private final Map<Command, CommandHandler> commandHandlers;

    private final AuthenticationService authenticationService;

    private final UserRepository userRepository;

    private final ConsoleService consoleService;

    public CommandProcessor(AuthenticationService authenticationService,
                            UserRepository userRepository,
                            ConsoleService consoleService) {
        this.commandHandlers = new HashMap<>();
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
        this.consoleService = consoleService;
        registerCommands();
    }

    /**
     * Обрабатывает введенную команду, вызывая соответствующий обработчик команды, если он существует.
     * @param commandValue введенная пользователем команда.
     */
    public void processCommand(String commandValue) {
        Command command = Command.getCommandByValue(commandValue);
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
        commandHandlers.put(HELP, consoleService::writeCommands);
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
            userRepository.saveAll();
            System.exit(0);
        });
    }

}
