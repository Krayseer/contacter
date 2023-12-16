package ru.anykeyers.repository;

import ru.anykeyers.bot.BotType;
import ru.anykeyers.domain.entity.User;

/**
 * Репозиторий для работы с таблицей пользователей
 */
public interface UserRepository {

    /**
     * Проверить существование пользователя по имени пользователя и типу бота
     *
     * @param username имя пользователя
     * @param botType тип бота
     * @return {@code true}, если пользователь существует, иначе {@code false}
     */
    boolean existsByUsernameAndBotType(String username, BotType botType);

    /**
     * Получить экземпляр пользователя по имени пользователя и типу бота
     *
     * @param username имя пользователя
     * @param botType тип бота
     */
    User getUserByUsernameAndBotType(String username, BotType botType);

    /**
     * Сохранить или обновить пользователя в БД
     *
     * @param user пользователь
     */
    void saveOrUpdate(User user);

}
