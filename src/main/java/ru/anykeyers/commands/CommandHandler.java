package ru.anykeyers.commands;

/**
 * Интерфейс для обработчика команд
 */
public interface CommandHandler {

    /**
     * Обработать команду
     */
    String handleCommand(String args);

}
