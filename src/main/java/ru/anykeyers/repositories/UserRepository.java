package ru.anykeyers.repositories;

import org.apache.commons.io.FileUtils;
import ru.anykeyers.ApplicationProperties;
import ru.anykeyers.domain.User;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс для манипуляции данными с базой данных пользователей приложения
 */
public class UserRepository implements FileDBRepository {

    /**
     * Карта для хранения учетных данных пользователей.
     */
    private final Map<String, String> users = new LinkedHashMap<>();

    /**
     * Путь до файловой БД
     */
    private final String dbFilePath;

    public UserRepository(String dbFilePath) {
        this.dbFilePath = dbFilePath;
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
    public String findPasswordByUsername(String username) {
        return users.get(username);
    }

    /**
     * Сохранить пользователя в {@link #users}
     */
    public void save(User user) {
        users.put(user.getUsername(), user.getPassword());
    }

    @Override
    public void saveAll() {
        File file = new File(dbFilePath);
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

    /**
     * Проинициализировать данные из файловой БД в {@link #users}
     */
    private void initUsersFromFile() {
        File file = new File(dbFilePath);
        try {
            List<String> lines = FileUtils.readLines(file, "UTF-8");
            lines.forEach(line -> {
                String[] userInfo = line.split(":");
                if (userInfo.length == 2) {
                    String username = userInfo[0];
                    String password = userInfo[1];
                    users.put(username, password);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getDbFilePath() {
        return dbFilePath;
    }

}
