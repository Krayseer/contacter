package ru.anykeyers.factory;

import ru.anykeyers.exception.state.StateTypeNotExistsException;
import ru.anykeyers.processor.state.impl.ContactStateProcessor;
import ru.anykeyers.processor.state.impl.OperationStateProcessor;
import ru.anykeyers.processor.state.StateProcessor;
import ru.anykeyers.processor.state.domain.StateType;
import ru.anykeyers.processor.state.impl.GroupStateProcessor;
import ru.anykeyers.repository.ContactRepository;
import ru.anykeyers.repository.GroupRepository;
import ru.anykeyers.service.*;
import ru.anykeyers.service.impl.*;
import ru.anykeyers.service.impl.contact.ContactServiceImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Фабрика по созданию обработчиков состояний
 */
public class StateProcessorFactory {

    private final UserStateService userStateService;

    /**
     * Карта вида [тип состояния -> его обработчик]
     */
    private final Map<StateType, StateProcessor> stateProcessorsByStateType;

    public StateProcessorFactory(UserStateService userStateService,
                                 RepositoryFactory repositoryFactory) {
        this.userStateService = userStateService;
        stateProcessorsByStateType = new HashMap<>();
        ContactRepository contactRepository = repositoryFactory.createContactRepository();
        GroupRepository groupRepository = repositoryFactory.createGroupRepository();
        ContactService contactService = new ContactServiceImpl(contactRepository);
        GroupService groupService = new GroupServiceImpl(groupRepository, contactRepository);
        registerStateProcessors(contactService, groupService);
    }

    /**
     * Получить обработчик состояния по принимаемому типу состояния
     *
     * @param stateType тип состояния
     */
    public StateProcessor getStateProcessorByType(StateType stateType) {
        return Optional.ofNullable(stateProcessorsByStateType.get(stateType))
                .orElseThrow(StateTypeNotExistsException::new);
    }

    /**
     * Регистрация обработчиков по каждому состоянию
     */
    private void registerStateProcessors(ContactService contactService,
                                         GroupService groupService) {
        stateProcessorsByStateType.put(StateType.CONTACT, createContactStateProcessor(contactService));
        stateProcessorsByStateType.put(StateType.GROUP, createGroupStateProcessor(groupService));
        stateProcessorsByStateType.put(StateType.OPERATION, createOperationStateProcessor(contactService, groupService));
    }

    /**
     * Получить обработчик состояния по контактам
     */
    private StateProcessor createContactStateProcessor(ContactService contactService) {
        return new ContactStateProcessor(userStateService, contactService);
    }

    /**
     * Получить обработчик состояния по группам
     */
    private StateProcessor createGroupStateProcessor(GroupService groupService) {
        return new GroupStateProcessor(userStateService, groupService);
    }

    /**
     * Получить обработчик состояния по получении/поиску/фильтрации/сортировке данных
     */
    private StateProcessor createOperationStateProcessor(ContactService contactService,
                                                         GroupService groupService) {
        return new OperationStateProcessor(userStateService, contactService, groupService);
    }

}
