package ru.anykeyers.commands;

public enum Command {

    LOG_IN("/login", "Авторизоваться"),
    LOG_OUT("/logout", "Выйти из аккаунта"),
    ADD_CONTACT("/add", "Добавить контакт"),
    EDIT_CONTACT("/edit", "Изменить контакт"),
    DELETE_CONTACT("/delete", "Удалить контакт"),
    ADD_GROUP("/add-group", "Добавить группу"),
    EDIT_GROUP("/edit-group", "Изменить группу"),
    DELETE_GROUP("/delete-group", "Удалить группу"),
    HELP("/help", "Показать возможные комманды"),
    EXIT_APP("/exit", "Выйти из приложения");

    private final String commandValue;

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

    public static Command getCommandByValue(String value) {
        for (Command command : Command.values()) {
            if (command.getCommandValue().equals(value)) {
                return command;
            }
        }
        return null;
    }

}
