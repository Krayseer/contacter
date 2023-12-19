package ru.anykeyers.bot;

import java.io.File;

/**
 * Бот
 */
public interface Bot {

    /**
     * Отправить сообщение в чат
     *
     * @param chatId идентификатор чата
     * @param text сообщение
     */
    void sendText(Long chatId, String text);

    /**
     * Отправить файл в чат
     *
     * @param chatId идентификатор чата
     * @param file файл
     */
    void sendFile(Long chatId, File file);

}
