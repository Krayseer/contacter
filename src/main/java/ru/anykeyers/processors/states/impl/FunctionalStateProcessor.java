package ru.anykeyers.processors.states.impl;

import ru.anykeyers.contexts.Messages;
import ru.anykeyers.processors.states.BaseStateProcessor;
import ru.anykeyers.processors.states.domain.State;
import ru.anykeyers.domain.kinds.filter.FilterKind;
import ru.anykeyers.domain.kinds.SearchKind;
import ru.anykeyers.domain.kinds.sort.SortKind;
import ru.anykeyers.services.AuthenticationService;
import ru.anykeyers.services.FilterService;
import ru.anykeyers.services.SearchService;
import ru.anykeyers.services.SortService;

import java.util.List;

/**
 * Обработчик состояний для получения обработанной информации (поиск, фильтрация, сортировка)
 */
public class FunctionalStateProcessor extends BaseStateProcessor {

    private final SearchService searchService;

    private final FilterService filterService;

    private final SortService sortService;

    private final AuthenticationService authenticationService;

    private final Messages messages;

    public FunctionalStateProcessor(AuthenticationService authenticationService,
                                    SearchService searchService,
                                    FilterService filterService,
                                    SortService sortService) {
        this.searchService = searchService;
        this.filterService = filterService;
        this.sortService = sortService;
        this.authenticationService = authenticationService;
        messages = new Messages();
        registerSearchStateHandlers();
        registerFilterCommands();
        registerSortCommands();
    }

    /**
     * Регистрация состояний и их обработчиков для поиска
     */
    private void registerSearchStateHandlers() {
        registerHandler(State.SEARCH_KIND, (user, message) -> {
            switch (message.toLowerCase()) {
                case SearchKind.SEARCH_ALL_CONTACTS -> {
                    return searchService.findAllContacts(user);
                }
                case SearchKind.SEARCH_BY_NAME -> {
                    user.setState(State.SEARCH_NAME);
                    authenticationService.saveOrUpdateUser(user);
                    return messages.getMessageByKey("search.contact.name");
                }
                case SearchKind.SEARCH_BY_PHONE -> {
                    user.setState(State.SEARCH_PHONE);
                    authenticationService.saveOrUpdateUser(user);
                    return messages.getMessageByKey("search.contact.phone-number");
                }
                case SearchKind.SEARCH_ALL_GROUPS -> {
                    return searchService.findAllGroups(user);
                }
                case SearchKind.SEARCH_GROUP_CONTACTS -> {
                    user.setState(State.SEARCH_GROUP_CONTACTS);
                    return messages.getMessageByKey("search.group.contacts");
                }
            }
            return messages.getMessageByKey("commons.bad-argument");
        });
        List<State> searchKinds = List.of(State.SEARCH_NAME, State.SEARCH_PHONE, State.SEARCH_GROUP_CONTACTS);
        searchKinds.forEach(state -> registerHandler(state, (user, message) -> {
            String result = searchService.findInfoByUserStateAndKind(user, message);
            user.clearState();
            authenticationService.saveOrUpdateUser(user);
            return result;
        }));
    }

    /**
     * Регистрация состояний и их обработчиков для фильтрации
     */
    private void registerFilterCommands() {
        registerHandler(State.FILTER_KIND, ((user, message) -> {
            switch (message.toLowerCase()) {
                case FilterKind.AGE -> {
                    user.setState(State.FILTER_AGE);
                    authenticationService.saveOrUpdateUser(user);
                    return messages.getMessageByKey("filter.age");
                }
                case FilterKind.GENDER -> {
                    user.setState(State.FILTER_GENDER);
                    authenticationService.saveOrUpdateUser(user);
                    return messages.getMessageByKey("filter.gender");
                }
                case FilterKind.BLOCK -> {
                    user.setState(State.FILTER_BLOCK);
                    authenticationService.saveOrUpdateUser(user);
                    return messages.getMessageByKey("filter.block");
                }
            }
            return messages.getMessageByKey("commons.bad-argument");
        }));
        registerHandler(State.FILTER_AGE, ((user, message) -> {
            if (!message.matches("\\d+")) {
                return messages.getMessageByKey("commons.number.invalid-format");
            }
            user.setEditInfo(message);
            user.setState(State.FILTER_AGE_KIND);
            authenticationService.saveOrUpdateUser(user);
            return messages.getMessageByKey("filter.age.kind");
        }));
        List<State> states = List.of(State.FILTER_AGE_KIND, State.FILTER_BLOCK, State.FILTER_GENDER);
        states.forEach(state -> {
            registerHandler(state, ((user, message) -> {
                String result = filterService.filterByUserStateAndKind(user, message);
                if (result.equals(messages.getMessageByKey("commons.bad-argument"))) {
                    return result;
                }
                user.clearState();
                authenticationService.saveOrUpdateUser(user);
                return result;
            }));
        });
    }

    /**
     * Регистрация состояний и их обработчиков для сортировки
     */
    private void registerSortCommands() {
        registerHandler(State.SORT_KIND, ((user, message) -> {
            switch (message.toLowerCase()) {
                case SortKind.NAME -> {
                    user.setState(State.SORT_NAME);
                    authenticationService.saveOrUpdateUser(user);
                    return messages.getMessageByKey("sort.kind.type");
                }
                case SortKind.AGE -> {
                    user.setState(State.SORT_AGE);
                    authenticationService.saveOrUpdateUser(user);
                    return messages.getMessageByKey("sort.kind.type");
                }
            }
            return messages.getMessageByKey("commons.bad-argument");
        }));
        List<State> states = List.of(State.SORT_NAME, State.SORT_AGE);
        states.forEach(state -> {
            registerHandler(state, ((user, message) -> {
                String result = sortService.sortByUserStateAndKind(user, message);
                if (result.equals(messages.getMessageByKey("commons.bad-argument"))) {
                    return result;
                }
                user.clearState();
                authenticationService.saveOrUpdateUser(user);
                return result;
            }));
        });
    }

}
