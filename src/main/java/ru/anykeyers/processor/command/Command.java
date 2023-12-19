package ru.anykeyers.processor.command;

/**
 * Команды для ботов
 */
public enum Command {

    ADD_CONTACT_COMMAND("/add_contact", "command.contact.add"),
    EDIT_CONTACT_COMMAND("/edit_contact", "command.contact.edit"),
    DELETE_CONTACT_COMMAND("/delete_contact", "command.contact.delete"),

    ADD_GROUP_COMMAND("/add_group", "command.group.add"),
    EDIT_GROUP_COMMAND("/edit_group", "command.group.edit"),
    DELETE_GROUP_COMMAND("/delete_group", "command.group.delete"),

    GET_COMMAND("/get", "command.operation.get"),
    SEARCH_COMMAND("/search", "command.operation.search"),
    FILTER_COMMAND("/filter", "command.operation.filter"),
    SORT_COMMAND("/sort", "command.operation.sort"),

    IMPORT_COMMAND("/import", "command.import"),
    EXPORT_COMMAND("/export", "command.export"),

    HELP_COMMAND("/help", "command.help");

    /**
     * Команда для обработки
     */
    private final String commandValue;

    /**
     * Ключ текста описания команды
     */
    private final String descriptionKey;

    Command(String commandValue, String descriptionKey) {
        this.commandValue = commandValue;
        this.descriptionKey = descriptionKey;
    }

    public String getCommandValue() {
        return commandValue;
    }

    public String getDescriptionKey() {
        return descriptionKey;
    }

}
