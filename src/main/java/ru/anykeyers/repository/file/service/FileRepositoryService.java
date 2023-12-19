package ru.anykeyers.repository.file.service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Сервис для файлового репозитория
 *
 * @param <T> объект Entity
 */
public interface FileRepositoryService<T> {

    /**
     * Получить коллекцию данных из карты
     *
     * @param dataByUsername карта вида [имя пользователя -> данные]
     */
    Collection<T> getCollectionFromMap(Map<String, ? extends Collection<T>> dataByUsername);

    /**
     * Получить карту из коллекции данных вида [имя пользователя -> данные]
     *
     * @param data коллекция всех данных
     * @param generateStringFunction функция, по которой нужно получать ключ карты (username)
     */
    Map<String, Set<T>> getMapFromCollection(Collection<T> data,
                                             Function<T, String> generateStringFunction);

}
