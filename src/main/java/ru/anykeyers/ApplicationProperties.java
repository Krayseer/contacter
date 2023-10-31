package ru.anykeyers;

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
        String propertiesPath = "src/main/resources/application.properties";
        try (FileInputStream fileInputStream = new FileInputStream(propertiesPath)) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getSetting(String key) {
        return properties.getProperty(key);
    }

}
