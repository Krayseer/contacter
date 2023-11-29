package ru.anykeyers.processors.commands;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Перечисление команд, возможных в приложении
 */
public enum Command {

    GET_CONTACTS_COMMAND("/get_contacts", "Получить все контакты"),
    ADD_CONTACT_COMMAND("/add_contact", "Добавить контакт"),
    EDIT_CONTACT_COMMAND("/edit_contact", "Изменить контакт"),
    DELETE_CONTACT_COMMAND("/delete_contact", "Удалить контакт"),

    GET_GROUPS_COMMAND("/get_groups", "Получить все группы"),
    GET_GROUP_CONTACTS_COMMAND("/get_group_contacts", "Получить контакты из группы"),
    ADD_GROUP_COMMAND("/add_group", "Добавить группу"),
    EDIT_GROUP_COMMAND("/edit_group", "Изменить состояние группы"),
    DELETE_GROUP_COMMAND("/delete_group", "Удалить группу"),

    HELP_COMMAND("/help", "Показать возможные комманды");

    /**
     * Команда для обработки
     */
    private final String commandValue;

    /**
     * Описание команды
     */
    private final String description;

    Command(String commandValue, String description) {
        this.commandValue = commandValue;
        this.description = description;
    }

    public String getCommandValue() {
        return commandValue;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Получить команду по текстовому представлению команды
     */
    public static Command getCommandByValue(String value) {
        for (Command command : Command.values()) {
            if (command.getCommandValue().equals(value)) {
                return command;
            }
        }
        return null;
    }

    /**
     * Получить все команды
     */
    public static String getAllCommands() {
        return Arrays.stream(values())
                .map(command -> String.format("%s : %s",
                        command.getCommandValue(),
                        command.getDescription()))
                .collect(Collectors.joining("\n"));
    }

}
