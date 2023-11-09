package ru.anykeyers.commands;

/**
 * Перечисление команд, возможных в приложении
 */
public enum Command {

    LOG_IN("/login", "Авторизоваться", false),
    LOG_OUT("/logout", "Выйти из аккаунта", true),
    ADD_CONTACT("/add", "Добавить контакт", true),
    EDIT_CONTACT("/edit", "Изменить контакт", true),
    DELETE_CONTACT("/delete", "Удалить контакт", true),
    ADD_GROUP("/add-group", "Добавить группу", true),
    EDIT_GROUP("/edit-group", "Изменить группу", true),
    DELETE_GROUP("/delete-group", "Удалить группу", true),
    HELP("/help", "Показать возможные комманды", false),
    EXIT_APP("/exit", "Выйти из приложения", false);

    /**
     * Команда для обработки
     */
    private final String commandValue;

    /**
     * Описание команды
     */
    private final String description;

    /**
     * Нужна ли для обработки команды аутентификация
     */
    private final boolean isNeedAuthenticate;

    Command(String commandValue, String description, boolean isNeedAuthenticate) {
        this.commandValue = commandValue;
        this.description = description;
        this.isNeedAuthenticate = isNeedAuthenticate;
    }

    public String getCommandValue() {
        return commandValue;
    }

    public String getDescription() {
        return description;
    }

    public boolean isNeedAuthenticate() {
        return isNeedAuthenticate;
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
