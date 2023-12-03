package ru.anykeyers.processors.states;

import ru.anykeyers.domain.User;

/**
 * Интерфейс обработчика
 */
public interface StateProcessor {

    /**
     * Обработка состояния пользователя с принимаемым сообщением
     * @param user пользователь
     * @param message сообщение для обработки
     * @return результат обработки сообщения в зависимости от состояния
     */
    String processState(User user, String message);

}
