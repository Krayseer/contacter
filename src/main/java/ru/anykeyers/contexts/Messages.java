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
        String pathToMessagesFile = "src/main/resources/messages.properties";
        try (FileInputStream fileInputStream = new FileInputStream(pathToMessagesFile)) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Получить сообщение из файла по ключу
     * @param key ключ сообщения
     * @return сообщение
     */
    public String getMessageByKey(String key) {
        return properties.getProperty(key);
    }

    /**
     * Получить сообщение из файла по ключу с аргументами
     * @param key ключ сообщения
     * @param args аргументы, которые нужно применить к сообщению
     * @return сообщение
     */
    public String getMessageByKey(String key, Object... args) {
        return String.format(properties.getProperty(key), args);
    }

}
