package ru.anykeyers.services;

import ru.anykeyers.contexts.Messages;
import ru.anykeyers.dataOperations.kinds.SortingKind;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.ContactRepository;

import java.util.*;
import java.util.stream.Collectors;

public class SortService {

    private final ContactRepository contactRepository;

    private final Messages messages;

    public SortService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
        this.messages = new Messages();
    }

    /**
     * Сортирует контакты
     * @param user текущий пользователь
     * @param value значение сортировки
     * @param type Поле сортировки
     * @return строка с контактами
     */
    public String sortContacts(User user, String value, SortingKind type) {
        Set<Contact> userContact = contactRepository.findByUsername(user.getUsername());
        List<Contact> resultContacts = new ArrayList<>(userContact);
        switch (type) {
            case NAME -> {
                if (!value.equals("asc") && !value.equals("desc")) {
                    return messages.getMessageByKey("contact.bad-ordering-value");
                } else if (value.equals("asc")) {
                    resultContacts.sort(Comparator.comparing(Contact::getName));
                }
                else {
                    resultContacts.sort(Comparator.comparing(Contact::getName).reversed());
                }
            }
            case AGE -> {
                if (!value.equals("asc") && !value.equals("desc")) {
                    return messages.getMessageByKey("contact.bad-ordering-value");
                } else if (value.equals("asc")) {
                    resultContacts.sort(Comparator.comparing(Contact::getAge));
                }
                else {
                    resultContacts.sort(Comparator.comparing(Contact::getAge).reversed());
                }
            }
        }
        if (resultContacts.size() > 0) {
            return resultContacts.stream().map(contact -> contact.getContactString(contact)).collect(Collectors.joining("\n"));
        } else {
            return messages.getMessageByKey("contacts.not-searched");
        }

    }

}
