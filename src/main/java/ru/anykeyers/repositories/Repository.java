package ru.anykeyers.repositories;

/**
 * Интерфейс для репозиториев работы с файловой базой данных
 */
public interface Repository {

    /**
     * @return относительный путь до файловой базы данных
     */
    String getDbFilePath();

}
