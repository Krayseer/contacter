package ru.anykeyers.contexts;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Настройки приложения
 */
public class ApplicationProperties {

    private final Properties properties;

    public ApplicationProperties() {
        this.properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream("src/main/resources/application.properties")) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Получить настройку из файла по ключу
     */
    public String getSetting(String key) {
        return properties.getProperty(key);
    }

}
