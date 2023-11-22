package ru.anykeyers.repositories;

import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.Group;

import java.util.Set;

/**
 * Репозиторий для работы с таблицей групп
 */
public interface GroupRepository {

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
     * @return группа или null, если группа отсутствует
     */
    Group findByUsernameAndName(String username, String nameGroupToFind);

    /**
     * Получить контакт по имени в группе
     * @param group группа для поиска
     * @param contactName имя контакта для поиска
     * @return контакт или null, если контакт не был найден
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
