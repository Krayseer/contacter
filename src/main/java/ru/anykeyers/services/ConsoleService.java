package ru.anykeyers.services;

import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.Group;
import ru.anykeyers.commands.Command;
import ru.anykeyers.domain.User;

import java.util.Arrays;
import java.util.ArrayList;
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
     * Запрашивает у пользователя при создании контакта имя, фамилию и номер телефона нового контакта
     * @return новый контакт
     */
    public Contact readContactFromConsole(Scanner scanner) {
        System.out.print("Введите имя пользователя: ");
        String name = scanner.nextLine();
        System.out.print("Введите фамилию пользователя: ");
        String lastname = scanner.nextLine();
        System.out.print("Введите номер телефона пользователя: ");
        String phoneNumber = scanner.nextLine();
        return new Contact(name, lastname, phoneNumber);
    }

    /**
     * Считывает имя группы из консоли
     * @return новую группу с новым именем
     */
    public Group readGroupFromConsole(Scanner scanner) {
        System.out.print("Введите название группы: ");
        String name = scanner.nextLine();
        return new Group(name, new ArrayList<>());
    }

    /**
     * Считывает имя и фамилию или номер телефона контакта для удаления
     * @return введеную строку
     */
    public String deleteContactFromConsole(Scanner scanner) {
        System.out.println("Введите имя и фамилию или номер телефона контакта для удаления: ");
        return scanner.nextLine();
    }

    /**
     * Считываем имя группы для удаления
     * @return введеную строку
     */
    public String deleteGroupFromConsole(Scanner scanner) {
        System.out.println("Введите название группы, которую вы хотите удалить: ");
        return scanner.nextLine();
    }

    /**
     * Считывает имя и фамилию или номер телефона контакта для изменения
     * @return введеную строку
     */
    public String editContactFromConsole(Scanner scanner) {
        System.out.println("Введите имя и фамилию или номер телефона контакта для изменения: ");
        return scanner.nextLine();
    }

    /**
     * Считывает имя группы для изменения
     * @return введеную строку
     */
    public String editGroupFromConsole(Scanner scanner) {
        System.out.println("Введите имя группы для изменения: ");
        return scanner.nextLine();
    }

    /**
     * Считывает действие пользователя, что он хочет сделать с контактом
     * @return номер действия
     */
    public int editInfo(Scanner scanner) {
        System.out.println("Что вы хотите изменить: 1. Имя и фамилия; 2. Номер телефона");
        String command = scanner.nextLine();
        if (command.matches("[-+]?\\d+")) {
            return Integer.parseInt(command);
        } else {
            return 3;
        }
    }

    /**
     * Считывает действие пользователя, что он хочет сделать с группой
     * @return номер действия
     */
    public int editGroupInfo(Scanner scanner) {
        System.out.println("Что вы хотите изменить: 1. Изменить название; 2. Добавить пользователя; 3. Удалить пользователя");
        String command = scanner.nextLine();
        if (command.matches("[-+]?\\d+")) {
            return Integer.parseInt(command);
        } else {
            return 4;
        }
    }

    /**
     * Считывает новое имя контакта
     * @return введеную строку
     */
    public String editName(Scanner scanner) {
        System.out.print("Введите новые имя и фамилию: ");
        return scanner.nextLine();
    }

    /**
     * Считывает новый телефон контакта
     * @return введеную строку
     */
    public String editPhoneNumber(Scanner scanner) {
        System.out.println("Введите новый номер телефона: ");
        return scanner.nextLine();
    }

    /**
     * Считывает новое имя группы
     * @return введеную строку
     */
    public String editGroupName(Scanner scanner) {
        System.out.print("Введите новое имя группы: ");
        return scanner.nextLine();
    }

    /**
     * Считывает имя контакта для добавления в группу
     * @return введеную строку
     */
    public String addContactInGroup(Scanner scanner) {
        System.out.print("Введите имя и фамилию пользователя, которого вы хотите добавить: ");
        return scanner.nextLine();
    }

    /**
     * Считывает имя контакта для удаления из группы
     * @return введеную строку
     */
    public String deleteContactFromGroup(Scanner scanner) {
        System.out.print("Введите имя и фамилию пользователя, которого вы хотите удалить: ");
        return scanner.nextLine();
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
