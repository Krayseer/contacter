package ru.anykeyers.commands;

import ru.anykeyers.contexts.Messages;
import ru.anykeyers.services.AuthenticationService;
import ru.anykeyers.services.ContactService;
import ru.anykeyers.services.GroupService;

import java.util.HashMap;
import java.util.Map;

import static ru.anykeyers.commands.Command.*;

/**
 * Класс отвечает за обработку команд, вводимых пользователем, и управление соответствующими обработчиками команд
 */
public class CommandProcessor {

    private final AuthenticationService authenticationService;

    private final ContactService contactService;

    private final GroupService groupService;

    private final Map<Command, CommandHandler> commandHandlers;

    private final Messages messages;

    public CommandProcessor(AuthenticationService authenticationService,
                            ContactService contactService,
                            GroupService groupService) {
        this.authenticationService = authenticationService;
        this.contactService = contactService;
        this.groupService = groupService;

        messages = new Messages();
        commandHandlers = new HashMap<>();
        registerCommands();
    }

    /**
     * Обрабатывает введенную команду, вызывая соответствующий обработчик команды.<br/>
     * Перед обработкой команды происходит валидация для проверки, нужна ли авторизация для выполнения орбаботчика команды,
     * и существует ли команда в списке зарегистрированных.
     * @param message введенная пользователем команда с аргументами
     */
    public String processCommand(String message) {
        int spaceIndex = message.indexOf(' ');
        if (spaceIndex == -1) {
            return processCommandInternal(message, null);
        }
        String commandValue = message.substring(0, spaceIndex);
        String commandArguments = message.substring(spaceIndex + 1);
        return processCommandInternal(commandValue, commandArguments);
    }

    private String processCommandInternal(String commandValue, String commandArguments) {
        Command command = Command.getCommandByValue(commandValue);
        CommandHandler commandHandler = command != null
                ? commandHandlers.get(command)
                : null;
        String[] args = commandArguments != null
                ? commandArguments.trim().replace(" ", "").split(",")
                : null;

        if (commandHandler == null) {
            return messages.getMessageByKey("command.not-exists");
        } else if (command.isNeedAuthenticate() && !authenticationService.isAuthenticated()) {
            return messages.getMessageByKey("auth.need-authenticate");
        } else if (!command.isNeedParameters() && args != null){
            return messages.getMessageByKey("command.not-exists");
        } else if (command.isNeedParameters() && args == null || args != null && args.length != command.getParametersLength()) {
            return messages.getMessageByKey("command.need-arguments", command.getParameters());
        }

        return commandHandler.handleCommand(commandArguments);
    }

    /**
     * Регистрация обработчиков команд
     */
    private void registerCommands() {
        registerCommonCommands();
        registerAuthenticationCommands();
        registerContactCommands();
        registerGroupCommands();
    }

    private void registerAuthenticationCommands() {
        commandHandlers.put(LOG_IN, authenticationService::authenticate);
        commandHandlers.put(LOG_OUT, (args) -> authenticationService.logoutUser());
    }

    private void registerContactCommands() {
        commandHandlers.put(ADD_CONTACT, (contactString) -> {
            // TODO: 22.11.2023
            return null;
        });
        commandHandlers.put(EDIT_CONTACT_NAME, (nameAndNewContactName) -> {
            // TODO: 22.11.2023
            return null;
        });
        commandHandlers.put(EDIT_CONTACT_PHONE, (nameAndNewContactPhoneNumber) -> {
            // TODO: 22.11.2023
            return null;
        });
        commandHandlers.put(DELETE_CONTACT_BY_NAME, (contactName) -> {
            // TODO: 22.11.2023
            return null;
        });
        commandHandlers.put(DELETE_CONTACT_BY_PHONE, (contactPhone) -> {
            // TODO: 22.11.2023
            return null;
        });
    }

    private void registerGroupCommands() {
        commandHandlers.put(ADD_GROUP, (groupName) -> {
            // TODO: 22.11.2023
            return null;
        });
        commandHandlers.put(EDIT_GROUP_NAME, (nameAndNewGroupName) -> {
            // TODO: 22.11.2023
            return null;
        });
        commandHandlers.put(GROUP_ADD_CONTACT, (groupAndContactNames) -> {
            // TODO: 22.11.2023
            return null;
        });
        commandHandlers.put(GROUP_DELETE_CONTACT, (groupNameAndContactNameToDelete) -> {
            // TODO: 22.11.2023
            return null;
        });
        commandHandlers.put(DELETE_GROUP, (groupNameToDelete) -> {
            // TODO: 22.11.2023
            return null;
        });
    }

    private void registerCommonCommands() {
        commandHandlers.put(HELP, (args) -> {
            // TODO: 22.11.2023
            return null;
        });
        commandHandlers.put(EXIT_APP, (args) -> {
            System.exit(0);
            return messages.getMessageByKey("application.exit");
        });
    }

}
