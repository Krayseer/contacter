package ru.anykeyers.exception.group;

import ru.anykeyers.common.Messages;

/**
 * Исключение, выбрасываемое при попытке добавления контакта в группу, который там уже присутствует<br/>
 * Содержит информацию об имени контакта, который вызвал конфликт
 */
public class ContactAlreadyExistsInGroupException extends RuntimeException {

    private final Messages messages = Messages.getInstance();

    private final String name;

    public ContactAlreadyExistsInGroupException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return messages.getMessageByKey("group.exception.contact.already-exists", name);
    }

}
