package ru.anykeyers.service;

import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.domain.entity.User;

import java.util.Set;

/**
 * Сервис по сортировке контактов
 */
public interface SortContactService {

    /**
     * Отсортировать пользователей в зависимости от состояния пользователя и введенного значения
     *
     * @param user пользователь
     * @param value аргумент сортировки
     * @return список отсортированных контактов
     */
    Set<Contact> sortByUserStateAndKind(User user, StateInfo userStateInfo, String value);

}
