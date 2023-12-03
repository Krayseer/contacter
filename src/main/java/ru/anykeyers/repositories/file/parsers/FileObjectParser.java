package ru.anykeyers.repositories.file.parsers;

/**
 * Интерфейс парсера
 */
public interface FileObjectParser<T> {

    /**
     * Форматировать объект для сохранения в файловую БД
     * @param object объект для записи в БД
     * @return строка соответствующего вида для сохранения в файловую БД
     */
    String parseTo(T object);

    /**
     * Сформировать объект из строки
     * @param line строка
     * @return T объект, сформированный из строки
     */
    T parseFrom(String line);

}
