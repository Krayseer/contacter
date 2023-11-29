package ru.anykeyers.repositories.file;

import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.file.formatters.UserFormatter;
import ru.anykeyers.repositories.UserRepository;
import ru.anykeyers.repositories.file.data.ObjectData;
import ru.anykeyers.repositories.file.data.UserData;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализация файловой базы данных
 */
public class FileUserRepository extends BaseFileRepository<User> implements UserRepository {

    public FileUserRepository(String userFilePath) {
        super(userFilePath);
        formatter = new UserFormatter();
    }

    @Override
    public boolean existsByUsername(String username) {
        return getInfosByUsername().containsKey(username);
    }

    @Override
    public void saveOrUpdate(User user) {
        Map<String, ObjectData<User>> infoByUsername = getInfosByUsername();
        if (infoByUsername.get(user.getUsername()) == null) {
            infoByUsername.put(user.getUsername(), new UserData());
        }
        UserData userInfo = (UserData) infoByUsername.get(user.getUsername());
        userInfo.addData(user);
        updateFile(infoByUsername);
    }

    @Override
    public User getUserByUsername(String username) {
        if (!existsByUsername(username)) {
            return null;
        }
        return (User) getInfosByUsername().get(username).getData();
    }

    private void updateFile(Map<String, ObjectData<User>> infoByUsername) {
        List<String> resultLines = infoByUsername.values().stream()
                .map(userInfo -> (User) userInfo.getData())
                .map(formatter::format)
                .collect(Collectors.toList());
        saveOrUpdateFile(resultLines);
    }

}
