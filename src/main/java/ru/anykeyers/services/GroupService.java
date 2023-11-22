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
 * Класс, отвечающий за логику при работе с группами
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
     * Добавляет группу в список групп пользователя
     */
    public String addGroup(User user, String groupName) {
        if (groupRepository.existsByUsernameAndName(user.getUsername(), groupName)) {
            return messages.getMessageByKey("group.already-exists", groupName);
        }
        Group group = new Group(user.getUsername(), UUID.randomUUID().toString(), groupName, new HashSet<>());
        groupRepository.saveOrUpdate(group);
        return messages.getMessageByKey("group.successful-save", groupName);
    }

    public String editGroupName(User user, String groupName, String newGroupName) {
        Group group = groupRepository.findByUsernameAndName(user.getUsername(), groupName);
        if (group == null) {
            return messages.getMessageByKey("group.not-exists", groupName);
        }
        group.setName(newGroupName);
        groupRepository.saveOrUpdate(group);
        return messages.getMessageByKey("group.successful-edit-name", groupName, newGroupName);
    }

    public String addContactInGroup(User user, String groupName, String contactName) {
        return performContactOperation(user, groupName, contactName, Group::addContactInGroup, "group.successful-contact-add");
    }

    public String deleteContactFromGroup(User user, String groupName, String contactName) {
        return performContactOperation(user, groupName, contactName, Group::deleteContactFromGroup, "group.successful-contact-delete");
    }

    public String deleteGroup(User user, String groupName) {
        Group group = groupRepository.findByUsernameAndName(user.getUsername(), groupName);
        if (group == null) {
            return messages.getMessageByKey("group.not-exists", groupName);
        }
        groupRepository.delete(group);
        return messages.getMessageByKey("group-successful-delete", groupName);
    }

    /**
     * Обработка операции связанной с контактами
     * @param user пользователь
     * @param groupName название группы
     * @param contactName имя контакта
     * @param operation операция, которую нужно выполнить с контактом
     * @param successMessageKey ключ сообщения успешного выполнения
     * @return сообщение результата обработки
     */
    private String performContactOperation(User user, String groupName, String contactName, BiConsumer<Group, Contact> operation, String successMessageKey) {
        Group group = groupRepository.findByUsernameAndName(user.getUsername(), groupName);
        if (group == null) {
            return messages.getMessageByKey("group.not-exists", groupName);
        }

        Contact contact = contactRepository.findByUsernameAndName(user.getUsername(), contactName);
        if (contact == null) {
            return messages.getMessageByKey("contact.not-exists", contactName);
        }

        operation.accept(group, contact);
        groupRepository.saveOrUpdate(group);
        return messages.getMessageByKey(successMessageKey, contactName, groupName);
    }

}
