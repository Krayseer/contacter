package ru.anykeyers.contexts;

import java.util.ResourceBundle;

/**
 * Настройки приложения
 */
public class ApplicationProperties {

    private final ResourceBundle properties;

    public ApplicationProperties() {
        properties = ResourceBundle.getBundle("application");
    }

    /**
     * Получить настройку из файла по ключу
     * @param key ключ, по которому нужно получить настройку
     * @return настройка
     */
    public String getSetting(String key) {
        return properties.getString(key);
    }

}
