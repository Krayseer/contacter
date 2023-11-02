package ru.anykeyers.services;

import ru.anykeyers.commands.Command;
import ru.anykeyers.domain.User;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Класс предоставляет методы для взаимодействия с консолью
 */
public class ConsoleService {

    private final Scanner scanner;

    public ConsoleService() {
        this.scanner = new Scanner(System.in);
    }

    public ConsoleService(InputStream is) {
        this.scanner = new Scanner(is);
    }

    /**
     * Считывает данные пользователя из консоли, включая имя пользователя и пароль.
     * @return Объект `User` с введенными данными.
     */
    public User readUserFromConsole() {
        System.out.println("Введите username: ");
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
        Set<String> commands = Arrays.stream(Command.values()).map(Command::getCommandValue).collect(Collectors.toSet());
        String command = scanner.nextLine();
        if (!commands.contains(command)) {
            System.out.println("Такой команды не существует, введите /help для просмтора возможных задач");
            return null;
        }
        return command;
    }

    public void writeCommands() {
        Arrays.stream(Command.values()).forEach(command -> System.out.printf("%s - %s%n", command.getCommandValue(), command.getDescription()));
    }

}
