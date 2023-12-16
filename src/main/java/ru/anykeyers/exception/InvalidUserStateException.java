package ru.anykeyers.exception;

import ru.anykeyers.common.Messages;

/**
 * Исключение, выбрасываемое при получении некорректного состоянии пользователя
 */
public class InvalidUserStateException extends RuntimeException {

    private final Messages messages = Messages.getInstance();

    @Override
    public String getMessage() {
        return messages.getMessageByKey("user.exception.state-invalid");
    }

}
