package ru.anykeyers.service;

import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.domain.entity.Group;
import ru.anykeyers.domain.entity.User;

import java.util.Set;

/**
 * Сервис для работы с группами
 */
public interface GroupService {

    /**
     * Найти все группы пользователя
     *
     * @param user пользователь
     * @return список групп
     */
    Set<Group> findAll(User user);

    /**
     * Найти все контакты в группе
     *
     * @param user пользователь
     * @return список контактов
     */
    Set<Contact> findAllGroupContacts(User user, String groupName);

    /**
     * Найти контакты в группе по имени
     *
     * @param user          пользователь
     * @param userStateInfo информация о состоянии пользователя
     * @param contactName   имя контакта
     * @return список контактов
     */
    Set<Contact> findGroupContactsByName(User user, StateInfo userStateInfo, String contactName);

    /**
     * Существует ли группа у пользователя
     *
     * @param user      пользователь
     * @param groupName имя контакта
     * @return {@code true}, если существует, иначе {@code false}
     */
    boolean existsGroup(User user, String groupName);

    /**
     * Добавляет группу в список групп пользователя
     *
     * @param user      пользователь
     * @param groupName название группы
     */
    void addGroup(User user, String groupName);

    /**
     * Изменить состояние группы пользователя
     * <ol>
     *     <li>Изменить название группы</li>
     *     <li>Добавить контакт в группу</li>
     *     <li>Удалить контакт из группы</li>
     * </ol>
     *
     * @param user     пользователь
     * @param newValue новое значение
     */
    void editGroup(User user, StateInfo userStateInfo, String newValue);

    /**
     * Удалить группу пользователя
     *
     * @param user      пользователь
     * @param groupName название группы
     */
    void deleteGroup(User user, String groupName);

}
