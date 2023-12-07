package ru.anykeyers.processors.commands;

import ru.anykeyers.contexts.Messages;
import ru.anykeyers.processors.states.domain.State;
import ru.anykeyers.processors.states.domain.StateType;
import ru.anykeyers.domain.User;
import ru.anykeyers.services.AuthenticationService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Обработчик команд
 */
public class CommandProcessor {

    private final AuthenticationService authenticationService;

    private final Messages messages;

    /**
     * Карта вида [команда -> ее обработчик]
     */
    private final Map<Command, CommandHandler> commandHandlers;

    public CommandProcessor(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
        messages = new Messages();
        commandHandlers = new HashMap<>();

        registerCommonCommands();
        registerContactCommands();
        registerGroupCommands();
        registerFunctionCommands();
    }

    /**
     * Обработка команды
     * @param user пользователь, для которого идет обработка команды
     * @param command команда
     * @return результат обработки команды
     */
    public String processCommand(User user, Command command) {
        CommandHandler commandHandler = commandHandlers.get(command);
        if (commandHandler == null) {
            return messages.getMessageByKey("command.not-exists-handler", command.getCommandValue());
        }
        return commandHandler.handleCommand(user);
    }

    /**
     * Регистрация команд, связанных с контактами
     */
    private void registerContactCommands() {
        commandHandlers.put(Command.ADD_CONTACT_COMMAND, (user) ->
                registerCommand(user, State.ADD_CONTACT, StateType.CONTACT, "contact.state.add"));
        commandHandlers.put(Command.EDIT_CONTACT_COMMAND, (user) ->
                registerCommand(user, State.EDIT_CONTACT, StateType.CONTACT, "contact.state.edit"));
        commandHandlers.put(Command.DELETE_CONTACT_COMMAND, (user) ->
                registerCommand(user, State.DELETE_CONTACT, StateType.CONTACT, "contact.state.delete"));
    }

    /**
     * Регистрация команд, связанных с группами
     */
    private void registerGroupCommands() {
        commandHandlers.put(Command.ADD_GROUP_COMMAND, (user) ->
                registerCommand(user, State.ADD_GROUP, StateType.GROUP, "group.state.add"));
        commandHandlers.put(Command.EDIT_GROUP_COMMAND, (user) ->
                registerCommand(user, State.EDIT_GROUP, StateType.GROUP, "group.state.edit"));
        commandHandlers.put(Command.DELETE_GROUP_COMMAND, (user) ->
                registerCommand(user, State.DELETE_GROUP, StateType.GROUP, "group.state.delete"));
    }

    /**
     * Регистрация команд, связанных с получением обработанных данных (поиск, сортировка, фильтрация)
     */
    private void registerFunctionCommands() {
        commandHandlers.put(Command.SEARCH_COMMAND, (user) ->
                registerCommand(user, State.SEARCH_KIND, StateType.FUNCTION, "search.kind"));
        commandHandlers.put(Command.FILTER_COMMAND, (user) ->
                registerCommand(user, State.FILTER_KIND, StateType.FUNCTION, "filter.kind"));
        commandHandlers.put(Command.SORT_COMMAND, (user) ->
                registerCommand(user, State.SORT_KIND, StateType.FUNCTION, "sort.kind"));
    }

    /**
     * Регистрация общих команд приложения
     */
    private void registerCommonCommands() {
        commandHandlers.put(Command.HELP_COMMAND, (user) -> {
            user.clearState();
            authenticationService.saveOrUpdateUser(user);
            return getAllCommands();
        });
    }

    /**
     * Регистрация команды с указанием дополнительной информации
     * @param user пользователь
     * @param state состояние
     * @param stateType тип состояния
     * @param messageKey ключ сообщения
     * @return сообщение о результате регистрации
     */
    private String registerCommand(User user, State state, StateType stateType, String messageKey) {
        user.setState(state);
        user.setStateType(stateType);
        authenticationService.saveOrUpdateUser(user);
        return messages.getMessageByKey(messageKey);
    }

    /**
     * Получить все команды
     */
    private String getAllCommands() {
        return Arrays.stream(Command.values())
                .map(command -> String.format("%s : %s",
                        command.getCommandValue(),
                        command.getDescription()))
                .collect(Collectors.joining("\n"));
    }

}
