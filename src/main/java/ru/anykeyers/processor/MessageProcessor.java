package ru.anykeyers.processor;

import ru.anykeyers.bot.Bot;
import ru.anykeyers.bot.BotType;
import ru.anykeyers.common.Messages;
import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.exception.CommandHandlerNotExistsException;
import ru.anykeyers.exception.state.StateHandlerNotExistsException;
import ru.anykeyers.exception.state.StateNotSelectException;
import ru.anykeyers.factory.RepositoryFactory;
import ru.anykeyers.factory.StateProcessorFactory;
import ru.anykeyers.processor.command.Command;
import ru.anykeyers.processor.command.CommandProcessor;
import ru.anykeyers.processor.state.StateProcessor;
import ru.anykeyers.processor.state.domain.State;
import ru.anykeyers.service.AuthenticationService;
import ru.anykeyers.service.UserStateService;

import java.util.Arrays;

/**
 * Обработчик сообщений
 */
public class MessageProcessor {

    private final Messages messages = Messages.getInstance();

    /**
     * Бот, для которого идет обработка сообщения
     */
    private final Bot bot;

    private final StateProcessorFactory stateProcessorFactory;

    private final AuthenticationService authenticationService;

    private final UserStateService userStateService;

    private final CommandProcessor commandProcessor;

    public MessageProcessor(Bot bot,
                            AuthenticationService authenticationService,
                            UserStateService userStateService,
                            RepositoryFactory repositoryFactory) {
        this.bot = bot;
        this.authenticationService = authenticationService;
        this.userStateService = userStateService;
        stateProcessorFactory = new StateProcessorFactory(userStateService, repositoryFactory);
        commandProcessor = new CommandProcessor(userStateService);
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
     *
     * @param username имя пользователя
     * @param message сообщение
     * @param botType тип бота, из которого пришло сообщение
     */
    public void processMessage(String username, String message, BotType botType) {
        Command command = getCommandByValue(message);
        User user = authenticationService.getUserByUsernameAndBotType(username, botType);
        String processResult = command != null
                ? processCommand(user, command)
                : processState(user, message);
        bot.sendMessage(user.getChatId(), processResult);
    }

    /**
     * Обработка команды
     *
     * @param user пользователь, для которого идет обработка команды
     * @param command команда
     */
    private String processCommand(User user, Command command) {
        String result;
        try {
            result = commandProcessor.processCommand(user, command);
        } catch (CommandHandlerNotExistsException ex) {
            return ex.getMessage();
        }
        return result;
    }

    /**
     * Обработка состояния<br/>
     * У пользователя берется тип установленного ранее состояния и в зависимости от этого сообщение перенаправляется
     * в соответствующий обработчик состояния
     *
     * @param user пользователь, который обрабатывает сообщение
     * @param message сообщение
     */
    private String processState(User user, String message) {
        StateInfo userStateInfo = userStateService.getUserState(user);
        if(State.NONE.equals(userStateInfo.getState())) {
            return messages.getMessageByKey("command.exception.need-select");
        }
        StateProcessor stateProcessor = stateProcessorFactory.getStateProcessorByType(userStateInfo.getStateType());
        String processStateResult;
        try {
            processStateResult = stateProcessor.processState(user, message);
        } catch (StateHandlerNotExistsException | StateNotSelectException ex) {
            return ex.getMessage();
        }
        return processStateResult;
    }

    /**
     * Получить команду по текстовому представлению команды
     */
    private Command getCommandByValue(String value) {
        return Arrays.stream(Command.values())
                .filter(command -> command.getCommandValue().equals(value))
                .findFirst()
                .orElse(null);
    }

}
