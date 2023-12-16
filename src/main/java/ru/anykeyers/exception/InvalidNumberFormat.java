package ru.anykeyers.exception;

import ru.anykeyers.common.Messages;

/**
 * Исключение, выбрасываемое при вводе некорректного числового значения
 */
public class InvalidNumberFormat extends RuntimeException {

    private final Messages messages = Messages.getInstance();

    @Override
    public String getMessage() {
        return messages.getMessageByKey("exception.number.invalid-format");
    }

}
