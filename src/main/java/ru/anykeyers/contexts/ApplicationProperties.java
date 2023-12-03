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
        String pathToApplicationProperties = "src/main/resources/application.properties";
        try (FileInputStream fileInputStream = new FileInputStream(pathToApplicationProperties)) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Получить настройку из файла по ключу
     * @param key ключ, по которому нужно получить настройку
     * @return настройка
     */
    public String getSetting(String key) {
        return properties.getProperty(key);
    }

}
