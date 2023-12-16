package ru.anykeyers.common;

import java.util.ResourceBundle;

/**
 * Настройки приложения
 */
public class ApplicationProperties {

    private static ApplicationProperties instance;

    private final ResourceBundle applicationProperties;

    private ApplicationProperties(ResourceBundle applicationPropertiesBundle) {
        applicationProperties = applicationPropertiesBundle;
    }

    /**
     * Получить экземпляр настроек
     */
    public static ApplicationProperties getInstance() {
        if (instance == null) {
            ResourceBundle applicationPropertiesBundle = ResourceBundle.getBundle("application");
            instance = new ApplicationProperties(applicationPropertiesBundle);
        }
        return instance;
    }

    /**
     * Получить настройку по ключу
     *
     * @param key ключ
     */
    public String getSettingByKey(String key) {
        return applicationProperties.getString(key);
    }

}
