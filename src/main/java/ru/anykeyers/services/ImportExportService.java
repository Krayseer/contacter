package ru.anykeyers.services;

import ru.anykeyers.domain.User;

/**
 * Интерфейс сервиса по импорту/экспорту данных
 */
public interface ImportExportService {

    /**
     * Импортировать данные
     * @param user пользователь
     * @param importPath путь до файла импорта
     * @return сообщение выполнения обработки
     */
    String importData(User user, String importPath);

    /**
     * Экспортировать данные
     * @param user пользователь
     * @param exportPath путь до файла экспорта
     * @return сообщение выполнения обработки
     */
    String exportData(User user, String exportPath);

}
