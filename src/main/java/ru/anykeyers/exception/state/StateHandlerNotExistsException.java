package ru.anykeyers.exception.state;

import ru.anykeyers.common.Messages;
import ru.anykeyers.processor.state.domain.State;

/**
 * Исключение, выбрасываемое при попытке получения обработчика несуществующего состояния<br/>
 * Содержит информацию о несуществующем состоянии
 */
public class StateHandlerNotExistsException extends RuntimeException {

    private final Messages messages = Messages.getInstance();

    public State state;

    public StateHandlerNotExistsException(State state) {
        this.state = state;
    }

    @Override
    public String getMessage() {
        return messages.getMessageByKey("state.exception.handler.not-exists", state);
    }

}
