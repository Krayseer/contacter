package ru.anykeyers.processors;

import ru.anykeyers.bots.Bot;
import ru.anykeyers.bots.BotType;
import ru.anykeyers.contexts.Messages;
import ru.anykeyers.domain.User;
import ru.anykeyers.factories.StateProcessorFactory;
import ru.anykeyers.processors.commands.Command;
import ru.anykeyers.processors.commands.CommandProcessor;
import ru.anykeyers.processors.states.StateProcessor;
import ru.anykeyers.processors.states.domain.State;
import ru.anykeyers.services.AuthenticationService;

import java.util.Arrays;

/**
 * Обработчик сообщений
 */
public class MessageProcessor {

    /**
     * Бот, для которого идет орбаботка сообщения
     */
    private final Bot bot;

    private final StateProcessorFactory stateProcessorFactory;

    private final AuthenticationService authenticationService;

    private final Messages messages;

    private final CommandProcessor commandProcessor;

    public MessageProcessor(Bot bot,
                            AuthenticationService authenticationService) {
        this.bot = bot;
        this.authenticationService = authenticationService;
        stateProcessorFactory = new StateProcessorFactory(authenticationService);
        commandProcessor = new CommandProcessor(authenticationService);
        messages = new Messages();
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
        Command command = getCommandByValue(message);
        User user = authenticationService.getUserByUsernameAndBotType(username, botType);
        String processResult = command != null
                ? processCommand(user, command)
                : processState(user, message);
        bot.sendMessage(user.getChatId(), processResult);
    }

    /**
     * Обработка команды
     * @param user пользователь, для которого идет обработка команды
     * @param command команда
     */
    private String processCommand(User user, Command command) {
        return commandProcessor.processCommand(user, command);
    }

    /**
     * Обработка состояния<br/>
     * У пользователя берется тип установленного ранее состояния и в зависимости от этого сообщение перенаправляется
     * в соответствующий обработчик состояния
     * @param user пользователь, который обрабатывает сообщение
     * @param message сообщение
     */
    private String processState(User user, String message) {
        if(State.NONE.equals(user.getState())) {
            return messages.getMessageByKey("state.need-command-select");
        }
        StateProcessor stateProcessor = stateProcessorFactory.createStateProcessorByType(user.getStateType());
        return stateProcessor.processState(user, message);
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
