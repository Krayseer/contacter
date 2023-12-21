package ru.anykeyers.utils;

/**
 * Утилитарный класс для работы со строками
 */
public final class StringUtils {

    /**
     * Проверить, что строка не является числом
     *
     * @param str строка, которую нужно проверить
     * @return {@code true}, если не является числом, иначе {@code false}
     */
    public boolean isNotNumber(String str) {
        return !str.matches("\\d+");
    }

}
