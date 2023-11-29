package ru.anykeyers.repositories.file;

import org.apache.commons.io.FileUtils;
import ru.anykeyers.repositories.file.formatters.ObjectFormatter;
import ru.anykeyers.repositories.file.data.ObjectData;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Базовый класс для файловых репозиториев
 * @param <T> класс сущности
 */
public abstract class BaseFileRepository<T> {

    protected ObjectFormatter<T> formatter;

    private final File dbFile;

    public BaseFileRepository(String dbFilePath) {
        this.dbFile = new File(dbFilePath);
    }

    /**
     * Получить карту вида [имя пользователя -> данные по конкретному типу]
     */
    protected Map<String, ObjectData<T>> getInfosByUsername() {
        return formatter.initFromFile(dbFile);
    }

    /**
     * Обновить или сохранить файловую БД
     * @param data данные для сохранения
     */
    protected void saveOrUpdateFile(List<String> data) {
        try {
            FileUtils.writeLines(dbFile, "UTF-8", data, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
