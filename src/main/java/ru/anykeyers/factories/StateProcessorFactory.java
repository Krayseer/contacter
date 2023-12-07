package ru.anykeyers.factories;

import ru.anykeyers.contexts.Messages;
import ru.anykeyers.processors.states.impl.ContactStateProcessor;
import ru.anykeyers.processors.states.impl.FunctionalStateProcessor;
import ru.anykeyers.processors.states.impl.GroupStateProcessor;
import ru.anykeyers.processors.states.StateProcessor;
import ru.anykeyers.processors.states.domain.StateType;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.repositories.GroupRepository;
import ru.anykeyers.services.*;
import ru.anykeyers.services.impl.*;

/**
 * Фабрика по созданию обработчиков состояний
 */
public class StateProcessorFactory {

    private final AuthenticationService authenticationService;

    private final RepositoryFactory repositoryFactory;

    private final Messages messages;

    public StateProcessorFactory(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
        repositoryFactory = new RepositoryFactory();
        messages = new Messages();
    }

    /**
     * Создать обработчика состояния по принимаемому типу состояния
     * @param stateType тип состояния
     * @return обработчик состояния
     */
    public StateProcessor createStateProcessorByType(StateType stateType) {
        switch (stateType) {
            case CONTACT -> {
                ContactRepository contactRepository = repositoryFactory.createContactRepository();
                ContactService contactService = new ContactServiceImpl(contactRepository);
                return new ContactStateProcessor(authenticationService, contactService);
            }
            case GROUP -> {
                GroupRepository groupRepository = repositoryFactory.createGroupRepository();
                ContactRepository contactRepository = repositoryFactory.createContactRepository();
                GroupService groupService = new GroupServiceImpl(groupRepository, contactRepository);
                return new GroupStateProcessor(authenticationService, groupService);
            }
            case FUNCTION -> {
                GroupRepository groupRepository = repositoryFactory.createGroupRepository();
                ContactRepository contactRepository = repositoryFactory.createContactRepository();
                SearchService searchService = new SearchServiceImpl(contactRepository, groupRepository);
                FilterService filterService = new FilterServiceImpl(contactRepository);
                SortService sortService = new SortServiceImpl(contactRepository);
                return new FunctionalStateProcessor(authenticationService, searchService, filterService, sortService);
            }
            default -> {
                String errorMessage = messages.getMessageByKey("state.type.not-exists");
                throw new IllegalArgumentException(errorMessage);
            }
        }
    }

}
