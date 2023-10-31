package ru.anykeyers.services;

import ru.anykeyers.ApplicationProperties;
import ru.anykeyers.security.AuthenticationManager;
import ru.anykeyers.security.User;

/**
 * Класс предоставляет сервис для аутентификации пользователей
 */
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final ConsoleService consoleService;

    public AuthenticationService(ConsoleService consoleService,
                                 ApplicationProperties applicationProperties) {
        this.authenticationManager = new AuthenticationManager(applicationProperties);
        this.consoleService = consoleService;
    }

    /**
     * Авторизация пользователя с консоли
     */
    public void authenticate() {
        User user = consoleService.readUserFromConsole();
        authenticationManager.login(user);
        if (isAuthenticated()) {
            System.out.println("Вы успешно авторизовались");
        } else {
            System.out.println("Пароль был введён неверно");
        }
    }

    /**
     * Сохранить добавленных пользователей в БД
     */
    public void saveUsers() {
        authenticationManager.saveUsersToFile();
    }

    /**
     * Выйти из аккаунта
     */
    public void logoutUser() {
        authenticationManager.logout();
        System.out.println("Вы успешно вышли из аккаунта");
    }

    /**
     * Существует ли авторизованный пользователь
     */
    private boolean isAuthenticated() {
        return authenticationManager.getCurrentUser() != null;
    }

}
