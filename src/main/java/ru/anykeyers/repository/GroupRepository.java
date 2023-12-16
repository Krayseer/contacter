package ru.anykeyers.repository;

import ru.anykeyers.domain.entity.Group;

import java.util.Optional;
import java.util.Set;

/**
 * Репозиторий для работы с таблицей групп
 */
public interface GroupRepository {

    /**
     * Существует ли группа у пользователя
     *
     * @param username имя пользователя
     * @param name название группы
     * @return {@code true}, если существует, иначе {@code false}
     */
    boolean existsByUsernameAndName(String username, String name);

    /**
     * Получить все группы пользователя
     *
     * @param username имя пользователя
     * @return список групп пользователя
     */
    Set<Group> findByUsername(String username);

    /**
     * Получить группу пользователя по названию группы
     *
     * @param username имя пользователя
     * @param groupName название группы
     * @return экземпляр группы
     */
    Optional<Group> getByUsernameAndName(String username, String groupName);

    /**
     * Сохранить или обновить группу пользователя
     *
     * @param group группа для сохранения или обновления
     */
    void saveOrUpdate(Group group);

    /**
     * Удалить группу пользователя
     *
     * @param group группа для удаления
     */
    void delete(Group group);

}
