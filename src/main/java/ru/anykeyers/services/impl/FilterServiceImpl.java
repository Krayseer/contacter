package ru.anykeyers.services.impl;

import ru.anykeyers.contexts.Messages;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.Gender;
import ru.anykeyers.domain.User;
import ru.anykeyers.domain.kinds.filter.FilterBlockKind;
import ru.anykeyers.domain.kinds.filter.FilterAgeKind;
import ru.anykeyers.domain.kinds.filter.FilterGenderKind;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.services.FilterService;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Реализация сервиса фильтрации контактов
 */
public class FilterServiceImpl implements FilterService {

    private final ContactRepository contactRepository;

    private final Messages messages;

    public FilterServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
        this.messages = new Messages();
    }

    @Override
    public String filterByUserStateAndKind(User user, String value) {
        String result = "";
        switch (user.getState()) {
            case FILTER_AGE_KIND -> result = filterByAgeKind(user, value);
            case FILTER_GENDER -> result = filterByGender(user, value);
            case FILTER_BLOCK -> result = filterByBlock(user, value);
        }
        return result.isEmpty()
                ? messages.getMessageByKey("commons.empty")
                : result;
    }

    /**
     * Отфильтровать контакты пользователя по возрасту
     * @param user пользователь
     * @param value тип фильтра по возрасту
     * @return результат фильтрации
     */
    private String filterByAgeKind(User user, String value) {
        Predicate<Contact> agePredicate = getAgePredicate(user, value);
        return applyFilter(agePredicate, user);
    }

    /**
     * Получить фильтр по состоянию пользователя и выбранному типу фильтрации по возрасту
     * @param user пользователь
     * @param value тип фильтрации
     * @return фильтр
     */
    private Predicate<Contact> getAgePredicate(User user, String value) {
        int userAge = Integer.parseInt(user.getEditInfo());
        switch (value) {
            case FilterAgeKind.MORE -> {
                return contact -> contact.getAge() > userAge;
            }
            case FilterAgeKind.LESS -> {
                return contact -> contact.getAge() < userAge;
            }
            case FilterAgeKind.EQUALS -> {
                return contact -> contact.getAge() == userAge;
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * Отфильтровать контакты пользователя по полу
     * @param user пользователь
     * @param value тип фильтра по полу
     * @return результат фильтрации
     */
    private String filterByGender(User user, String value) {
        Predicate<Contact> genderPredicate = getGenderPredicate(value);
        return applyFilter(genderPredicate, user);
    }

    /**
     * Получить фильтр по полу
     * @param value тип фильтра по полу
     * @return фильтр
     */
    private Predicate<Contact> getGenderPredicate(String value) {
        switch (value) {
            case FilterGenderKind.MAN -> {
                return contact -> contact.getGender() != null && contact.getGender().equals(Gender.MAN);
            }
            case FilterGenderKind.WOMAN -> {
                return contact -> contact.getGender() != null && contact.getGender().equals(Gender.WOMAN);
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * Отфильтровать контакты пользователя по блокировке
     * @param user пользователь
     * @param value тип фильтра по блокировке
     * @return результат фильтрации
     */
    public String filterByBlock(User user, String value) {
        Predicate<Contact> blockPredicate = getBlockPredicate(value);
        return applyFilter(blockPredicate, user);
    }

    /**
     * Получить фильтр по блокировке
     * @param value тип фильтра по блокировке
     * @return фильтр
     */
    private Predicate<Contact> getBlockPredicate(String value) {
        switch (value) {
            case FilterBlockKind.BLOCKED -> {
                return Contact::isBlock;
            }
            case FilterBlockKind.NON_BLOCKED -> {
                return contact -> !contact.isBlock();
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * Применить фильтр для поиска по контактам
     * @param predicate фильтр
     * @param user пользователь
     * @return результат фильтрации
     */
    private String applyFilter(Predicate<Contact> predicate, User user) {
        if (predicate != null) {
            Set<Contact> contacts = contactRepository.findByUsername(user.getUsername());
            if (contacts == null) {
                return messages.getMessageByKey("commons.empty");
            }
            return contactRepository.findByUsername(user.getUsername()).stream()
                    .filter(predicate)
                    .map(Contact::getInfo)
                    .collect(Collectors.joining("\n\n"));
        } else {
            return messages.getMessageByKey("commons.bad-argument");
        }
    }

}
