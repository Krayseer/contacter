package ru.anykeyers.repositories.file;

import org.apache.commons.io.FileUtils;
import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.UserRepository;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Реализация файловой базы данных
 */
public class FileUserRepository implements UserRepository {

    /**
     * Файловая БД
     */
    private final File dbFile;

    /**
     * Множество, для хранения пользователей
     */
    private final Set<User> users = new HashSet<>();

    public FileUserRepository(String dbFilePath) {
        this.dbFile = new File(dbFilePath);
        initUsersFromFile();
    }

    @Override
    public boolean exists(User user) {
        return users.contains(user);
    }
    @Override
    public void save(User user) {
        users.add(user);
        try {
            FileUtils.writeStringToFile(dbFile, user.toString(), "UTF-8", true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Проинициализировать данные из файловой БД в {@link #users}
     */
    private void initUsersFromFile() {
        try {
            List<String> usersLines = FileUtils.readLines(dbFile, "UTF-8");
            usersLines.forEach(userInfo -> {
                User user = new User(userInfo);
                users.add(user);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
