package ru.anykeyers.repositories;

/**
 * Интерфейс, для работы репозиториев с файловой БД
 */
public interface FileDBRepository {

    /**
     * Сохранить все данные в файловую БД
     */
    void saveAll();

}
