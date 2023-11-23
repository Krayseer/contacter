package ru.anykeyers.commands;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Перечисление команд, возможных в приложении
 */
public enum Command {

    LOG_IN("/login", new Parameters("<имя пользователя>"), "Авторизоваться", false),
    LOG_OUT("/logout", null, "Выйти из аккаунта", true),

    ADD_CONTACT("/add-contact", new Parameters("<имя контакта>"), "Добавить контакт", true),
    EDIT_CONTACT_NAME("/edit-contact-name", new Parameters("<старое имя контакта>", "<новое имя контакта>"), "Изменить имя контакта", true),
    EDIT_CONTACT_PHONE("/edit-contact-phone", new Parameters("<имя контакта>", "<новый номер телефона>"), "Изменить номер контакта", true),
    EDIT_CONTACT_GENDER("/edit-contact-gender", new Parameters("<имя контакта>", "<новый пол>"), "Изменить пол контакта", true),
    EDIT_CONTACT_AGE("/edit-contact-age", new Parameters("<имя контакта>", "<новый возраст>"), "Изменить возраст контакта", true),
    BLOCK_CONTACT("/block-contact", new Parameters("<имя контакта>"), "Заблокировать контакт", true),
    UNBLOCK_CONTACT("/unblock-contact", new Parameters("<имя контакта>"), "Разблокировать контакт", true),
    DELETE_CONTACT_BY_NAME("/delete-contact-by-name", new Parameters("<имя контакта>"), "Удалить контакт по имени", true),
    DELETE_CONTACT_BY_PHONE("/delete-contact-by-phone", new Parameters("<номер телефона>"), "Удалить контакт по номеру", true),
    SEARCH_CONTACT_BY_NAME("/search-contact-by-name", new Parameters("<имя контакта>"), "Найти контакт по имени", true),
    SEARCH_CONTACT_BY_PHONE("/search-contact-by-phone", new Parameters("<номер телефона>"),"Найти контакт по номеру телефона", true),
    FILTER_CONTACT_BY_GENDER("/filter-gender", new Parameters("<пол>"), "Отфильтровать по полу", true),
    FILTER_CONTACT_BY_BLOCK("/filter-block", null, "Отфильтровать заблокированных пользователей", true),
    FILTER_CONTACT_BY_UNBLOCK("/filter-non-block", null, "Отфильтровать незаблокированных пользователей", true),
    FILTER_CONTACT_BY_AGE("/filter-age", new Parameters("<выражение>", "<возраст>"), "Отфильтровать заблокированных пользователей", true),
    SORT_NAME("/sort-name", new Parameters("<порядок сортировки>"), "Сортировка контактов по имени", true),
    SORT_AGE("/sort-age", new Parameters("<порядок сортировки>"), "Сортировка контактов по номеру телефона", true),

    ADD_GROUP("/add-group", new Parameters("<название группы>"), "Добавить группу", true),
    EDIT_GROUP_NAME("/edit-group-name", new Parameters("<старое название группы>", "<новое название группы>"), "Изменить название группы", true),
    GROUP_ADD_CONTACT("/group-add-contact", new Parameters("<название группы>", "<имя контакта>"), "Добавить пользователя в группу", true),
    GROUP_DELETE_CONTACT("/group-delete-contact", new Parameters("<название группы>", "<имя контакта>"), "Удалить пользователя из группы", true),
    DELETE_GROUP("/delete-group", new Parameters("<название группы>"), "Удалить группу", true),

    HELP("/help", null, "Показать возможные комманды", false),
    EXIT_APP("/exit", null, "Выйти из приложения", false);

    /**
     * Команда для обработки
     */
    private final String commandValue;

    /**
     * Принимаемые командой параметры
     */
    private final Parameters parameters;

    /**
     * Описание команды
     */
    private final String description;

    /**
     * Нужна ли для обработки команды аутентификация
     */
    private final boolean isNeedAuthenticate;

    Command(String commandValue, Parameters parameters, String description, boolean isNeedAuthenticate) {
        this.commandValue = commandValue;
        this.parameters = parameters;
        this.description = description;
        this.isNeedAuthenticate = isNeedAuthenticate;
    }

    public String getCommandValue() {
        return commandValue;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public int getParametersLength() {
        return parameters.getParametersLength();
    }

    public String getDescription() {
        return description;
    }

    public boolean isNeedParameters() {
        return parameters != null;
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

    public static String getAllCommands() {
        return Arrays.stream(values())
                .map(command -> String.format("%s %s : %s",
                        command.getCommandValue(),
                        command.getParameters() != null ? command.getParameters() : "",
                        command.getDescription()))
                .collect(Collectors.joining("\n"));
    }

    private static class Parameters {

        private final String[] parameters;

        public Parameters(String... parameters) {
            this.parameters = parameters;
        }

        public int getParametersLength() {
            return parameters.length;
        }

        @Override
        public String toString() {
            return Arrays.toString(parameters);
        }
    }

}
