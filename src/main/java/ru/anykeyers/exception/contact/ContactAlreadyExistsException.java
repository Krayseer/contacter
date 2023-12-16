package ru.anykeyers.exception.contact;

import ru.anykeyers.common.Messages;

/**
 * Исключение, выбрасываемое при попытке добавления контакта, который уже существует<br/>
 * Содержит информацию об имени контакта, который вызвал конфликт
 */
public class ContactAlreadyExistsException extends RuntimeException {

    private final Messages messages = Messages.getInstance();

    private final String name;

    public ContactAlreadyExistsException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return messages.getMessageByKey("contact.exception.already-exists", name);
    }

}
