package ru.anykeyers.processor.state.impl;

import ru.anykeyers.common.Messages;
import ru.anykeyers.domain.Message;
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
            Enum<DataGetKind> kind = enumUtils.getEnumKindByField(DataGetKind.values(), field);
            StateInfo userStateInfo = userStateService.getUserState(user);
            if (kind.equals(DataGetKind.CONTACTS)) {
                Set<Contact> contacts = contactService.findAll(user);
                userStateInfo.clear();
                return formatContacts(contacts);
            } else if (kind.equals(DataGetKind.GROUPS)) {
                Set<Group> groups = groupService.findAll(user);
                userStateInfo.clear();
                return formatGroups(groups);
            } else if (kind.equals(DataGetKind.GROUP_CONTACTS)) {
                userStateInfo.setState(State.GET_GROUP_CONTACTS);
                return new Message(messages.getMessageByKey("get.group.contacts"));
            }
            throw new BadArgumentException();
        }));
        registerHandler(State.GET_GROUP_CONTACTS, ((user, message) -> {
            Set<Contact> contacts;
            try {
                contacts = groupService.findAllGroupContacts(user, message);
            } catch (Exception ex) {
                return new Message(ex.getMessage());
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
            Enum<ContactSearchKind> kind = enumUtils.getEnumKindByField(ContactSearchKind.values(), field);
            StateInfo userStateInfo = userStateService.getUserState(user);
            if (kind.equals(ContactSearchKind.BY_NAME)) {
                userStateInfo.setState(State.SEARCH_NAME);
                return messages.getMessageByKey("search.contact.name");
            } else if (kind.equals(ContactSearchKind.BY_PHONE)) {
                userStateInfo.setState(State.SEARCH_PHONE);
                return messages.getMessageByKey("search.contact.phone-number");
            } else if (kind.equals(ContactSearchKind.GROUP_CONTACTS_BY_NAME)) {
                userStateInfo.setState(State.SEARCH_GROUP_CONTACTS);
                return messages.getMessageByKey("search.group.contacts");
            }
            throw new BadArgumentException();
        });
        registerHandler(State.SEARCH_GROUP_CONTACTS, ((user, message) -> {
            StateInfo userStateInfo = userStateService.getUserState(user);
            userStateInfo.setState(State.SEARCH_GROUP_CONTACTS_BY_NAME);
            userStateInfo.setEditInfo(message);
            return new Message(messages.getMessageByKey("search.group.contacts.name"));
        }));
        registerHandler(State.SEARCH_GROUP_CONTACTS_BY_NAME, ((user, message) -> {
            StateInfo userStateInfo = userStateService.getUserState(user);
            Set<Contact> contacts;
            try {
                contacts = groupService.findGroupContactsByName(user, userStateInfo, message);
            } catch (Exception ex) {
                return new Message(ex.getMessage());
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
                return new Message(ex.getMessage());
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
            Enum<ContactFilterKind> kind = enumUtils.getEnumKindByField(ContactFilterKind.values(), field);
            StateInfo userStateInfo = userStateService.getUserState(user);
            if (kind.equals(ContactFilterKind.BY_AGE)) {
                userStateInfo.setState(State.FILTER_AGE);
                return messages.getMessageByKey("filter.age");
            } else if (kind.equals(ContactFilterKind.BY_GENDER)) {
                userStateInfo.setState(State.FILTER_GENDER);
                return messages.getMessageByKey("filter.gender");
            } else if (kind.equals(ContactFilterKind.BY_BLOCK)) {
                userStateInfo.setState(State.FILTER_BLOCK);
                return messages.getMessageByKey("filter.block");
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
                contacts = contactService.filterByKind(user, userStateInfo, message);
            } catch (Exception ex) {
                return new Message(ex.getMessage());
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
            Enum<ContactSortKind> kind = enumUtils.getEnumKindByField(ContactSortKind.values(), field);
            StateInfo userStateInfo = userStateService.getUserState(user);
            if (kind.equals(ContactSortKind.BY_NAME)) {
                userStateInfo.setState(State.SORT_NAME);
                return messages.getMessageByKey("sort.kind.type");
            } else if (kind.equals(ContactSortKind.BY_AGE)) {
                userStateInfo.setState(State.SORT_AGE);
                return messages.getMessageByKey("sort.kind.type");
            }
            throw new BadArgumentException();
        }));
        List<State> states = List.of(State.SORT_NAME, State.SORT_AGE);
        states.forEach(state -> registerHandler(state, ((user, message) -> {
            StateInfo userStateInfo = userStateService.getUserState(user);
            Set<Contact> contacts;
            try {
                Enum<SortDirectionKind> kind = enumUtils.getEnumKindByField(SortDirectionKind.values(), message);
                contacts = contactService.sortByKind(user, userStateInfo, kind);
            } catch (Exception ex) {
                return new Message(ex.getMessage());
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
    private Message formatContacts(Set<Contact> contacts) {
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
    private Message formatGroups(Set<Group> groups) {
        String groupsToText = groups.isEmpty()
                ? messages.getMessageByKey("commons.empty")
                : groups.stream()
                .map(groupMapper::format)
                .collect(Collectors.joining("\n\n"));
        return new Message(groupsToText);
    }

}
