package ru.anykeyers.repositories.file.formatters;

import ru.anykeyers.repositories.file.data.ObjectData;

import java.io.File;
import java.util.Map;

/**
 * Интерфейс форматтера, описывающий поведение для обработки файловых БД
 * @param <T>
 */
public interface ObjectFormatter<T> {

    /**
     * Форматировать объект для сохранения в файловую БД
     * @param object объект для записи в БД
     * @return строка соответствующего вида для сохранения в файловую БД
     */
    String format(T object);

    /**
     * Инициализация карты вида [имя пользователя -> данные пользователя]
     * @param file файловая БД
     */
    Map<String, ObjectData<T>> initFromFile(File file);

}
