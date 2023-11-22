package ru.anykeyers.repositories;

import ru.anykeyers.domain.Contact;

import java.util.Set;

/**
 * Репозиторий для работы с таблицей контактов
 */
public interface ContactRepository {

    boolean existsByUsernameAndName(String username, String name);

    /**
     * Получить все контакты пользователя
     * @param username имя пользователя
     */
    Set<Contact> findByUsername(String username);

    /**
     * Получить контакт пользователя по имени
     * @param username имя пользователя
     * @param name имя контакта
     * @return контакт пользователя
     */
    Contact findByUsernameAndName(String username, String name);

    /**
     * Получить контакт пользователя по номеру телефона
     * @param username имя пользователя
     * @param phoneNumber номер телефона контакта
     * @return контакт пользователя
     */
    Contact findByUsernameAndPhoneNumber(String username, String phoneNumber);

    /**
     * Сохраняет или обновляет контакт
     * @param contact контакт для сохранения или обновления
     */
    void saveOrUpdate(Contact contact);

    /**
     * Удалить контакт
     * @param contact удаляемый контакт
     */
    void delete(Contact contact);

}
