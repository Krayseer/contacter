package ru.anykeyers.services;

import ru.anykeyers.security.User;

import java.util.Scanner;

/**
 * Класс предоставляет методы для взаимодействия с консолью
 */
public class ConsoleService {

    private final Scanner scanner;

    public ConsoleService() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Считывает данные пользователя из консоли, включая имя пользователя и пароль.
     * @return Объект `User` с введенными данными.
     */
    public User readUserFromConsole() {
        System.out.print("Введите username: ");
        String username = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();
        return new User(username, password);
    }

    /**
     * Считывает команду, введенную пользователем из консоли.
     * @return Строка, представляющая введенную команду.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

}
