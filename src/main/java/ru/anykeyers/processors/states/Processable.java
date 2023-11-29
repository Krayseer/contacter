package ru.anykeyers.processors.states;

import ru.anykeyers.domain.User;

/**
 * Интерфейс обработчика
 */
public interface Processable {

    /**
     * Обработка состояния с принимаемым сообщением
     * @param user пользователь, который обрабатывает сообщение
     * @param message сообщение
     */
    String processState(User user, String message);

}
