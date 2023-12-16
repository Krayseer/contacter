package ru.anykeyers.service.impl;

import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.domain.entity.Group;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.exception.InvalidUserStateException;
import ru.anykeyers.exception.group.GroupNotExistsException;
import ru.anykeyers.processor.state.domain.State;
import ru.anykeyers.repository.ContactRepository;
import ru.anykeyers.repository.GroupRepository;
import ru.anykeyers.service.SearchService;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Реализация сервиса {@link SearchService}
 */
public class SearchServiceImpl implements SearchService {

    private final ContactRepository contactRepository;

    private final GroupRepository groupRepository;

    public SearchServiceImpl(ContactRepository contactRepository,
                             GroupRepository groupRepository) {
        this.contactRepository = contactRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    public Set<Contact> findContactsByArgument(User user, StateInfo userStateInfo, String value) {
        Set<Contact> contacts;
        switch (userStateInfo.getState()) {
            case SEARCH_NAME, SEARCH_PHONE -> contacts = contactRepository.findByUsername(user.getUsername());
            case SEARCH_GROUP_CONTACTS_BY_NAME -> {
                Optional<Group> group = groupRepository.getByUsernameAndName(user.getUsername(), userStateInfo.getEditInfo());
                if (group.isEmpty()) {
                    throw new GroupNotExistsException(userStateInfo.getEditInfo());
                }
                contacts = group.get().getContacts();
            }
            default -> throw new InvalidUserStateException();
        }
        return contacts.stream()
                .filter(getContactPredicateByState(userStateInfo.getState(), value))
                .collect(Collectors.toSet());
    }

    /**
     * Получить предикат фильтрации для контактов
     *
     * @param userState состояние пользователя
     * @param value тип действия
     */
    private Predicate<Contact> getContactPredicateByState(State userState, String value) {
        switch (userState) {
            case SEARCH_NAME, SEARCH_GROUP_CONTACTS_BY_NAME -> {
                return contact -> contact.getName() != null && contact.getName().contains(value);
            }
            case SEARCH_PHONE -> {
                return contact -> contact.getPhoneNumber() != null && contact.getPhoneNumber().contains(value);
            }
        }
        throw new InvalidUserStateException();
    }

}
