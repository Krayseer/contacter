package ru.anykeyers.processor.state;

import ru.anykeyers.domain.entity.User;

/**
 * Обработчик состояний
 */
public interface StateProcessor {

    /**
     * Обработка состояния пользователя с принимаемым сообщением
     *
     * @param user пользователь
     * @param message сообщение для обработки
     * @return результат обработки сообщения в зависимости от состояния
     */
    String processState(User user, String message);

}
