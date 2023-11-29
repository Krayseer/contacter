package ru.anykeyers.processors.states;

import ru.anykeyers.contexts.Messages;
import ru.anykeyers.domain.User;
import ru.anykeyers.processors.states.domain.State;
import ru.anykeyers.bots.Bot;
import ru.anykeyers.factories.RepositoryFactory;
import ru.anykeyers.services.AuthenticationService;

import java.util.HashMap;
import java.util.Map;

/**
 * Базовый класс для классов обработчиков состояний
 */
public abstract class BaseStateProcessor implements Processable {

    /**
     * Карта вида [состояние -> его обработчик]
     */
    protected final Map<State, StateHandler> stateHandlers;

    protected final AuthenticationService authenticationService;

    protected final RepositoryFactory repositoryFactory;

    protected final Messages messages;

    public BaseStateProcessor(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
        stateHandlers = new HashMap<>();
        repositoryFactory = new RepositoryFactory();
        messages = new Messages();
        registerStatesHandlers();
    }

    @Override
    public String processState(User user, String message) {
        StateHandler stateHandler = stateHandlers.get(user.getState());
        return stateHandler.handleState(user, message);
    }

    /**
     * Регистрация обработчиков состояний {@link #stateHandlers}
     */
    public abstract void registerStatesHandlers();

    /**
     * Обработчик состояния
     */
    protected interface StateHandler {

        /**
         * Обработать сообщение
         * @param message сообщение
         */
        String handleState(User user, String message);

    }

}
