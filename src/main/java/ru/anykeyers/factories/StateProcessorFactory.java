package ru.anykeyers.factories;

import ru.anykeyers.contexts.Messages;
import ru.anykeyers.processors.states.impl.ContactStateProcessor;
import ru.anykeyers.processors.states.impl.FunctionalStateProcessor;
import ru.anykeyers.processors.states.impl.GroupStateProcessor;
import ru.anykeyers.processors.states.StateProcessor;
import ru.anykeyers.processors.states.domain.StateType;
import ru.anykeyers.processors.states.impl.ImportExportStateProcessor;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.repositories.GroupRepository;
import ru.anykeyers.services.*;
import ru.anykeyers.services.impl.*;

/**
 * Фабрика по созданию обработчиков состояний
 */
public class StateProcessorFactory {

    private final AuthenticationService authenticationService;

    private final ContactRepository contactRepository;

    private final GroupRepository groupRepository;

    private final Messages messages;

    public StateProcessorFactory(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
        RepositoryFactory repositoryFactory = new RepositoryFactory();
        contactRepository = repositoryFactory.createContactRepository();
        groupRepository = repositoryFactory.createGroupRepository();
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
                ContactService contactService = new ContactServiceImpl(contactRepository);
                return new ContactStateProcessor(authenticationService, contactService);
            }
            case GROUP -> {
                GroupService groupService = new GroupServiceImpl(groupRepository, contactRepository);
                return new GroupStateProcessor(authenticationService, groupService);
            }
            case FUNCTION -> {
                SearchService searchService = new SearchServiceImpl(contactRepository, groupRepository);
                FilterService filterService = new FilterServiceImpl(contactRepository);
                SortService sortService = new SortServiceImpl(contactRepository);
                return new FunctionalStateProcessor(authenticationService, searchService, filterService, sortService);
            }
            case IMPORT_EXPORT -> {
                ImportExportService importExportService = new ImportExportServiceImpl(contactRepository);
                return new ImportExportStateProcessor(importExportService);
            }
            default -> {
                String errorMessage = messages.getMessageByKey("state.type.not-exists");
                throw new IllegalArgumentException(errorMessage);
            }
        }
    }

}
