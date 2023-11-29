package ru.anykeyers.repositories.file.data;

import java.io.File;

/**
 * Информация об объекте<br/>
 * Используется в {@link ru.anykeyers.repositories.file.formatters.ObjectFormatter#initFromFile(File)} для указания конкретной
 * информации для пользователя (это может быть объект, коллекция объектов и тд.)
 * @param <T> класс объекта
 */
public interface ObjectData<T> {

    /**
     * Получить информацию об объекте
     */
    Object getData();

    /**
     * Добавить данные объекта
     * @param data данные для добавления
     */
    void addData(T data);

    /**
     * Удалить данные об объекте
     * @param data данные для удаления
     */
    void removeData(T data);

}
