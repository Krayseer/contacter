package ru.anykeyers.security;

import org.apache.commons.io.FileUtils;
import ru.anykeyers.ApplicationProperties;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Класс  отвечает за управление аутентификацией пользователей и ведение списка пользователей.
 * Предоставляет методы для входа в систему, выхода и управления пользователями.
 */
public class AuthenticationManager {

    private final Map<String, String> users = new HashMap<>(); // Карта для хранения учетных данных пользователей.

    private final String usersFilePath; // Путь к файлу, в котором хранятся данные пользователей.

    private User currentUser; // Текущий авторизованный пользователь.

    public AuthenticationManager(ApplicationProperties applicationProperties) {
        usersFilePath = applicationProperties.getSetting("saved-users-file-path");
        initUsersFromFile();
    }

    /**
     * Получает текущего авторизованного пользователя.
     * @return Объект `User`, представляющий текущего пользователя, или null, если ни один пользователь не авторизован.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Пытается войти в систему с проверкой учетных данных и обновляет текущего пользователя.
     * @param user Объект `User` с именем пользователя и паролем для входа в систему.
     */
    public void login(User user) {
        if(!users.containsKey(user.getUsername())) {
            users.put(user.getUsername(), user.getPassword());
        }
        if (isValidUser(user)) {
            currentUser = user;
        }
    }

    /**
     * Выход текущего пользователя из системы.
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * Сохранить добавленных пользователей в БД
     */
    public void saveUsersToFile() {
        File file = new File(usersFilePath);
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
        File file = new File(usersFilePath);
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

    private boolean isValidUser(User user) {
        String username = users.get(user.getUsername());
        return username != null && username.equals(user.getPassword());
    }

}
