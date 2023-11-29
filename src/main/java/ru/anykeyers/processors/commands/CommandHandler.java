package ru.anykeyers.processors.commands;

import ru.anykeyers.domain.User;

/**
 * Интерфейс для обработчика команд
 */
public interface CommandHandler {

    /**
     * Обработать команду
     */
    String handleCommand(User user);

}
