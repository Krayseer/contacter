package ru.anykeyers.processors.states;

import ru.anykeyers.contexts.Messages;
import ru.anykeyers.domain.User;
import ru.anykeyers.processors.states.domain.State;

import java.util.HashMap;
import java.util.Map;

/**
 * Базовый класс для обработчиков состояний
 */
public abstract class BaseStateProcessor implements StateProcessor {

    /**
     * Карта вида [состояние -> его обработчик]
     */
    private final Map<State, StateHandler> stateHandlers;

    private final Messages messages;

    public BaseStateProcessor() {
        stateHandlers = new HashMap<>();
        messages = new Messages();
    }

    @Override
    public String processState(User user, String message) {
        if (user.getState() == null) {
            return messages.getMessageByKey("state.not-select");
        }
        StateHandler stateHandler = stateHandlers.get(user.getState());
        if (stateHandler == null) {
            return messages.getMessageByKey("state.handler.not-exists", user.getState());
        }
        return stateHandler.handleState(user, message);
    }

    /**
     * Регистрация обработчика состояния в {@link #stateHandlers}
     * @param state состояние
     * @param stateHandler обработчик состояния
     */
    protected void registerHandler(State state, StateHandler stateHandler) {
        stateHandlers.put(state, stateHandler);
    }

}
