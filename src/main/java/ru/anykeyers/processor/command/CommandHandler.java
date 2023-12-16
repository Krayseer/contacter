package ru.anykeyers.processor.command;

import ru.anykeyers.domain.entity.User;

/**
 * Обработчик команды
 */
public interface CommandHandler {

    /**
     * Обработать команду
     *
     * @param user пользователь, обрабатывающий команду
     * @return результат обработки команды
     */
    String handleCommand(User user);

}
