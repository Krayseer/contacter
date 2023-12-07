package ru.anykeyers.services.impl;

import ru.anykeyers.contexts.Messages;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.Group;
import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.repositories.GroupRepository;
import ru.anykeyers.services.SearchService;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Реализация сервиса по поиску контактов и групп пользователя
 */
public class SearchServiceImpl implements SearchService {

    private final ContactRepository contactRepository;

    private final GroupRepository groupRepository;

    private final Messages messages;

    public SearchServiceImpl(ContactRepository contactRepository,
                             GroupRepository groupRepository) {
        this.contactRepository = contactRepository;
        this.groupRepository = groupRepository;
        this.messages = new Messages();
    }

    @Override
    public String findAllContacts(User user) {
        Set<Contact> contacts = contactRepository.findByUsername(user.getUsername());
        return contacts == null || contacts.isEmpty()
                ? messages.getMessageByKey("commons.empty")
                : contacts.stream()
                    .map(Contact::getInfo)
                    .collect(Collectors.joining("\n\n"));
    }

    @Override
    public String findAllGroups(User user) {
        Set<Group> groups = groupRepository.findByUsername(user.getUsername());
        return groups == null || groups.isEmpty()
                ? messages.getMessageByKey("commons.empty")
                : groups.stream()
                    .map(Group::getInfo)
                    .collect(Collectors.joining("\n"));
    }

    @Override
    public String findInfoByUserStateAndKind(User user, String value) {
        switch (user.getState()) {
            case SEARCH_NAME -> {
                return findContactInfoByUsernameAndName(user, value);
            }
            case SEARCH_PHONE -> {
                return findContactInfoByUsernameAndPhoneNumber(user, value);
            }
            case SEARCH_GROUP_CONTACTS -> {
                return findGroupContactsInfoByUsernameAndName(user, value);
            }
        }
        String errorMessage = messages.getMessageByKey("state.type.not-exists");
        throw new RuntimeException(errorMessage);
    }

    /**
     * Получить информацию контакта по имени пользователя и имени контакта
     * @param user пользотватель
     * @param name имя контакта
     * @return результат поиска
     */
    private String findContactInfoByUsernameAndName(User user, String name) {
        Contact contact = contactRepository.findByUsernameAndName(user.getUsername(), name);
        return contact == null
                ? messages.getMessageByKey("contact.not-exists", name)
                : contact.getInfo();
    }

    /**
     * Получить информацию контакта по имени пользователя и номеру телефона контакта
     * @param user пользователь
     * @param phoneNumber номер телефона, по которому производится поиск
     * @return результат поиска
     */
    private String findContactInfoByUsernameAndPhoneNumber(User user, String phoneNumber) {
        Contact contact = contactRepository.findByUsernameAndPhoneNumber(user.getUsername(), phoneNumber);
        return contact == null
                ? messages.getMessageByKey("contact.not-exists.by-phone", phoneNumber)
                : contact.getInfo();
    }

    /**
     * Получить информацию контактов в группе по имени пользователя и названию группы
     * @param user пользователь
     * @param groupName название группы
     * @return результат поиска
     */
    private String findGroupContactsInfoByUsernameAndName(User user, String groupName) {
        Group group = groupRepository.findByUsernameAndName(user.getUsername(), groupName);
        if (group == null) {
            return messages.getMessageByKey("group.not-exists", groupName);
        }
        return group.getContacts() == null || group.getContacts().isEmpty()
                ? messages.getMessageByKey("group.contacts.empty", groupName)
                : group.getContacts().stream()
                    .map(contact -> {
                        String phoneNumber = contact.getPhoneNumber() == null ? "не указан" : contact.getPhoneNumber();
                        return new Object[]{ contact.getName(), phoneNumber };
                    })
                    .map(contactInfo -> messages.getMessageByKey("contact.info", contactInfo))
                    .collect(Collectors.joining("\n\n"));
    }

}
