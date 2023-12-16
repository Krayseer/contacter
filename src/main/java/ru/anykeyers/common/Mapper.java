package ru.anykeyers.common;

/**
 * Преобразователь объекта в строку и наоборот
 */
public interface Mapper<T> {

    /**
     * Форматировать объект в строку
     *
     * @param object объект, который нужно форматировать
     * @return строка, сформированная из объекта
     */
    String format(T object);

    /**
     * Сформировать объект из строки
     *
     * @param str строка
     * @return объект, сформированный из строки
     */
    T parse(String str);

}
