package ru.anykeyers.services;

import ru.anykeyers.bots.BotType;
import ru.anykeyers.domain.User;

/**
 * Интерфейс для сервиса аутентификации пользователей
 */
public interface AuthenticationService {

    /**
     * Проверить существование пользователя по имени пользователя и типу бота
     * @param username имя пользователя
     * @param botType тип бота
     * @return {@code true}, если пользователь существует, иначе {@code false}
     */
    boolean existsUserByUsernameAndBotType(String username, BotType botType);

    /**
     * Получить экземпляр пользователя по имени пользователя и типу бота
     * @param username имя пользоватея
     * @param botType тип бота
     * @return экземпляр пользователя
     */
    User getUserByUsernameAndBotType(String username, BotType botType);

    /**
     * Сохранить или обновить пользователя в БД
     * @param user пользователь
     */
    void saveOrUpdateUser(User user);

}
