package ru.anykeyers.repository.file.service;

import java.io.File;
import java.util.Collection;
import java.util.Map;

/**
 * Интерфейс сервиса для работы с файловой БД
 * @param <T> тип объекта
 */
public interface FileService<T> {

    /**
     * Инициализация коллекции данных из файла
     *
     * @param dbFile путь до файловой БД
     */
    Collection<T> initDataFromFile(File dbFile);

    /**
     * Сохранить или обновить файловую БД новыми данными
     *
     * @param dbFile путь до файловой БД
     * @param data новые данные
     */
    void saveOrUpdateFile(File dbFile, Map<String, ? extends Collection<T>> data);

}
