package ru.anykeyers.repositories.file.mapper;

/**
 * Интерфейс парсера
 */
public interface Mapper<T> {

    /**
     * Форматировать объект для сохранения в файловую БД
     * @param object объект для записи в БД
     * @return строка соответствующего вида для сохранения в файловую БД
     */
    String format(T object);

    /**
     * Сформировать объект из строки
     * @param line строка
     * @return T объект, сформированный из строки
     */
    T parse(String line);

}
