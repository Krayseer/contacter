package ru.anykeyers.processor.command;

import ru.anykeyers.common.Messages;
import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.exception.CommandHandlerNotExistsException;
import ru.anykeyers.processor.state.domain.State;
import ru.anykeyers.processor.state.domain.StateType;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.service.UserStateService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Обработчик команд
 */
public class CommandProcessor {

    private final Messages messages = Messages.getInstance();

    private final UserStateService userStateService;

    /**
     * Карта вида [команда -> ее обработчик]
     */
    private final Map<Command, CommandHandler> commandHandlers;

    public CommandProcessor(UserStateService userStateService) {
        this.userStateService = userStateService;
        commandHandlers = new HashMap<>();

        registerCommonCommands();
        registerContactCommands();
        registerGroupCommands();
        registerOperationCommands();
    }

    /**
     * Обработка команды
     *
     * @param user пользователь, для которого идет обработка команды
     * @param command команда
     * @return результат обработки команды
     */
    public String processCommand(User user, Command command) {
        CommandHandler commandHandler = commandHandlers.get(command);
        if (commandHandler == null) {
            throw new CommandHandlerNotExistsException(command.getCommandValue());
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
     * Регистрация команд, связанных с получением обработанных данных (получение, поиск, сортировка, фильтрация)
     */
    private void registerOperationCommands() {
        StateType stateType = StateType.OPERATION;
        commandHandlers.put(Command.GET_COMMAND, (user) ->
                registerCommand(user, State.GET_KIND, stateType, "get.kind"));
        commandHandlers.put(Command.SEARCH_COMMAND, (user) ->
                registerCommand(user, State.SEARCH_KIND, stateType, "search.kind"));
        commandHandlers.put(Command.FILTER_COMMAND, (user) ->
                registerCommand(user, State.FILTER_KIND, stateType, "filter.kind"));
        commandHandlers.put(Command.SORT_COMMAND, (user) ->
                registerCommand(user, State.SORT_KIND, stateType, "sort.kind"));
    }

    /**
     * Регистрация общих команд приложения
     */
    private void registerCommonCommands() {
        commandHandlers.put(Command.HELP_COMMAND, (user) -> {
            StateInfo userStateInfo = userStateService.getUserState(user);
            userStateInfo.clear();
            return Arrays.stream(Command.values())
                    .map(command -> String.format("%s : %s",
                            command.getCommandValue(),
                            messages.getMessageByKey(command.getDescriptionKey())))
                    .collect(Collectors.joining("\n"));
        });
    }

    /**
     * Регистрация команды с указанием дополнительной информации
     *
     * @param user пользователь
     * @param state состояние
     * @param stateType тип состояния
     * @param messageKey ключ сообщения
     * @return сообщение о результате регистрации
     */
    private String registerCommand(User user, State state, StateType stateType, String messageKey) {
        StateInfo userStateInfo = userStateService.getUserState(user);
        userStateInfo.setState(state);
        userStateInfo.setStateType(stateType);
        return messages.getMessageByKey(messageKey);
    }

}
