package ru.anykeyers.processors.commands;

import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.Group;
import ru.anykeyers.bots.BotType;
import ru.anykeyers.factories.RepositoryFactory;
import ru.anykeyers.processors.states.Processable;
import ru.anykeyers.contexts.Messages;
import ru.anykeyers.processors.states.domain.State;
import ru.anykeyers.processors.states.domain.StateType;
import ru.anykeyers.domain.User;
import ru.anykeyers.factories.StateProcessorFactory;
import ru.anykeyers.bots.Bot;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.repositories.GroupRepository;
import ru.anykeyers.services.ContactService;
import ru.anykeyers.services.GroupService;
import ru.anykeyers.services.AuthenticationService;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.anykeyers.processors.states.domain.State.*;
import static ru.anykeyers.processors.commands.Command.*;

/**
 * Обработчик команд, вводимых пользователем
 */
public class CommandProcessor {

    private final Bot bot;

    private final AuthenticationService authenticationService;

    private final StateProcessorFactory stateHandlerFactory;

    private final Messages messages;

    private final Map<Command, CommandHandler> commandHandlers;

    private final ContactService contactService;

    private final GroupService groupService;

    public CommandProcessor(Bot bot, AuthenticationService authenticationService) {
        this.bot = bot;
        this.authenticationService = authenticationService;

        RepositoryFactory repositoryFactory = new RepositoryFactory();
        stateHandlerFactory = new StateProcessorFactory(authenticationService);

        ContactRepository contactRepository = repositoryFactory.createContactRepository();
        contactService = new ContactService(contactRepository);

        GroupRepository groupRepository = repositoryFactory.createGroupRepository();
        groupService = new GroupService(groupRepository, contactRepository);

        messages = new Messages();
        commandHandlers = new HashMap<>();

        registerCommonCommands();
        registerContactCommands();
        registerGroupCommands();
    }

    /**
     * Обработка введенного пользователем сообщения<br/>
     * <ol>
     *      <li>Устанавливается тип бота, из которого пришло сообщение</li>
     *      <li>Идентифицируется тип сообщения, после чего идет перенаправление в соответствующий обработчик
     *         (обработчик команды или обработчик состояния)
     *      </li>
     *      <li>В {@link #bot} отправляется результат обработки сообщения</li>
     * </ol>
     * @param username имя пользователя
     * @param message сообщение
     * @param botType тип бота, из которого пришло сообщение
     */
    public void processMessage(String username, String message, BotType botType) {
        Command command = Command.getCommandByValue(message);
        User user = authenticationService.getUserByUsername(username);
        user.setBotType(botType);
        String processResult = command != null
                ? processCommand(user, command)
                : processState(user, message);
        bot.sendMessage(user.getChatIdByAppType(), processResult);
    }

    /**
     * Обработка команды
     * @param user пользователь, для которого идет обработка команды
     * @param command команда
     */
    private String processCommand(User user, Command command) {
        CommandHandler commandHandler = commandHandlers.get(command);
        if (commandHandler == null) {
            return messages.getMessageByKey("command.not-exists");
        }
        return commandHandler.handleCommand(user);
    }

    /**
     * Обработка сообщения.<br/>
     * У пользователя берется тип установленного ранее состояния и в зависимости от этого сообщение перенаправляется
     * в соответствующий обработчик состояния
     * @param user пользователь, который обрабатывает сообщение
     * @param message сообщение
     */
    private String processState(User user, String message) {
        if(NONE.equals(user.getState())) {
            return messages.getMessageByKey("state.need-command-select");
        }
        Processable stateProcessor = stateHandlerFactory.getStateProcessorByType(user.getStateType());
        return stateProcessor.processState(user, message);
    }

    /**
     * Регистрация команд, связанных с контактами
     */
    private void registerContactCommands() {
        commandHandlers.put(GET_CONTACTS_COMMAND, (user) -> {
            return contactService.getAllContacts(user).stream()
                    .map(Contact::getInfo)
                    .collect(Collectors.joining("\n"));
        });
        commandHandlers.put(ADD_CONTACT_COMMAND, (user) ->
                registerCommand(user, ADD_CONTACT, StateType.CONTACT, "contact.state.add"));
        commandHandlers.put(EDIT_CONTACT_COMMAND, (user) ->
                registerCommand(user, EDIT_CONTACT, StateType.CONTACT, "contact.state.edit"));
        commandHandlers.put(DELETE_CONTACT_COMMAND, (user) ->
                registerCommand(user, DELETE_CONTACT, StateType.CONTACT, "contact.state.delete"));
    }

    /**
     * Регистрация команд, связанных с группами
     */
    private void registerGroupCommands() {
        commandHandlers.put(GET_GROUPS_COMMAND, (user) -> {
            return groupService.getAllGroups(user.getUsername()).stream()
                    .map(Group::getInfo)
                    .collect(Collectors.joining("\n"));
        });
        commandHandlers.put(GET_GROUP_CONTACTS_COMMAND, (user) ->
                registerCommand(user, GET_GROUP_CONTACTS, StateType.GROUP, "group.state.add"));
        commandHandlers.put(ADD_GROUP_COMMAND, (user) ->
                registerCommand(user, ADD_GROUP, StateType.GROUP, "group.state.add"));
        commandHandlers.put(EDIT_GROUP_COMMAND, (user) ->
                registerCommand(user, EDIT_GROUP, StateType.GROUP, "group.state.edit"));
        commandHandlers.put(DELETE_GROUP_COMMAND, (user) ->
                registerCommand(user, DELETE_GROUP, StateType.GROUP, "group.state.delete"));
    }

    /**
     * Регистрация общих команд приложения
     */
    private void registerCommonCommands() {
        commandHandlers.put(HELP_COMMAND, (user) -> {
            user.clearState();
            authenticationService.saveOrUpdateUser(user);
            return Command.getAllCommands();
        });
    }

    /**
     * Регистрация команды с указанием дополнительной информации
     * @param user пользователь
     * @param state состояние
     * @param stateType тип состояния
     * @param messageKey ключ сообщения
     */
    private String registerCommand(User user, State state, StateType stateType, String messageKey) {
        user.setState(state);
        user.setStateType(stateType);
        authenticationService.saveOrUpdateUser(user);
        return messages.getMessageByKey(messageKey);
    }

}
