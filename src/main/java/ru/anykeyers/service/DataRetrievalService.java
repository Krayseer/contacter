package ru.anykeyers.service;

import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.domain.entity.Group;
import ru.anykeyers.domain.entity.User;

import java.util.Set;

/**
 * Сервис по получению полной информации контактов, групп
 */
public interface DataRetrievalService {

    /**
     * Получить все контакты пользователя
     *
     * @param user пользователь
     * @return список контактов
     */
    Set<Contact> getAllContacts(User user);

    /**
     * Получить все группы пользователя
     *
     * @param user пользователь
     * @return список групп
     */
    Set<Group> getAllGroups(User user);

    /**
     * Получить все контакты в группе
     *
     * @param user пользователь
     * @return список контактов
     */
    Set<Contact> getAllGroupContacts(User user, String groupName);

}
