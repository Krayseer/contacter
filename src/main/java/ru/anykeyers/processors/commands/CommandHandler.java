package ru.anykeyers.processors.commands;

import ru.anykeyers.domain.User;

/**
 * Интерфейс для обработчика команд
 */
public interface CommandHandler {

    /**
     * Обработать команду
     * @param user пользователь, обрабатывающий команды
     * @return результат обработки команды
     */
    String handleCommand(User user);

}
