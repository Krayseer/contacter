package ru.anykeyers.services;

import ru.anykeyers.domain.User;

/**
 * Интерфейс сервиса по сортировке контактов
 */
public interface SortService {

    /**
     * Отсортировать пользователей в зависимости от состояния пользователя и введенного значения
     * @param user пользователь
     * @param value аргумент сортировки
     * @return результат сортировки контактов
     */
    String sortByUserStateAndKind(User user, String value);

}
