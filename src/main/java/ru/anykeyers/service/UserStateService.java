package ru.anykeyers.service;

import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.domain.entity.User;

/**
 * Сервис по хранению состояния пользователей
 */
public interface UserStateService {

    /**
     * Получить состояние пользователя
     *
     * @param user пользователь
     */
    StateInfo getUserState(User user);

}
