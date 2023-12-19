package ru.anykeyers.service;

import ru.anykeyers.domain.entity.User;

import java.io.File;

/**
 * Сервис по импорту/экспорту данных пользователя
 */
public interface ImportExportService {

    /**
     * Импортировать данные
     *
     * @param user пользователь
     * @param importFile файл с данными для импорта
     */
    void importData(User user, File importFile);

    /**
     * Экспортировать данные
     *
     * @param user пользователь
     * @param exportFile файл экспорта
     */
    void exportData(User user, File exportFile);

}
