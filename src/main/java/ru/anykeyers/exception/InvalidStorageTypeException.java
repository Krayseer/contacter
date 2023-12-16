package ru.anykeyers.exception;

import ru.anykeyers.common.Messages;

/**
 * Исключение, выбрасываемое при некорректном типе хранилища
 */
public class InvalidStorageTypeException extends RuntimeException {

    private final Messages messages = Messages.getInstance();

    private final String storageType;

    public InvalidStorageTypeException(String storageType) {
        this.storageType = storageType;
    }

    @Override
    public String getMessage() {
        return messages.getMessageByKey("exception.storage.invalid-type", storageType);
    }

}
