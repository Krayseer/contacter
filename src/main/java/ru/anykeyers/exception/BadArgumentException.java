package ru.anykeyers.exception;

import ru.anykeyers.common.Messages;

/**
 * Исключение, выбрасываемое при выборе неверного параметра обработки
 */
public class BadArgumentException extends RuntimeException {

    private final Messages messages = Messages.getInstance();

    @Override
    public String getMessage() {
        return messages.getMessageByKey("exception.argument.invalid");
    }

}
