package ru.anykeyers.services.impl;

import ru.anykeyers.contexts.Messages;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.Group;
import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.repositories.GroupRepository;
import ru.anykeyers.services.GroupService;

/**
 * Сервис по работе с группами
 */
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;

    private final ContactRepository contactRepository;

    private final Messages messages;

    public GroupServiceImpl(GroupRepository groupRepository,
                            ContactRepository contactRepository) {
        this.groupRepository = groupRepository;
        this.contactRepository = contactRepository;
        messages = new Messages();
    }

    @Override
    public boolean existsGroup(User user, String groupName) {
        return groupRepository.existsByUsernameAndName(user.getUsername(), groupName);
    }

    @Override
    public String addGroup(User user, String groupName) {
        if (groupRepository.existsByUsernameAndName(user.getUsername(), groupName)) {
            return messages.getMessageByKey("group.already-exists", groupName);
        }
        Group group = new Group(user.getUsername(), groupName);
        groupRepository.saveOrUpdate(group);
        return messages.getMessageByKey("group.successful-save", groupName);
    }

    @Override
    public String editGroup(User user, String newValue) {
        String groupNameToEdit = user.getGroupNameToEdit();
        Group groupToEdit = groupRepository.findByUsernameAndName(user.getUsername(), groupNameToEdit);

        switch (user.getState()) {
            case EDIT_GROUP_NAME -> groupToEdit.setName(newValue);
            case EDIT_GROUP_ADD_CONTACT -> {
                Contact contact = contactRepository.findByUsernameAndName(user.getUsername(), newValue);
                if (contact == null) {
                    return messages.getMessageByKey("contact.not-exists", newValue);
                }
                if (groupRepository.findContactInGroupByName(groupToEdit, newValue) != null) {
                    return messages.getMessageByKey("group.contact.already-exists", newValue);
                }
                groupToEdit.addContactInGroup(contact);
            }
            case EDIT_GROUP_DELETE_CONTACT -> {
                Contact contact = contactRepository.findByUsernameAndName(user.getUsername(), newValue);
                boolean deleteResult = groupToEdit.deleteContactFromGroup(contact);
                if (!deleteResult) {
                    return messages.getMessageByKey("group.contact.not-exists", newValue);
                }
            }
            default -> messages.getMessageByKey("argument.bad");
        }

        groupRepository.saveOrUpdate(groupToEdit);
        return messages.getMessageByKey("group.successful-edit-name", groupNameToEdit);
    }

    @Override
    public String deleteGroup(User user, String groupName) {
        Group group = groupRepository.findByUsernameAndName(user.getUsername(), groupName);
        if (group == null) {
            return messages.getMessageByKey("group.not-exists", groupName);
        }
        groupRepository.delete(group);
        return messages.getMessageByKey("group.successful-delete", groupName);
    }

}
