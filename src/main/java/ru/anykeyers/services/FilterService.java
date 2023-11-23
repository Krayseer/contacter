package ru.anykeyers.services;

import ru.anykeyers.contexts.Messages;
import ru.anykeyers.dataOperations.kinds.FilteringKind;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.ContactRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FilterService {

    private final ContactRepository contactRepository;

    private final Messages messages;

    public FilterService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
        this.messages = new Messages();
    }

    /**
     * Общий метод фильтрации контактов
     * @param user текущий пользователб
     * @param value значение для фильтрации
     * @param type поле фильтрации
     * @return строка с контактами
     */
    public String filterContacts(User user, String value, FilteringKind type) {
        List<Contact> resultContacts = new ArrayList<>();
        Set<Contact> userContacts = contactRepository.findByUsername(user.getUsername());
        switch (type) {
            case GENDER -> {
                if (!value.equals("Мужчина") && !value.equals("Женщина")) {
                    return messages.getMessageByKey("contact.bad-gender-value");
                } else {
                    resultContacts = filterContactsByGender(userContacts, value);
                }
            }
            case BLOCK -> resultContacts = filterContactsByBlock(userContacts, true);
            case UNBLOCK -> resultContacts = filterContactsByBlock(userContacts, false);
            case AGE -> {
                String[] expressionAndAge = value.trim().split(",\\s*");
                try {
                    String expression = expressionAndAge[0];
                    int age = Integer.parseInt(expressionAndAge[1]);
                    if (!expression.equals(">") && !expression.equals("<") && !expression.equals("=")) {
                        return messages.getMessageByKey("contact.bad-expression-value");
                    }
                    resultContacts = filterContactsByAge(userContacts, expression, age);
                } catch (NumberFormatException e) {
                    return messages.getMessageByKey("contact.bad-age-value");
                }
            }
        }
        if (resultContacts.size() > 0) {
            return resultContacts.stream().map(contact -> contact.getContactString(contact)).collect(Collectors.joining("\n"));
        } else {
            return messages.getMessageByKey("contacts.not-searched");
        }
    }

    /**
     * Фильтрует контакты по возрасту
     * @param userContacts контакты пользователя
     * @param expression выражение(>,<,=)
     * @param age возраст фильтрации
     * @return список контактов
     */
    private List<Contact> filterContactsByAge(Set<Contact> userContacts, String expression, int age) {
        if (expression.equals(">")) {
            return userContacts.stream().filter(contact -> contact.getAge() > age).toList();
        } else if (expression.equals("<")) {
            return userContacts.stream().filter(contact -> contact.getAge() < age).toList();
        } else {
            return userContacts.stream().filter(contact -> contact.getAge() == age).toList();
        }
    }

    /**
     * Фильтрует контакты по блокировке
     * @param userContacts контакты пользователя
     * @param byBlock блокированные или разблокированные
     * @return список контактов
     */
    private List<Contact> filterContactsByBlock(Set<Contact> userContacts, boolean byBlock) {
        if (byBlock) {
            return userContacts.stream().filter(contact -> contact.getBlock().equals("BLOCK")).toList();
        } else {
            return userContacts.stream().filter(contact -> contact.getBlock().equals("UNBLOCK")).toList();
        }
    }

    /**
     * Фильтрует контакты по гендеру
     * @param userContacts контакты пользователя
     * @param value значение для фильтрации
     * @return список отфильтрованных контактов
     */
    private List<Contact> filterContactsByGender(Set<Contact> userContacts, String value) {
        return userContacts.stream().filter(contact -> contact.getGender().equals(value)).toList();
    }
}
