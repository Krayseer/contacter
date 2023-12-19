package ru.anykeyers.repository.file.service;

import java.io.File;
import java.util.Collection;

/**
 * Файловый сервис
 */
public interface FileService<T> {

    /**
     * Инициализация коллекции данных из файла
     *
     * @param file файл с данными
     */
    Collection<T> initDataFromFile(File file);

    /**
     * Сохранить или обновить файл новыми данными
     *
     * @param file файл, который нужно обновить
     * @param data новые данные
     */
    void saveOrUpdateFile(File file, Collection<T> data);

}
