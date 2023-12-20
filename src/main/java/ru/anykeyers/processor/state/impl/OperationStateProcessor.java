package ru.anykeyers.processor.state.impl;

import ru.anykeyers.common.Messages;
import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.domain.entity.Group;
import ru.anykeyers.common.Mapper;
import ru.anykeyers.exception.InvalidNumberFormat;
import ru.anykeyers.processor.state.domain.kinds.DataGetKind;
import ru.anykeyers.processor.state.domain.kinds.sort.SortDirectionKind;
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

    private final ContactService contactService;

    private final GroupService groupService;

    private final Mapper<Contact> contactMapper;

    private final Mapper<Group> groupMapper;

    public OperationStateProcessor(UserStateService userStateService,
                                   ContactService contactService,
                                   GroupService groupService) {
        super(userStateService);
        this.contactService = contactService;
        this.groupService = groupService;

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
            DataGetKind kind = (DataGetKind) utils.getEnumKindByField(DataGetKind.values(), field);
            switch (kind) {
                case CONTACTS -> {
                    Set<Contact> contacts = contactService.findAll(user);
                    StateInfo userStateInfo = userStateService.getUserState(user);
                    userStateInfo.clear();
                    return formatContacts(contacts);
                }
                case GROUPS -> {
                    Set<Group> groups = groupService.findAll(user);
                    StateInfo userStateInfo = userStateService.getUserState(user);
                    userStateInfo.clear();
                    return formatGroups(groups);
                }
                case GROUP_CONTACTS -> {
                    StateInfo userStateInfo = userStateService.getUserState(user);
                    userStateInfo.setState(State.GET_GROUP_CONTACTS);
                    return messages.getMessageByKey("get.group.contacts");
                }
            }
            throw new BadArgumentException();
        }));
        registerHandler(State.GET_GROUP_CONTACTS, ((user, message) -> {
            Set<Contact> contacts;
            try {
                contacts = groupService.findAllGroupContacts(user, message);
            } catch (Exception ex) {
                return ex.getMessage();
            }
            StateInfo userStateInfo = userStateService.getUserState(user);
            userStateInfo.clear();
            return formatContacts(contacts);
        }));
    }

    /**
     * Регистрация состояний и их обработчиков для поиска
     */
    private void registerSearchStateHandlers() {
        registerHandler(State.SEARCH_KIND, (user, field) -> {
            ContactSearchKind kind =
                    (ContactSearchKind) utils.getEnumKindByField(ContactSearchKind.values(), field);
            switch (kind) {
                case BY_NAME -> {
                    StateInfo userStateInfo = userStateService.getUserState(user);
                    userStateInfo.setState(State.SEARCH_NAME);
                    return messages.getMessageByKey("search.contact.name");
                }
                case BY_PHONE -> {
                    StateInfo userStateInfo = userStateService.getUserState(user);
                    userStateInfo.setState(State.SEARCH_PHONE);
                    return messages.getMessageByKey("search.contact.phone-number");
                }
                case GROUP_CONTACTS_BY_NAME -> {
                    StateInfo userStateInfo = userStateService.getUserState(user);
                    userStateInfo.setState(State.SEARCH_GROUP_CONTACTS);
                    return messages.getMessageByKey("search.group.contacts");
                }
            }
            throw new BadArgumentException();
        });
        registerHandler(State.SEARCH_GROUP_CONTACTS, ((user, message) -> {
            StateInfo userStateInfo = userStateService.getUserState(user);
            userStateInfo.setState(State.SEARCH_GROUP_CONTACTS_BY_NAME);
            userStateInfo.setEditInfo(message);
            return messages.getMessageByKey("search.group.contacts.name");
        }));
        registerHandler(State.SEARCH_GROUP_CONTACTS_BY_NAME, ((user, message) -> {
            StateInfo userStateInfo = userStateService.getUserState(user);
            Set<Contact> contacts;
            try {
                contacts = groupService.findGroupContactsByName(user, userStateInfo, message);
            } catch (Exception ex) {
                return ex.getMessage();
            }
            userStateInfo.clear();
            return formatContacts(contacts);
        }));
        List<State> searchKinds = List.of(State.SEARCH_NAME, State.SEARCH_PHONE);
        searchKinds.forEach(state -> registerHandler(state, (user, message) -> {
            StateInfo userStateInfo = userStateService.getUserState(user);
            Set<Contact> contacts;
            try {
                contacts = contactService.searchByArgument(user, userStateInfo, message);
            } catch (Exception ex) {
                return ex.getMessage();
            }
            userStateInfo.clear();
            return formatContacts(contacts);
        }));
    }

    /**
     * Регистрация состояний и их обработчиков для фильтрации
     */
    private void registerFilterCommands() {
        registerHandler(State.FILTER_KIND, ((user, field) -> {
            ContactFilterKind kind =
                    (ContactFilterKind) utils.getEnumKindByField(ContactFilterKind.values(), field);
            switch (kind) {
                case BY_AGE -> {
                    StateInfo userStateInfo = userStateService.getUserState(user);
                    userStateInfo.setState(State.FILTER_AGE);
                    return messages.getMessageByKey("filter.age");
                }
                case BY_GENDER -> {
                    StateInfo userStateInfo = userStateService.getUserState(user);
                    userStateInfo.setState(State.FILTER_GENDER);
                    return messages.getMessageByKey("filter.gender");
                }
                case BY_BLOCK -> {
                    StateInfo userStateInfo = userStateService.getUserState(user);
                    userStateInfo.setState(State.FILTER_BLOCK);
                    return messages.getMessageByKey("filter.block");
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
            return messages.getMessageByKey("filter.age.kind");
        }));
        List<State> states = List.of(State.FILTER_AGE_KIND, State.FILTER_BLOCK, State.FILTER_GENDER);
        states.forEach(state -> registerHandler(state, ((user, message) -> {
            StateInfo userStateInfo = userStateService.getUserState(user);
            Set<Contact> contacts;
            try {
                contacts = contactService.filterByKind(user, userStateInfo, message);
            } catch (Exception ex) {
                return ex.getMessage();
            }
            userStateInfo.clear();
            return formatContacts(contacts);
        })));
    }

    /**
     * Регистрация состояний и их обработчиков для сортировки
     */
    private void registerSortCommands() {
        registerHandler(State.SORT_KIND, ((user, field) -> {
            ContactSortKind kind = (ContactSortKind) utils.getEnumKindByField(ContactSortKind.values(), field);
            switch (kind) {
                case BY_NAME -> {
                    StateInfo userStateInfo = userStateService.getUserState(user);
                    userStateInfo.setState(State.SORT_NAME);
                    return messages.getMessageByKey("sort.kind.type");
                }
                case BY_AGE -> {
                    StateInfo userStateInfo = userStateService.getUserState(user);
                    userStateInfo.setState(State.SORT_AGE);
                    return messages.getMessageByKey("sort.kind.type");
                }
            }
            throw new BadArgumentException();
        }));
        List<State> states = List.of(State.SORT_NAME, State.SORT_AGE);
        states.forEach(state -> registerHandler(state, ((user, message) -> {
            StateInfo userStateInfo = userStateService.getUserState(user);
            Set<Contact> contacts;
            try {
                SortDirectionKind kind =
                        (SortDirectionKind) utils.getEnumKindByField(SortDirectionKind.values(), message);
                contacts = contactService.sortByKind(user, userStateInfo, kind);
            } catch (Exception ex) {
                return ex.getMessage();
            }
            userStateInfo.clear();
            return formatContacts(contacts);
        })));
    }

    /**
     * Получить информацию по всем контактам в человеко читаемом формате для пользователя
     *
     * @param contacts контакты
     */
    private String formatContacts(Set<Contact> contacts) {
        return contacts.isEmpty()
                ? messages.getMessageByKey("commons.empty")
                : contacts.stream()
                    .map(contactMapper::format)
                    .collect(Collectors.joining("\n\n"));
    }

    /**
     * Получить информацию по всем группам в человеко читаемом формате для пользователя
     *
     * @param groups группы
     */
    private String formatGroups(Set<Group> groups) {
        return groups.isEmpty()
                ? messages.getMessageByKey("commons.empty")
                : groups.stream()
                    .map(groupMapper::format)
                    .collect(Collectors.joining("\n\n"));
    }

}
