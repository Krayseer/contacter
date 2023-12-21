package ru.anykeyers.common;

import ru.anykeyers.domain.FileFormat;
import ru.anykeyers.exception.BadArgumentException;
import ru.anykeyers.exception.InvalidNumberFormat;

/**
 * Утилитарный класс
 */
public final class Utils {

    private final Messages messages = Messages.getInstance();

    /**
     * Получить конкретный аргумент перечисления по строковому представлению числа
     *
     * @param enumerate список enum's
     * @param field     строковое представление числа
     * @return конкретный аргумент перечисления
     */
    public Enum<?> getEnumKindByField(Enum<?>[] enumerate, String field) {
        int num;
        try {
            num = Integer.parseInt(field);
            return enumerate[num - 1];
        } catch (NumberFormatException numberFormatException) {
            throw new InvalidNumberFormat();
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            throw new BadArgumentException();
        }
    }

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
