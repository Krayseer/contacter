package ru.anykeyers;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Настройки приложения
 */
public class ApplicationProperties {

    private final Properties properties;

    public ApplicationProperties(String propertiesPath) {
        this.properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(propertiesPath)) {
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
