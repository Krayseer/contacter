package ru.anykeyers.repository;

import ru.anykeyers.domain.entity.Contact;

import java.util.Optional;
import java.util.Set;

/**
 * Репозиторий для работы с таблицей контактов
 */
public interface ContactRepository {

    /**
     * Существует ли контакт у пользователя
     *
     * @param username имя пользователя
     * @param name имя контакта
     * @return {@code true}, если существует, иначе {@code false}
     */
    boolean existsByUsernameAndName(String username, String name);

    /**
     * Получить все контакты пользователя
     *
     * @param username имя пользователя
     * @return список контактов пользователя
     */
    Set<Contact> findByUsername(String username);

    /**
     * Получить контакт пользователя по идентификатору
     *
     * @param username имя пользователя
     * @param contactId идентификатор контакта
     */
    Optional<Contact> getByUsernameAndId(String username, String contactId);

    /**
     * Получить контакт пользователя по имени
     *
     * @param username имя пользователя
     * @param name имя контакта
     */
    Optional<Contact> getByUsernameAndName(String username, String name);

    /**
     * Сохранить или обновить контакт
     *
     * @param contact контакт для сохранения или обновления
     */
    void saveOrUpdate(Contact contact);

    /**
     * Удалить контакт
     *
     * @param contact удаляемый контакт
     */
    void delete(Contact contact);

}
