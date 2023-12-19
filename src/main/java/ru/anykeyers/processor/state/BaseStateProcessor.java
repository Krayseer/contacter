package ru.anykeyers.processor.state;

import ru.anykeyers.utils.EnumUtils;
import ru.anykeyers.domain.Message;
import ru.anykeyers.domain.Message;
import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.exception.state.StateHandlerNotExistsException;
import ru.anykeyers.exception.state.StateNotSelectException;
import ru.anykeyers.processor.state.domain.State;
import ru.anykeyers.service.UserStateService;

import java.util.HashMap;
import java.util.Map;

/**
 * Базовый класс для обработчиков состояний
 */
public abstract class BaseStateProcessor implements StateProcessor {

    /**
     * Карта вида [состояние -> его обработчик]
     */
    private final Map<State, StateHandler> stateHandlers = new HashMap<>();

    protected final UserStateService userStateService;

    protected final EnumUtils enumUtils;

    public BaseStateProcessor(UserStateService userStateService) {
        this.userStateService = userStateService;
        enumUtils = new EnumUtils();
    }

    @Override
    public Message processState(User user, String message) {
        StateInfo userStateInfo = userStateService.getUserState(user);
        if (userStateInfo.getState() == null) {
            throw new StateNotSelectException();
        }
        StateHandler stateHandler = stateHandlers.get(userStateInfo.getState());
        if (stateHandler == null) {
            throw new StateHandlerNotExistsException(userStateInfo.getState());
        }
        Message handleStateResult;
        try {
            handleStateResult = stateHandler.handleState(user, message);
        } catch (Exception ex) {
            return new Message(ex.getMessage());
        }
        return handleStateResult;
    }

    /**
     * Регистрация обработчика состояния в {@link #stateHandlers}
     *
     * @param state состояние
     * @param stateHandler обработчик состояния
     */
    protected void registerHandler(State state, StateHandler stateHandler) {
        stateHandlers.put(state, stateHandler);
    }

}
