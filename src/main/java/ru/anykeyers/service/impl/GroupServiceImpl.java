package ru.anykeyers.service.impl;

import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.domain.entity.Group;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.exception.BadArgumentException;
import ru.anykeyers.exception.contact.ContactNotExistsException;
import ru.anykeyers.exception.group.ContactAlreadyExistsInGroupException;
import ru.anykeyers.exception.group.ContactNotExistsInGroupException;
import ru.anykeyers.exception.group.GroupAlreadyExistsException;
import ru.anykeyers.exception.group.GroupNotExistsException;
import ru.anykeyers.repository.ContactRepository;
import ru.anykeyers.repository.GroupRepository;
import ru.anykeyers.service.GroupService;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Реализация сервиса {@link GroupService}
 */
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;

    private final ContactRepository contactRepository;
    public GroupServiceImpl(GroupRepository groupRepository,
                            ContactRepository contactRepository) {
        this.groupRepository = groupRepository;
        this.contactRepository = contactRepository;
    }

    @Override
    public Set<Group> findAll(User user) {
        return groupRepository.findByUsername(user.getUsername());
    }

    @Override
    public Set<Contact> findAllGroupContacts(User user, String groupName) {
        Optional<Group> group = groupRepository.getByUsernameAndName(user.getUsername(), groupName);
        if (group.isEmpty()) {
            throw new GroupNotExistsException(groupName);
        }
        return group.map(Group::getContacts).orElse(Collections.emptySet());
    }

    @Override
    public Set<Contact> findGroupContactsByName(User user, StateInfo userStateInfo, String contactName) {
        Set<Contact> contacts = findAllGroupContacts(user, userStateInfo.getEditInfo());
        return contacts.stream()
                .filter(contact -> contact.getName() != null && contact.getName().contains(contactName))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean existsGroup(User user, String groupName) {
        return groupRepository.existsByUsernameAndName(user.getUsername(), groupName);
    }

    @Override
    public void addGroup(User user, String groupName) {
        if (groupRepository.existsByUsernameAndName(user.getUsername(), groupName)) {
            throw new GroupAlreadyExistsException(groupName);
        }
        Group group = new Group(user.getUsername(), groupName);
        groupRepository.saveOrUpdate(group);
    }

    @Override
    public void editGroup(User user, StateInfo userStateInfo, String newValue) {
        String groupNameToEdit = userStateInfo.getEditInfo();
        Optional<Group> groupToEdit = groupRepository.getByUsernameAndName(user.getUsername(), groupNameToEdit);
        if (groupToEdit.isEmpty()) {
            throw new GroupNotExistsException(groupNameToEdit);
        }
        switch (userStateInfo.getState()) {
            case EDIT_GROUP_NAME -> groupToEdit.get().setName(newValue);
            case EDIT_GROUP_ADD_CONTACT -> {
                Optional<Contact> contact = contactRepository.getByUsernameAndName(user.getUsername(), newValue);
                if (contact.isEmpty()) {
                    throw new ContactNotExistsException(newValue);
                }
                if (getContactInGroupByName(groupToEdit.get(), newValue).isPresent()) {
                    throw new ContactAlreadyExistsInGroupException(newValue);
                }
                groupToEdit.get().addContactInGroup(contact.get());
            }
            case EDIT_GROUP_DELETE_CONTACT -> {
                Optional<Contact> contact = contactRepository.getByUsernameAndName(user.getUsername(), newValue);
                if (contact.isEmpty()) {
                    throw new ContactNotExistsException(newValue);
                }
                boolean deleteResult = groupToEdit.get().deleteContactFromGroup(contact.get());
                if (!deleteResult) {
                    throw new ContactNotExistsInGroupException(contact.get().getName());
                }
            }
            default -> throw new BadArgumentException();
        }
        groupRepository.saveOrUpdate(groupToEdit.get());
    }

    @Override
    public void deleteGroup(User user, String groupName) {
        Optional<Group> group = groupRepository.getByUsernameAndName(user.getUsername(), groupName);
        if (group.isEmpty()) {
            throw new GroupNotExistsException(groupName);
        }
        groupRepository.delete(group.get());
    }

    /**
     * Получить контакт в группе по имени
     *
     * @param group группа
     * @param contactName имя контакта
     */
    private Optional<Contact> getContactInGroupByName(Group group, String contactName) {
        return Optional.ofNullable(group.getContacts())
                .flatMap(contacts -> contacts.stream()
                        .filter(contact -> Objects.equals(contact.getName(), contactName))
                        .findFirst());
    }

}
