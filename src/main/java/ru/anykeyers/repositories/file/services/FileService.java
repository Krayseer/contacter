package ru.anykeyers.repositories.file.services;

import java.io.File;
import java.util.Collection;
import java.util.Map;

/**
 * Интерфейс сервиса для работы с файловой БД
 * @param <T> тип объекта
 */
public interface FileService<T> {

    /**
     * Инициализация колелкции данных из файла
     * @param dbFile файловая БД
     * @return коллекция данных
     */
    Collection<T> initDataFromFile(File dbFile);

    /**
     * Сохранить или обновить файловую БД новыми данными
     * @param dbFile файловая БД
     * @param data новые данные
     */
    void saveOrUpdateFile(File dbFile, Map<String, ? extends Collection<T>> data);

}
