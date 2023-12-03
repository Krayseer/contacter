package ru.anykeyers.services;

import ru.anykeyers.domain.User;

/**
 * Интерфейс сервиса для работы с контактами
 */
public interface ContactService {

    /**
     * Существует ли контакт у пользователя
     * @param user пользователь
     * @param contactName имя контакта
     * @return {@code true}, если существует, иначе {@code false}
     */
    boolean existsContact(User user, String contactName);

    /**
     * Добавляет контакт в список контактов пользователя
     * @param user пользователь
     * @param contactName имя контакта
     * @return результат добавления контакта
     */
    String addContact(User user, String contactName);

    /**
     * Изменить состояние контакта
     * <ol>
     *     <li>Изменить имя</li>
     *     <li>Изменить телефонный номер</li>
     * </ol>
     * @param user пользователь, которому нужно изменить контакт
     * @param newValue новое значение
     * @return результат изменения состояния контакта
     */
    String editContact(User user, String newValue);


    /**
     * Удаляет контакт из списка контактов пользователя
     * @param user пользователь
     * @param contactName имя контакта
     * @return результат удаления контакта
     */
    String deleteContact(User user, String contactName);

}
