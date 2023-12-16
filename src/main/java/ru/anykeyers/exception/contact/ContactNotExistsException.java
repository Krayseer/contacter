package ru.anykeyers.exception.contact;

import ru.anykeyers.common.Messages;

/**
 * Исключение, выбрасываемое при попытке получения контакта с несуществующим именем<br/>
 * Содержит информацию об имени контакта, который вызвал конфликт
 */
public class ContactNotExistsException extends RuntimeException {

    private final Messages messages = Messages.getInstance();

    private final String name;

    public ContactNotExistsException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return messages.getMessageByKey("contact.exception.name.not-exists", name);
    }

}
