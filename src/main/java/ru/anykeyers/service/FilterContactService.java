package ru.anykeyers.service;

import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.domain.entity.User;

import java.util.Set;

/**
 * Сервис по фильтрации контактов
 */
public interface FilterContactService {

    /**
     * Отфильтровать контакты в зависимости от состояния пользователя и введенного значения
     *
     * @param user пользователь
     * @param value значение, по которому будет происходить фильтрация
     * @return список отфильтрованных контактов
     */
    Set<Contact> filterByUserStateAndKind(User user, StateInfo userStateInfo, String value);

}
