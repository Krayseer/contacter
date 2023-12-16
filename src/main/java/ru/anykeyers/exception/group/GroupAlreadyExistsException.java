package ru.anykeyers.exception.group;

import ru.anykeyers.common.Messages;

/**
 * Исключение, выбрасываемое при попытке добавления группы с названием, которое уже существует<br/>
 * Содержит информацию о названии группы, которое вызвало конфликт
 */
public class GroupAlreadyExistsException extends RuntimeException {

    private final Messages messages = Messages.getInstance();

    private final String name;

    public GroupAlreadyExistsException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return messages.getMessageByKey("group.exception.already-exists", name);
    }

}
