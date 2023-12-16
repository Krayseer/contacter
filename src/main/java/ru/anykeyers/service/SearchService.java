package ru.anykeyers.service;

import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.domain.entity.User;

import java.util.Set;

/**
 * Сервис по поиску контактов и групп
 */
public interface SearchService {

    /**
     * Получить все контакты, по вхождении подстроки 'value'
     *
     * @param value значение, по которому нужно произвести поиск
     * @return список контактов, удовлетворяющих критерию поиску
     */
    Set<Contact> findContactsByArgument(User user, StateInfo userStateInfo, String value);

}
