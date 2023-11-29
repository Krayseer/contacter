package ru.anykeyers.services;

import ru.anykeyers.contexts.Messages;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.Group;
import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.repositories.GroupRepository;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * Сервис по работе с группами
 */
public class GroupService {

    private final GroupRepository groupRepository;

    private final ContactRepository contactRepository;

    private final Messages messages;

    public GroupService(GroupRepository groupRepository,
                        ContactRepository contactRepository) {
        this.groupRepository = groupRepository;
        this.contactRepository = contactRepository;
        messages = new Messages();
    }

    /**
     * Получить все группы пользователя
     * @param username имя пользователя
     * @return список групп
     */
    public Set<Group> getAllGroups(String username) {
        return groupRepository.findByUsername(username);
    }

    /**
     * Получить контакты группы
     * @param username имя пользователя
     * @param groupName название группы
     * @return список контактов группы
     */
    public Set<Contact> getGroupContacts(String username, String groupName) {
        return groupRepository.findByUsernameAndName(username, groupName).getContacts();
    }

    /**
     * Добавляет группу в список групп пользователя
     * @param user пользователь
     * @param groupName название группы
     * @return результат добавления группы
     */
    public String addGroup(User user, String groupName) {
        if (groupRepository.existsByUsernameAndName(user.getUsername(), groupName)) {
            return messages.getMessageByKey("group.already-exists", groupName);
        }
        Group group = new Group(user.getUsername(), UUID.randomUUID().toString(), groupName, new HashSet<>());
        groupRepository.saveOrUpdate(group);
        return messages.getMessageByKey("group.successful-save", groupName);
    }

    /**
     * Изменить состояние группы пользователя
     * <ol>
     *     <li>Изменить название группы</li>
     *     <li>Добавить контакт в группу</li>
     *     <li>Удалить контакт из группы</li>
     * </ol>
     * @param user пользователь
     * @param newValue новое значение
     * @return результат изменения состояния группы
     */
    public String editGroup(User user, String newValue) {
        String groupNameToEdit = user.getGroupNameToEdit();
        Group groupToEdit = groupRepository.findByUsernameAndName(user.getUsername(), groupNameToEdit);

        switch (user.getState()) {
            case EDIT_GROUP_NAME -> groupToEdit.setName(newValue);
            case EDIT_GROUP_ADD_CONTACT -> {
                Contact contact = contactRepository.findByUsernameAndName(user.getUsername(), newValue);
                groupToEdit.addContactInGroup(contact);
            }
            case EDIT_GROUP_DELETE_CONTACT -> {
                Contact contact = contactRepository.findByUsernameAndName(user.getUsername(), newValue);
                groupToEdit.deleteContactFromGroup(contact);
            }
            default -> messages.getMessageByKey("field.invalid");
        }

        groupRepository.saveOrUpdate(groupToEdit);
        return messages.getMessageByKey("group.successful-edit-name", groupNameToEdit);
    }

    /**
     * Удалить группу пользователя
     * @param user пользователь
     * @param groupName название группы
     * @return результат удаления группы
     */
    public String deleteGroup(User user, String groupName) {
        Group group = groupRepository.findByUsernameAndName(user.getUsername(), groupName);
        if (group == null) {
            return messages.getMessageByKey("group.not-exists", groupName);
        }
        groupRepository.delete(group);
        return messages.getMessageByKey("group.successful-delete", groupName);
    }

}
