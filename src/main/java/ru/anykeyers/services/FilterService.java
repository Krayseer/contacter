package ru.anykeyers.services;

import ru.anykeyers.domain.User;

/**
 * Интерфейс сервиса для фильтрации контактов
 */
public interface FilterService {

    /**
     * Отфильтровать контакты в зависимости от состояния пользователя и введенного значения
     * @param user пользователь
     * @param value значение, по которому будет происходить фильтрация
     * @return результат фильтрации контактов
     */
    String filterByUserStateAndKind(User user, String value);

}
