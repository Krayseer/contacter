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
    boolean exists(User user);

    /**
     * Сохранить пользователя
     */
    void save(User user);

}
