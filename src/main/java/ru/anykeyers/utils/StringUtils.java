package ru.anykeyers.utils;

import ru.anykeyers.common.Messages;
import ru.anykeyers.domain.FileFormat;

/**
 * Утилитарный класс для работы со строками
 */
public final class StringUtils {

    private final Messages messages = Messages.getInstance();

    /**
     * Проверить, что строка не является числом
     *
     * @param str строка, которую нужно проверить
     * @return {@code true}, если не является числом, иначе {@code false}
     */
    public boolean isNotNumber(String str) {
        return !str.matches("\\d+");
    }

    /**
     * Получить формат файла
     *
     * @param filePath путь до файла
     */
    public FileFormat getFileFormat(String filePath) {
        FileFormat fileFormat;
        try {
            fileFormat = FileFormat.valueOf(filePath.split("\\.")[1].toUpperCase());
        } catch (RuntimeException ex) {
            String errorMessage = messages.getMessageByKey("exception.file.invalid-format");
            throw new RuntimeException(errorMessage);
        }
        return fileFormat;
    }

}
