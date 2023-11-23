package ru.anykeyers.commands;

import ru.anykeyers.contexts.Messages;
import ru.anykeyers.dataOperations.kinds.SortingKind;
import ru.anykeyers.services.AuthenticationService;
import ru.anykeyers.services.ContactService;
import ru.anykeyers.services.GroupService;
import ru.anykeyers.services.SortService;

import java.util.HashMap;
import java.util.Map;

import static ru.anykeyers.commands.Command.*;
import static ru.anykeyers.domain.Contact.Field.*;

/**
 * Класс отвечает за обработку команд, вводимых пользователем, и управление соответствующими обработчиками команд
 */
public class CommandProcessor {

    private final AuthenticationService authenticationService;

    private final ContactService contactService;

    private final GroupService groupService;

    private final SortService sortService;

    private final Map<Command, CommandHandler> commandHandlers;

    private final Messages messages;

    public CommandProcessor(AuthenticationService authenticationService,
                            ContactService contactService,
                            GroupService groupService,
                            SortService sortService) {
        this.authenticationService = authenticationService;
        this.contactService = contactService;
        this.groupService = groupService;
        this.sortService = sortService;

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
        commandHandlers.put(ADD_CONTACT, (contactString) ->
                contactService.addContact(authenticationService.getCurrentUser(), contactString)
        );
        commandHandlers.put(EDIT_CONTACT_NAME, (nameAndNewContactName) -> {
            String[] arguments = getArguments(nameAndNewContactName);
            String contactName = arguments[0];
            String newName = arguments[1];
            return contactService.editContact(
                    authenticationService.getCurrentUser(), contactName, newName, CONTACT_NAME
            );
        });
        commandHandlers.put(EDIT_CONTACT_PHONE, (nameAndNewContactPhoneNumber) -> {
            String[] arguments = getArguments(nameAndNewContactPhoneNumber);
            String contactName = arguments[0];
            String newPhoneNumber = arguments[1];
            return contactService.editContact(
                    authenticationService.getCurrentUser(), contactName, newPhoneNumber, CONTACT_PHONE_NUMBER
            );
        });
        commandHandlers.put(EDIT_CONTACT_AGE, (nameAndNewContactAge) -> {
            String[] arguments = getArguments(nameAndNewContactAge);
            String contactName = arguments[0];
            String newAge = arguments[1];
            return contactService.editContact(authenticationService.getCurrentUser(), contactName, newAge, CONTACT_AGE);
        });
        commandHandlers.put(EDIT_CONTACT_GENDER, (nameAndNewContactGender) -> {
            String[] arguments = getArguments(nameAndNewContactGender);
            String contactName = arguments[0];
            String newGender = arguments[1];
            return contactService.editContact(authenticationService.getCurrentUser(), contactName, newGender, CONTACT_GENDER);
        });
        commandHandlers.put(BLOCK_CONTACT, (contactName) ->
                contactService.blockContact(authenticationService.getCurrentUser(), contactName)
        );
        commandHandlers.put(UNBLOCK_CONTACT, (contactName) ->
                contactService.unblockContact(authenticationService.getCurrentUser(), contactName)
        );
        commandHandlers.put(DELETE_CONTACT_BY_NAME, (contactName) ->
                contactService.deleteContact(authenticationService.getCurrentUser(), contactName, CONTACT_NAME)
        );
        commandHandlers.put(DELETE_CONTACT_BY_PHONE, (contactPhone) ->
                contactService.deleteContact(authenticationService.getCurrentUser(), contactPhone, CONTACT_PHONE_NUMBER)
        );
    }

    private void registerGroupCommands() {
        commandHandlers.put(ADD_GROUP, (groupName) ->
                groupService.addGroup(authenticationService.getCurrentUser(), groupName)
        );
        commandHandlers.put(EDIT_GROUP_NAME, (nameAndNewGroupName) -> {
            String[] arguments = getArguments(nameAndNewGroupName);
            String groupName = arguments[0];
            String newGroupName = arguments[1];
            return groupService.editGroupName(
                    authenticationService.getCurrentUser(), groupName, newGroupName
            );
        });
        commandHandlers.put(GROUP_ADD_CONTACT, (groupAndContactNames) -> {
            String[] arguments = getArguments(groupAndContactNames);
            String groupName = arguments[0];
            String contactName = arguments[1];
            return groupService.addContactInGroup(authenticationService.getCurrentUser(), groupName, contactName);
        });
        commandHandlers.put(GROUP_DELETE_CONTACT, (groupNameAndContactNameToDelete) -> {
            String[] arguments = getArguments(groupNameAndContactNameToDelete);
            String groupName = arguments[0];
            String contactNameToDelete = arguments[1];
            return groupService.deleteContactFromGroup(
                    authenticationService.getCurrentUser(), groupName, contactNameToDelete
            );
        });
        commandHandlers.put(DELETE_GROUP, (groupNameToDelete) ->
                groupService.deleteGroup(authenticationService.getCurrentUser(), groupNameToDelete)
        );
    }

    /**
     * Регистрирует команды для поиска
     */
    private void registerSearchCommands() {
        commandHandlers.put(SEARCH_CONTACT_BY_NAME, (contactName) -> {
            // TODO: 23.11.2023 ДОДЕЛАТЬ
            return null;
        });
        commandHandlers.put(SEARCH_CONTACT_BY_PHONE, (contactPhone) -> {
            // TODO: 23.11.2023 ДОДЕЛАТЬ
            return null;
        });
    }

    /**
     * Регистрирует команды для фильтрации
     */
    private void registerFilterCommands() {
        commandHandlers.put(FILTER_CONTACT_BY_GENDER, (gender) -> {
            // TODO: 23.11.2023 ДОДЕЛАТЬ
            return null;
        });
        commandHandlers.put(FILTER_CONTACT_BY_AGE, (expressionAndAge) -> {
            // TODO: 23.11.2023 ДОДЕЛАТЬ
            return null;
        });
        commandHandlers.put(FILTER_CONTACT_BY_BLOCK, (args) -> {
            // TODO: 23.11.2023 ДОДЕЛАТЬ
            return null;
        });
        commandHandlers.put(FILTER_CONTACT_BY_UNBLOCK, (args) -> {
            // TODO: 23.11.2023 ДОДЕЛАТЬ
            return null;
        });
    }

    private void registerSortCommands() {
        commandHandlers.put(SORT_AGE, (ascOrDesc) ->
                sortService.sortContacts(authenticationService.getCurrentUser(), ascOrDesc, SortingKind.AGE)
        );
        commandHandlers.put(SORT_NAME, (ascOrDesc) ->
                sortService.sortContacts(authenticationService.getCurrentUser(), ascOrDesc, SortingKind.NAME)
        );
    }


    private void registerCommonCommands() {
        commandHandlers.put(HELP, (args) -> Command.getAllCommands());
        commandHandlers.put(EXIT_APP, (args) -> {
            System.exit(0);
            return messages.getMessageByKey("application.exit");
        });
    }

    private String[] getArguments(String args) {
        return args.trim().split(",\\s*");
    }

}
