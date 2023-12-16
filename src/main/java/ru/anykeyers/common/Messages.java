package ru.anykeyers.common;

import java.util.ResourceBundle;

/**
 * Сообщения приложения
 */
public class Messages {

    private static Messages instance;

    private final ResourceBundle messages;

    private Messages(ResourceBundle messagesBundle) {
        messages = messagesBundle;
    }

    /**
     * Получить экземпляр сообщений
     */
    public static Messages getInstance() {
        if (instance == null) {
            ResourceBundle messagesBundle = ResourceBundle.getBundle("messages");
            instance = new Messages(messagesBundle);
        }
        return instance;
    }

    /**
     * Получить сообщение по ключу
     *
     * @param key ключ
     */
    public String getMessageByKey(String key) {
        return messages.getString(key);
    }

    /**
     * Получить сообщение по ключу с аргументами
     *
     * @param key ключ
     * @param args аргументы
     */
    public String getMessageByKey(String key, Object... args) {
        return String.format(getMessageByKey(key), args);
    }

}
