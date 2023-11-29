package ru.anykeyers.repositories;

import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.Group;

import java.util.Set;

/**
 * Репозиторий для работы с таблицей групп
 */
public interface GroupRepository {

    /**
     * Существует ли группа у пользователя
     * @param username имя пользователя
     * @param name название группы
     */
    boolean existsByUsernameAndName(String username, String name);

    /**
     * Получить группы пользователя
     * @param username имя пользователя
     */
    Set<Group> findByUsername(String username);

    /**
     * Получить группу по имени пользователя и имени группы
     * @param username имя пользователя
     * @param nameGroupToFind имя группы для поиска
     */
    Group findByUsernameAndName(String username, String nameGroupToFind);

    /**
     * Получить контакт по имени в группе
     * @param group группа для поиска
     * @param contactName имя контакта для поиска
     */
    Contact findContactInGroupByName(Group group, String contactName);

    /**
     * Получить группу по имени пользователя и имени группы
     * @param group группа для сохранения
     */
    void saveOrUpdate(Group group);

    /**
     * Удаляет группу из списка групп пользователя
     * @param groupToDelete группа для удаления
     */
    void delete(Group groupToDelete);

}
