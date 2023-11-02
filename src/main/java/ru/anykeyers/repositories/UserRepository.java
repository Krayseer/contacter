package ru.anykeyers.repositories;

import org.apache.commons.io.FileUtils;
import ru.anykeyers.ApplicationProperties;
import ru.anykeyers.domain.User;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserRepository implements Repository {

    /**
     * Карта для хранения учетных данных пользователей.
     */
    private final Map<String, String> users = new LinkedHashMap<>();

    private final ApplicationProperties applicationProperties;

    public UserRepository(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
        initUsersFromFile();
    }

    /**
     * Существует ли username в БД
     * @return true, если существует, иначе false
     */
    public boolean existsByUsername(String username) {
        return users.containsKey(username);
    }

    /**
     * Получить пароль по username
     */
    public String getPasswordByUsername(String username) {
        return users.get(username);
    }

    /**
     * Сохранить пользователя в карту пользователей для сохранения в БД
     */
    public void save(User user) {
        users.put(user.getUsername(), user.getPassword());
    }

    /**
     * Сохранить добавленных пользователей из карты в БД
     */
    public void saveAll() {
        File file = new File(getDbFilePath());
        try {
            StringBuilder dataForSave = new StringBuilder();
            for (Map.Entry<String, String> entry : users.entrySet()) {
                String userToSave = String.format("%s:%s%n", entry.getKey(), entry.getValue());
                dataForSave.append(userToSave);
            }
            FileUtils.writeStringToFile(file, dataForSave.toString(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initUsersFromFile() {
        File file = new File(getDbFilePath());
        try {
            List<String> lines = FileUtils.readLines(file, "UTF-8");
            for (String line : lines) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String username = parts[0];
                    String password = parts[1];
                    users.put(username, password);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getDbFilePath() {
        return applicationProperties.getSetting("saved-users-file-path");
    }

}
