package ru.anykeyers.services;

import ru.anykeyers.repositories.UserRepository;
import ru.anykeyers.domain.User;

/**
 * Класс представляет из себя сервис для аутентификации пользователей
 */
public class AuthenticationService {

    private final ConsoleService consoleService;

    private final UserRepository userRepository;

    /**
     * Текущий авторизованный пользователь.
     */
    private User currentUser;

    public AuthenticationService(UserRepository userRepository) {
        this.consoleService = new ConsoleService();
        this.userRepository = userRepository;
    }

    /**
     * Авторизация пользователя с консоли
     */
    public void authenticate() {
        User user = consoleService.readUserFromConsole();
        if(!userRepository.existsByUsername(user.getUsername())) {
            userRepository.save(user);
        }
        String userPassword = userRepository.findPasswordByUsername(user.getUsername());
        if (userPassword != null && userPassword.equals(user.getPassword())) {
            currentUser = user;
            System.out.println("Вы успешно авторизовались");
        } else {
            System.out.println("Пароль был введён неверно");
        }
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
    public void logoutUser() {
        currentUser = null;
        System.out.println("Вы успешно вышли из аккаунта");
    }

    /**
     * Существует ли авторизованный пользователь
     */
    public boolean isAuthenticated() {
        return currentUser != null;
    }

}
