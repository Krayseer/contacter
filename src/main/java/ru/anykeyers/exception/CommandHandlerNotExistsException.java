package ru.anykeyers.exception;

import ru.anykeyers.common.Messages;

/**
 * Исключение, выбрасываемое при попытке обработки команды, для которой не установлен обработчик<br/>
 * Содержит информацию о команде, для которой не установлен обработчик
 */
public class CommandHandlerNotExistsException extends RuntimeException {

    private final Messages messages = Messages.getInstance();

    private final String command;

    public CommandHandlerNotExistsException(String command) {
        this.command = command;
    }

    @Override
    public String getMessage() {
        return messages.getMessageByKey("command.exception.handler.not-exists", command);
    }

}
