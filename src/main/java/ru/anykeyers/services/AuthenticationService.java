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
        boolean authenticated = authenticationManager.login(user);
        if (authenticated) {
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
     * Существует ли авторизованный пользователь
     */
    public boolean isAuthenticated() {
        return authenticationManager.getCurrentUser() != null;
    }

    /**
     * Выйти из аккаунта
     */
    public void logoutUser() {
        authenticationManager.logout();
        System.out.println("Вы успешно вышли из аккаунта");
    }

}
