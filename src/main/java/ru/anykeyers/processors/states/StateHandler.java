package ru.anykeyers.processors.states;

import ru.anykeyers.domain.User;

/**
 * Интерфейс для обработчика состояний
 */
public interface StateHandler {

    /**
     * Обработать состояние
     * @param user пользователь
     * @param message сообщение для обработки
     * @return результат обработки сообщения
     */
    String handleState(User user, String message);

}
