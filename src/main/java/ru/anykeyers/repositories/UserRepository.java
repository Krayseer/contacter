package ru.anykeyers.repositories;

import ru.anykeyers.domain.User;

/**
 * Репозиторий для работы с таблицей БД, связанной с пользователями
 */
public interface UserRepository {

    /**
     * Существует ли пользователь
     * @return true, если существует, иначе false
     */
    boolean existsByUsername(String username);

    /**
     * Сохранить пользователя
     */
    void saveOrUpdate(User user);

    /**
     * Получить экземпляр пользователя
     * @param username имя пользователя
     */
    User getUserByUsername(String username);

}
