package ru.anykeyers;

import ru.anykeyers.commands.CommandProcessor;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.repositories.GroupRepository;
import ru.anykeyers.repositories.UserRepository;
import ru.anykeyers.services.AuthenticationService;
import ru.anykeyers.services.ConsoleService;
import ru.anykeyers.services.ContactService;
import ru.anykeyers.services.GroupService;

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
        GroupRepository groupRepository = new GroupRepository(applicationProperties.getSetting("group-file-path"));
        ContactRepository contactRepository = new ContactRepository(applicationProperties.getSetting("contacts-file-path"));
        consoleService = new ConsoleService();
        AuthenticationService authenticationService = new AuthenticationService(userRepository);
        ContactService contactService = new ContactService(contactRepository, authenticationService);
        GroupService groupService = new GroupService(authenticationService, contactService, groupRepository);
        commandProcessor = new CommandProcessor(authenticationService, contactService, groupService, groupRepository, contactRepository, userRepository);
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
