package ru.anykeyers.processors.commands;

/**
 * Перечисление команд, возможных в приложении
 */
public enum Command {

    ADD_CONTACT_COMMAND("/add_contact", "Добавить контакт"),
    EDIT_CONTACT_COMMAND("/edit_contact", "Изменить контакт"),
    DELETE_CONTACT_COMMAND("/delete_contact", "Удалить контакт"),

    ADD_GROUP_COMMAND("/add_group", "Добавить группу"),
    EDIT_GROUP_COMMAND("/edit_group", "Изменить состояние группы"),
    DELETE_GROUP_COMMAND("/delete_group", "Удалить группу"),

    SEARCH_COMMAND("/search", "Поиск по контактам и группам"),
    FILTER_COMMAND("/filter", "Фильтрация контактов"),
    SORT_COMMAND("/sort", "Сортировка контактов"),

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

}
