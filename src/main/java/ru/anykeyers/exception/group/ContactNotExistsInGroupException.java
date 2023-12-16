package ru.anykeyers.exception.group;

import ru.anykeyers.common.Messages;

/**
 * Исключение, выбрасываемое при попытке получения несуществующего контакта в группе<br/>
 * Содержит информацию об имени контакта, который вызвал конфликт
 */
public class ContactNotExistsInGroupException extends RuntimeException {

    private final Messages messages = Messages.getInstance();

    private final String name;

    public ContactNotExistsInGroupException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return messages.getMessageByKey("group.exception.contact.not-exists", name);
    }

}
