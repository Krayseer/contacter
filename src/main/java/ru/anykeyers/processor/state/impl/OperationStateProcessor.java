package ru.anykeyers.processor.state.impl;

import ru.anykeyers.common.Messages;
import ru.anykeyers.domain.Message;
import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.domain.entity.Group;
import ru.anykeyers.common.Mapper;
import ru.anykeyers.exception.InvalidNumberFormat;
import ru.anykeyers.processor.state.domain.kinds.DataGetKind;
import ru.anykeyers.processor.state.domain.mapper.UiContactMapper;
import ru.anykeyers.exception.BadArgumentException;
import ru.anykeyers.processor.state.BaseStateProcessor;
import ru.anykeyers.processor.state.domain.State;
import ru.anykeyers.processor.state.domain.kinds.filter.ContactFilterKind;
import ru.anykeyers.processor.state.domain.kinds.ContactSearchKind;
import ru.anykeyers.processor.state.domain.kinds.sort.ContactSortKind;
import ru.anykeyers.processor.state.domain.mapper.UiGroupMapper;
import ru.anykeyers.service.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Обработчик состояний для получения обработанной информации (получение, поиск, фильтрация, сортировка)
 */
public class OperationStateProcessor extends BaseStateProcessor {

    private final Messages messages = Messages.getInstance();

    private final DataRetrievalService dataRetrievalService;

    private final SearchService searchService;

    private final FilterContactService filterService;

    private final SortContactService sortService;

    private final Mapper<Contact> contactMapper;

    private final Mapper<Group> groupMapper;

    public OperationStateProcessor(UserStateService userStateService,
                                   DataRetrievalService dataRetrievalService,
                                   SearchService searchService,
                                   FilterContactService filterService,
                                   SortContactService sortService) {
        super(userStateService);
        this.dataRetrievalService = dataRetrievalService;
        this.searchService = searchService;
        this.filterService = filterService;
        this.sortService = sortService;

        contactMapper = new UiContactMapper();
        groupMapper = new UiGroupMapper();

        registerDataRetrievalService();
        registerSearchStateHandlers();
        registerFilterCommands();
        registerSortCommands();
    }

    /**
     * Регистрация состояний и их обработчиков для получения данных
     */
    private void registerDataRetrievalService() {
        registerHandler(State.GET_KIND, ((user, field) -> {
            switch (field) {
                case DataGetKind.CONTACTS -> {
                    Set<Contact> contacts = dataRetrievalService.getAllContacts(user);
                    StateInfo userStateInfo = userStateService.getUserState(user);
                    userStateInfo.clear();
                    return getUiContactsInfo(contacts);
                }
                case DataGetKind.GROUPS -> {
                    Set<Group> groups = dataRetrievalService.getAllGroups(user);
                    StateInfo userStateInfo = userStateService.getUserState(user);
                    userStateInfo.clear();
                    return getUiGroupsInfo(groups);
                }
                case DataGetKind.GROUP_CONTACTS -> {
                    StateInfo userStateInfo = userStateService.getUserState(user);
                    userStateInfo.setState(State.GET_GROUP_CONTACTS);
                    return new Message(messages.getMessageByKey("get.group.contacts"));
                }
            }
            throw new BadArgumentException();
        }));
        registerHandler(State.GET_GROUP_CONTACTS, ((user, message) -> {
            Set<Contact> contacts;
            try {
                contacts = dataRetrievalService.getAllGroupContacts(user, message);
            } catch (Exception ex) {
                return new Message(ex.getMessage());
            }
            StateInfo userStateInfo = userStateService.getUserState(user);
            userStateInfo.clear();
            return getUiContactsInfo(contacts);
        }));
    }

    /**
     * Регистрация состояний и их обработчиков для поиска
     */
    private void registerSearchStateHandlers() {
        registerHandler(State.SEARCH_KIND, (user, field) -> {
            switch (field) {
                case ContactSearchKind.BY_NAME -> {
                    StateInfo userStateInfo = userStateService.getUserState(user);
                    userStateInfo.setState(State.SEARCH_NAME);
                    return new Message(messages.getMessageByKey("search.contact.name"));
                }
                case ContactSearchKind.BY_PHONE -> {
                    StateInfo userStateInfo = userStateService.getUserState(user);
                    userStateInfo.setState(State.SEARCH_PHONE);
                    return new Message(messages.getMessageByKey("search.contact.phone-number"));
                }
                case ContactSearchKind.GROUP_CONTACTS_BY_NAME -> {
                    StateInfo userStateInfo = userStateService.getUserState(user);
                    userStateInfo.setState(State.SEARCH_GROUP_CONTACTS);
                    return new Message(messages.getMessageByKey("search.group.contacts"));
                }
            }
            throw new BadArgumentException();
        });
        registerHandler(State.SEARCH_GROUP_CONTACTS, ((user, message) -> {
            StateInfo userStateInfo = userStateService.getUserState(user);
            userStateInfo.setState(State.SEARCH_GROUP_CONTACTS_BY_NAME);
            userStateInfo.setEditInfo(message);
            return new Message(messages.getMessageByKey("search.group.contacts.name"));
        }));
        List<State> searchKinds = List.of(State.SEARCH_NAME, State.SEARCH_PHONE, State.SEARCH_GROUP_CONTACTS_BY_NAME);
        searchKinds.forEach(state -> registerHandler(state, (user, message) -> {
            StateInfo userStateInfo = userStateService.getUserState(user);
            Set<Contact> contacts;
            try {
                contacts = searchService.findContactsByArgument(user, userStateInfo, message);
            } catch (Exception ex) {
                return new Message(ex.getMessage());
            }
            userStateInfo.clear();
            return getUiContactsInfo(contacts);
        }));
    }

    /**
     * Регистрация состояний и их обработчиков для фильтрации
     */
    private void registerFilterCommands() {
        registerHandler(State.FILTER_KIND, ((user, field) -> {
            switch (field) {
                case ContactFilterKind.BY_AGE -> {
                    StateInfo userStateInfo = userStateService.getUserState(user);
                    userStateInfo.setState(State.FILTER_AGE);
                    return new Message(messages.getMessageByKey("filter.age"));
                }
                case ContactFilterKind.BY_GENDER -> {
                    StateInfo userStateInfo = userStateService.getUserState(user);
                    userStateInfo.setState(State.FILTER_GENDER);
                    return new Message(messages.getMessageByKey("filter.gender"));
                }
                case ContactFilterKind.BY_BLOCK -> {
                    StateInfo userStateInfo = userStateService.getUserState(user);
                    userStateInfo.setState(State.FILTER_BLOCK);
                    return new Message(messages.getMessageByKey("filter.block"));
                }
            }
            throw new BadArgumentException();
        }));
        registerHandler(State.FILTER_AGE, ((user, message) -> {
            try {
                Integer.parseInt(message);
            } catch (NumberFormatException ex) {
                throw new InvalidNumberFormat();
            }
            StateInfo userStateInfo = userStateService.getUserState(user);
            userStateInfo.setState(State.FILTER_AGE_KIND);
            userStateInfo.setEditInfo(message);
            return new Message(messages.getMessageByKey("filter.age.kind"));
        }));
        List<State> states = List.of(State.FILTER_AGE_KIND, State.FILTER_BLOCK, State.FILTER_GENDER);
        states.forEach(state -> registerHandler(state, ((user, message) -> {
            StateInfo userStateInfo = userStateService.getUserState(user);
            Set<Contact> contacts;
            try {
                contacts = filterService.filterByUserStateAndKind(user, userStateInfo,  message);
            } catch (Exception ex) {
                return new Message(ex.getMessage());
            }
            userStateInfo.clear();
            return getUiContactsInfo(contacts);
        })));
    }

    /**
     * Регистрация состояний и их обработчиков для сортировки
     */
    private void registerSortCommands() {
        registerHandler(State.SORT_KIND, ((user, field) -> {
            switch (field) {
                case ContactSortKind.BY_NAME -> {
                    StateInfo userStateInfo = userStateService.getUserState(user);
                    userStateInfo.setState(State.SORT_NAME);
                    return new Message(messages.getMessageByKey("sort.kind.type"));
                }
                case ContactSortKind.BY_AGE -> {
                    StateInfo userStateInfo = userStateService.getUserState(user);
                    userStateInfo.setState(State.SORT_AGE);
                    return new Message(messages.getMessageByKey("sort.kind.type"));
                }
            }
            throw new BadArgumentException();
        }));
        List<State> states = List.of(State.SORT_NAME, State.SORT_AGE);
        states.forEach(state -> registerHandler(state, ((user, message) -> {
            StateInfo userStateInfo = userStateService.getUserState(user);
            Set<Contact> contacts;
            try {
                contacts = sortService.sortByUserStateAndKind(user, userStateInfo, message);
            } catch (Exception ex) {
                return new Message(ex.getMessage());
            }
            userStateInfo.clear();
            return getUiContactsInfo(contacts);
        })));
    }

    /**
     * Получить информацию по всем контактам в человеко читаемом формате для пользователя
     *
     * @param contacts контакты
     */
    private Message getUiContactsInfo(Set<Contact> contacts) {
        String contactsToText = contacts.isEmpty()
                ? messages.getMessageByKey("commons.empty")
                : contacts.stream()
                    .map(contactMapper::format)
                    .collect(Collectors.joining("\n\n"));
        return new Message(contactsToText);
    }

    /**
     * Получить информацию по всем группам в человеко читаемом формате для пользователя
     *
     * @param groups группы
     */
    private Message getUiGroupsInfo(Set<Group> groups) {
        String groupsToText = groups.isEmpty()
                ? messages.getMessageByKey("commons.empty")
                : groups.stream()
                .map(groupMapper::format)
                .collect(Collectors.joining("\n\n"));
        return new Message(groupsToText);
    }

}