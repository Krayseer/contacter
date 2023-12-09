package ru.anykeyers.contexts;

import java.util.ResourceBundle;

/**
 * Сообщения приложения
 */
public class Messages {

    private final ResourceBundle messages;

    public Messages() {
        messages = ResourceBundle.getBundle("messages");
    }

    /**
     * Получить сообщение из файла по ключу
     * @param key ключ сообщения
     * @return сообщение
     */
    public String getMessageByKey(String key) {
        return messages.getString(key);
    }

    /**
     * Получить сообщение из файла по ключу с аргументами
     * @param key ключ сообщения
     * @param args аргументы, которые нужно применить к сообщению
     * @return сообщение
     */
    public String getMessageByKey(String key, Object... args) {
        return String.format(getMessageByKey(key), args);
    }

}
