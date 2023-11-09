package ru.anykeyers.commands;

import ru.anykeyers.repositories.FileDBRepository;
import ru.anykeyers.repositories.UserRepository;
import ru.anykeyers.services.AuthenticationService;
import ru.anykeyers.services.ConsoleService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.anykeyers.commands.Command.*;

/**
 * Класс отвечает за обработку команд, вводимых пользователем, и управление соответствующими обработчиками команд
 */
public class CommandProcessor {

    private final AuthenticationService authenticationService;

    private final UserRepository userRepository;

    private final ConsoleService consoleService;

    private final Map<Command, CommandHandler> commandHandlers;

    public CommandProcessor(AuthenticationService authenticationService,
                            UserRepository userRepository) {
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;

        commandHandlers = new HashMap<>();
        consoleService = new ConsoleService();

        registerCommands();
    }

    /**
     * Обрабатывает введенную команду, вызывая соответствующий обработчик команды.<br/>
     * Перед обработкой команды происходит валидация для проверки, нужна ли авторизация для выполнения орбаботчика команды,
     * и существует ли команда в списке зарегистрированных.
     * @param commandValue введенная пользователем команда.
     */
    public void processCommand(String commandValue) {
        Command command = Command.getCommandByValue(commandValue);
        CommandHandler commandHandler = commandHandlers.get(command);
        if(commandHandler == null) {
            System.out.println("Такой команды не существует, введите /help для просмтора возможных задач");
            return;
        }
        if(!isCommandValid(command)) {
            System.out.println("Необходимо авторизоваться");
            return;
        }
        commandHandler.handleCommand();
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
            List<FileDBRepository> repositories = List.of(userRepository);
            repositories.forEach(FileDBRepository::saveAll);
            System.exit(0);
        });
    }

    /**
     * Проверить команду на валидность
     */
    private boolean isCommandValid(Command command) {
        return command != null && (!command.isNeedAuthenticate() || authenticationService.isAuthenticated());
    }

}
