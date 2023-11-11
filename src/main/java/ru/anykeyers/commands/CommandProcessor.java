package ru.anykeyers.commands;

import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.repositories.GroupRepository;
import ru.anykeyers.repositories.FileDBRepository;
import ru.anykeyers.repositories.UserRepository;
import ru.anykeyers.services.AuthenticationService;
import ru.anykeyers.services.ConsoleService;
import ru.anykeyers.services.ContactService;
import ru.anykeyers.services.GroupService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.anykeyers.commands.Command.*;

/**
 * Класс отвечает за обработку команд, вводимых пользователем, и управление соответствующими обработчиками команд
 */
public class CommandProcessor {

    private final AuthenticationService authenticationService;

    private final ContactService contactService;

    private final GroupService groupService;

    private final GroupRepository groupRepository;

    private final UserRepository userRepository;

    private final ConsoleService consoleService;

    private final ContactRepository contactRepository;

    private final Map<Command, CommandHandler> commandHandlers;

    public CommandProcessor(AuthenticationService authenticationService,
                            ContactService contactService,
                            GroupService groupService,
                            GroupRepository groupRepository,
                            ContactRepository contactRepository,
                            UserRepository userRepository) {
        this.authenticationService = authenticationService;
        this.contactService = contactService;
        this.groupService = groupService;
        this.groupRepository = groupRepository;
        this.contactRepository = contactRepository;
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
        commandHandlers.put(ADD_CONTACT, contactService::addContact);
        commandHandlers.put(EDIT_CONTACT, contactService::editContact);
        commandHandlers.put(DELETE_CONTACT, contactService::deleteContact);
        commandHandlers.put(ADD_GROUP, groupService::addGroup);
        commandHandlers.put(EDIT_GROUP, groupService::editGroup);
        commandHandlers.put(DELETE_GROUP, groupService::deleteGroup);
        commandHandlers.put(HELP, consoleService::writeCommands);
        commandHandlers.put(EXIT_APP, () -> {
            List<FileDBRepository> repositories = List.of(userRepository, contactRepository, groupRepository);
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
