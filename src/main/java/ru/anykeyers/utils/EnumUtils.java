package ru.anykeyers.utils;

import ru.anykeyers.exception.BadArgumentException;
import ru.anykeyers.exception.InvalidNumberFormat;

/**
 * Утилитарный класс для перечислений
 */
public final class EnumUtils {

    /**
     * Получить конкретный аргумент перечисления по строковому представлению числа
     *
     * @param enumerate список enum's
     * @param field     строковое представление числа
     * @return конкретный аргумент перечисления
     */
    public <T extends Enum<T>> Enum<T> getEnumKindByField(Enum<T>[] enumerate, String field) {
        int num;
        try {
            num = Integer.parseInt(field);
            return enumerate[num - 1];
        } catch (RuntimeException exception) {
            throw new BadArgumentException();
        }
    }

}
