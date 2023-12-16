package ru.anykeyers.processor.state;

import ru.anykeyers.domain.entity.User;

/**
 * Обработчик состояния
 */
public interface StateHandler {

    /**
     * Обработать состояние
     *
     * @param user пользователь
     * @param message сообщение для обработки
     * @return результат обработки сообщения
     */
    String handleState(User user, String message);

}
