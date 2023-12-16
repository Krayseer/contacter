package ru.anykeyers.exception;

import ru.anykeyers.common.Messages;

/**
 * Исключение, выбрасываемое при ошибке чтения файла<br/>
 * Содержит информацию о пути файла
 */
public class FileReadException extends RuntimeException {

    private final Messages messages = Messages.getInstance();

    private final String filePath;

    public FileReadException(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String getMessage() {
        return messages.getMessageByKey("exception.file.read-error", filePath);
    }

}
