package ru.anykeyers.service.impl.contact;

import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.exception.InvalidUserStateException;
import ru.anykeyers.processor.state.domain.State;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Сервис по поиску контактов
 */
class SearchContactService {

    /**
     * Найти контакты по состоянию пользователя и подстроке
     *
     * @param contacts  контакты
     * @param userState информация о состоянии пользователя
     * @param value     подстрока
     * @return список контактов
     */
    Set<Contact> searchContacts(Set<Contact> contacts, State userState, String value) {
        return contacts.stream()
                .filter(getContactPredicateByState(userState, value))
                .collect(Collectors.toSet());
    }

    /**
     * Получить предикат фильтрации для контактов
     *
     * @param userState состояние пользователя
     * @param value     тип действия
     */
    private Predicate<Contact> getContactPredicateByState(State userState, String value) {
        switch (userState) {
            case SEARCH_NAME -> {
                return contact -> contact.getName() != null && contact.getName().contains(value);
            }
            case SEARCH_PHONE -> {
                return contact -> contact.getPhoneNumber() != null && contact.getPhoneNumber().contains(value);
            }
        }
        throw new InvalidUserStateException();
    }

}
