package ru.anykeyers.services;

import ru.anykeyers.repositories.UserRepository;
import ru.anykeyers.domain.User;

/**
 * Сервис для аутентификации пользователей
 */
public class AuthenticationService {

    private final UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Получить экземпляр пользователя по имени пользователя
     * @param username имя пользоватея
     * @return экземпляр
     */
    public User getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }

    /**
     * Сохранить или обновить пользователя в БД
     * @param user пользователь
     */
    public void saveOrUpdateUser(User user) {
        userRepository.saveOrUpdate(user);
    }

    /**
     * Пользователь существует
     * @param username имя пользователя
     * @return {@code true}, если пользователь существует, иначе {@code false}
     */
    public boolean existsUser(String username) {
        return userRepository.existsByUsername(username);
    }

}
