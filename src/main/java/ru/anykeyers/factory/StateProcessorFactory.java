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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Фабрика по созданию обработчиков состояний
 */
public class StateProcessorFactory {

    private final UserStateService userStateService;

    private final RepositoryFactory repositoryFactory;

    /**
     * Карта вида [тип состояния -> его обработчик]
     */
    private final Map<StateType, StateProcessor> stateProcessorsByStateType;

    public StateProcessorFactory(UserStateService userStateService,
                                 RepositoryFactory repositoryFactory) {
        this.userStateService = userStateService;
        this.repositoryFactory = repositoryFactory;
        stateProcessorsByStateType = new HashMap<>();
        registerStateProcessors();
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
    private void registerStateProcessors() {
        ContactRepository contactRepository = repositoryFactory.createContactRepository();
        GroupRepository groupRepository = repositoryFactory.createGroupRepository();

        stateProcessorsByStateType.put(StateType.CONTACT, createContactStateProcessor(contactRepository));
        stateProcessorsByStateType.put(StateType.GROUP, createGroupStateProcessor(contactRepository, groupRepository));
        stateProcessorsByStateType.put(StateType.OPERATION, createOperationStateProcessor(contactRepository, groupRepository));
    }

    /**
     * Получить обработчик состояния по контактам
     */
    private StateProcessor createContactStateProcessor(ContactRepository contactRepository) {
        ContactService contactService = new ContactServiceImpl(contactRepository);
        return new ContactStateProcessor(userStateService, contactService);
    }

    /**
     * Получить обработчик состояния по группам
     */
    private StateProcessor createGroupStateProcessor(ContactRepository contactRepository,
                                                     GroupRepository groupRepository) {
        GroupService groupService = new GroupServiceImpl(groupRepository, contactRepository);
        return new GroupStateProcessor(userStateService, groupService);
    }

    /**
     * Получить обработчик состояния по получении/поиску/фильтрации/сортировке данных
     */
    private StateProcessor createOperationStateProcessor(ContactRepository contactRepository,
                                                         GroupRepository groupRepository) {
        DataRetrievalService dataRetrievalService = new DataRetrievalServiceImpl(contactRepository, groupRepository);
        SearchService searchService = new SearchServiceImpl(contactRepository, groupRepository);
        FilterContactService filterService = new FilterContactServiceImpl(contactRepository);
        SortContactService sortService = new SortContactServiceImpl(contactRepository);
        return new OperationStateProcessor(
                userStateService, dataRetrievalService,
                searchService, filterService, sortService
        );
    }

}
