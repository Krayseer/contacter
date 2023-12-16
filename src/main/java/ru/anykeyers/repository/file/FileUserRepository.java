package ru.anykeyers.repository.file;

import ru.anykeyers.bot.BotType;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.common.Mapper;
import ru.anykeyers.repository.file.mapper.FileUserMapper;
import ru.anykeyers.repository.UserRepository;
import ru.anykeyers.repository.file.service.FileService;
import ru.anykeyers.repository.file.service.impl.FileServiceImpl;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализация файловой базы данных для пользователей
 */
public class FileUserRepository implements UserRepository {

    private final Map<String, Set<User>> usersByUsername;

    private final File dbFile;

    private final FileService<User> fileService;

    public FileUserRepository(String userFilePath) {
        dbFile = new File(userFilePath);
        Mapper<User> userMapper = new FileUserMapper();
        fileService = new FileServiceImpl<>(userMapper);
        Collection<User> users = fileService.initDataFromFile(dbFile);
        usersByUsername = users.stream()
                .collect(Collectors.groupingBy(User::getUsername, Collectors.toSet()));
    }

    @Override
    public boolean existsByUsernameAndBotType(String username, BotType botType) {
        String userCompoundKey = generateCompoundUserKey(username, botType);
        return usersByUsername.containsKey(userCompoundKey);
    }

    @Override
    public void saveOrUpdate(User user) {
        usersByUsername.put(user.getUsername(), Collections.singleton(user));
        fileService.saveOrUpdateFile(dbFile, usersByUsername);
    }

    @Override
    public User getUserByUsernameAndBotType(String username, BotType botType) {
        if (!existsByUsernameAndBotType(username, botType)) {
            return null;
        }
        String userCompoundKey = generateCompoundUserKey(username, botType);
        Collection<User> singletonCollectionUser = usersByUsername.get(userCompoundKey);
        return singletonCollectionUser.stream()
                .findFirst()
                .orElseThrow();
    }

    /**
     * Сгенерировать составной ключ(username) для пользователя
     *
     * @param username имя пользователя
     * @param botType тип бота
     */
    private String generateCompoundUserKey(String username, BotType botType) {
        return String.format("%s-%s", username, botType);
    }

}
