package ru.anykeyers.factories;

import ru.anykeyers.processors.states.impl.ContactStateProcessor;
import ru.anykeyers.processors.states.impl.GroupStateProcessor;
import ru.anykeyers.processors.states.Processable;
import ru.anykeyers.processors.states.domain.StateType;
import ru.anykeyers.services.AuthenticationService;

/**
 * Фабрика по созданию обработчиков состояний
 */
public class StateProcessorFactory {

    /**
     * Обработчик состояний для контактов
     */
    private final Processable contactStateProcessor;

    /**
     * Обработчик состояний для групп
     */
    private final Processable groupStateProcessor;

    public StateProcessorFactory(AuthenticationService authenticationService) {
        contactStateProcessor = new ContactStateProcessor(authenticationService);
        groupStateProcessor = new GroupStateProcessor(authenticationService);
    }

    /**
     * Получить обработчика по принимаемому типу состояния
     * @param stateType тип состояния(контакты, группа...)
     * @return обработчик
     */
    public Processable getStateProcessorByType(StateType stateType) {
        return switch (stateType) {
            case NONE -> null;
            case CONTACT -> contactStateProcessor;
            case GROUP -> groupStateProcessor;
        };
    }

}
