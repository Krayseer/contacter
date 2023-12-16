package ru.anykeyers.exception.state;

import ru.anykeyers.common.Messages;

/**
 * Исключение, выбрасываемое при попытке получения несуществующего типа состояния
 */
public class StateTypeNotExistsException extends RuntimeException {

    private final Messages messages = Messages.getInstance();

    @Override
    public String getMessage() {
        return messages.getMessageByKey("state.exception.type.not-exists");
    }

}
