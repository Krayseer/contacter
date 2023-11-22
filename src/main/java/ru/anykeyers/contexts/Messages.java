package ru.anykeyers.contexts;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Сообщения приложения
 */
public class Messages {

    private final Properties properties;

    public Messages() {
        properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream("src/main/resources/messages.properties")) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Получить сообщение из файла по ключу
     */
    public String getMessageByKey(String key) {
        return properties.getProperty(key);
    }

    /**
     * Получить сообщение из файла по ключу с аргументами
     */
    public String getMessageByKey(String key, Object... args) {
        return String.format(properties.getProperty(key), args);
    }

}
