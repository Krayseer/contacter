package ru.anykeyers.service;

import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.domain.entity.User;

/**
 * Сервис для работы с контактами
 */
public interface ContactService {

    /**
     * Существует ли контакт у пользователя
     *
     * @param user пользователь
     * @param contactName имя контакта
     * @return {@code true}, если существует, иначе {@code false}
     */
    boolean existsContact(User user, String contactName);

    /**
     * Добавляет контакт в список контактов пользователя
     *
     * @param user пользователь
     * @param contactName имя контакта
     */
    void addContact(User user, String contactName);

    /**
     * Изменить состояние контакта
     * <ol>
     *     <li>Изменить имя</li>
     *     <li>Изменить телефонный номер</li>
     *     <li>Изменить возраст</li>
     *     <li>Изменить пол</li>
     *     <li>Изменить блокировку</li>
     * </ol>
     *
     * @param user пользователь, которому нужно изменить контакт
     * @param newValue новое значение
     */
    void editContact(User user, StateInfo userStateInfo, String newValue);


    /**
     * Удаляет контакт из списка контактов пользователя
     *
     * @param user пользователь
     * @param contactName имя контакта
     */
    void deleteContact(User user, String contactName);

}
