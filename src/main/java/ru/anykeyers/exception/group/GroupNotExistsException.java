package ru.anykeyers.exception.group;

import ru.anykeyers.common.Messages;

/**
 * Исключение, выбрасываемое при попытке получения несуществующей группы<br/>
 * Содержит информацию о названии группы, которое вызвало конфликт
 */
public class GroupNotExistsException extends RuntimeException {

    private final Messages messages = Messages.getInstance();

    private final String name;

    public GroupNotExistsException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return messages.getMessageByKey("group.exception.name.not-exists", name);
    }

}
