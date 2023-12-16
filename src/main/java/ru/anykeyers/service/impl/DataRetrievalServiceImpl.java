package ru.anykeyers.service.impl;

import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.domain.entity.Group;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.exception.group.GroupNotExistsException;
import ru.anykeyers.repository.ContactRepository;
import ru.anykeyers.repository.GroupRepository;
import ru.anykeyers.service.DataRetrievalService;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * Реализация сервиса {@link DataRetrievalService}
 */
public class DataRetrievalServiceImpl implements DataRetrievalService {

    private final ContactRepository contactRepository;

    private final GroupRepository groupRepository;

    public DataRetrievalServiceImpl(ContactRepository contactRepository,
                                    GroupRepository groupRepository) {
        this.contactRepository = contactRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    public Set<Contact> getAllContacts(User user) {
        return contactRepository.findByUsername(user.getUsername());
    }

    @Override
    public Set<Group> getAllGroups(User user) {
        return groupRepository.findByUsername(user.getUsername());
    }

    @Override
    public Set<Contact> getAllGroupContacts(User user, String groupName) {
        Optional<Group> group = groupRepository.getByUsernameAndName(user.getUsername(), groupName);
        if (group.isEmpty()) {
            throw new GroupNotExistsException(groupName);
        }
        return group.map(Group::getContacts).orElse(Collections.emptySet());
    }

}
