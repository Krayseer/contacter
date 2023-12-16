package ru.anykeyers.exception.state;

import ru.anykeyers.common.Messages;

/**
 * Исключение, выбрасываемое при попытке обработки состояния без состояния
 */
public class StateNotSelectException extends RuntimeException {

    private final Messages messages = Messages.getInstance();

    @Override
    public String getMessage() {
        return messages.getMessageByKey("state.exception.not-select");
    }

}
