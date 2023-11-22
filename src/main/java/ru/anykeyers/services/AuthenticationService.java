package ru.anykeyers.services;

import ru.anykeyers.contexts.Messages;
import ru.anykeyers.repositories.UserRepository;
import ru.anykeyers.domain.User;

/**
 * Класс представляет из себя сервис для аутентификации пользователей
 */
public class AuthenticationService {

    private final Messages messages;

    private final UserRepository userRepository;

    /**
     * Текущий авторизованный пользователь.
     */
    private User currentUser;

    public AuthenticationService(UserRepository userRepository) {
        messages = new Messages();
        this.userRepository = userRepository;
    }

    /**
     * Авторизация пользователя с консоли
     */
    public String authenticate(String username) {
        User user = new User(username);
        if (!userRepository.exists(user)) {
            userRepository.save(user);
        }
        currentUser = user;
        return messages.getMessageByKey("auth.successful");
    }

    /**
     * Получает текущего авторизованного пользователя.
     * @return Объект `User`, представляющий текущего пользователя, или null, если ни один пользователь не авторизован.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Выход из аккаунта авторизованного пользователя
     */
    public String logoutUser() {
        currentUser = null;
        return messages.getMessageByKey("auth.successful-logout");
    }

    /**
     * Существует ли авторизованный пользователь
     */
    public boolean isAuthenticated() {
        return currentUser != null;
    }

}
