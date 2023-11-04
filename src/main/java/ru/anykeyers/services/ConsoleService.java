package ru.anykeyers.services;

import ru.anykeyers.commands.Command;
import ru.anykeyers.domain.User;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Класс предоставляет методы для взаимодействия с консолью
 */
public class ConsoleService {

    /**
     * Считывает данные пользователя из консоли, включая имя пользователя и пароль.
     * @return Объект `User` с введенными данными.
     */
    public User readUserFromConsole() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите имя пользователя: ");
        String username = scanner.nextLine();
        while(username.length() == 0) {
            System.out.println("Имя пользователя должно быть введено");
            System.out.print("Введите имя пользователя: ");
            username = scanner.nextLine();
        }

        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();
        while(password.length() == 0) {
            System.out.println("Пароль должен быть введён");
            System.out.print("Введите пароль: ");
            password = scanner.nextLine();
        }
        return new User(username, password);
    }

    /**
     * Считывает команду, введенную пользователем из консоли.
     * @return Строка, представляющая введенную команду.
     */
    public String readCommand() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    /**
     * Написать все зарегистрированные команды в приложении
     */
    public void writeCommands() {
        Arrays.stream(Command.values()).forEach(command ->  {
            String commandInfo = String.format("%s : %s", command.getCommandValue(), command.getDescription());
            System.out.println(commandInfo);
        });
    }

}
