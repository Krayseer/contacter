package ru.anykeyers.repository.file;

import ru.anykeyers.bot.BotType;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.common.Mapper;
import ru.anykeyers.service.FileService;
import ru.anykeyers.repository.UserRepository;
import ru.anykeyers.repository.file.service.FileRepositoryService;
import ru.anykeyers.repository.file.service.impl.FileRepositoryServiceImpl;
import ru.anykeyers.service.impl.contact.import_export.txt.TXTFileService;
import ru.anykeyers.service.impl.contact.import_export.txt.domain.TXTUserMapper;

import java.io.File;
import java.util.*;

/**
 * Реализация файловой базы данных для пользователей
 */
public class FileUserRepository implements UserRepository {

    private final Map<String, Set<User>> usersByUsername;

    private final File dbFile;

    private final FileService<User> fileService;

    private final FileRepositoryService<User> repositoryService;

    public FileUserRepository(String userFilePath) {
        dbFile = new File(userFilePath);
        Mapper<User> userMapper = new TXTUserMapper();
        fileService = new TXTFileService<>(userMapper);
        repositoryService = new FileRepositoryServiceImpl<>();
        Collection<User> users = fileService.initDataFromFile(dbFile);
        usersByUsername = repositoryService.getMapFromCollection(users, User::getUsername);
    }

    @Override
    public boolean existsByUsernameAndBotType(String username, BotType botType) {
        String userCompoundKey = generateCompoundUserKey(username, botType);
        return usersByUsername.containsKey(userCompoundKey);
    }

    @Override
    public void saveOrUpdate(User user) {
        usersByUsername.put(user.getUsername(), Collections.singleton(user));
        fileService.saveOrUpdateFile(dbFile, repositoryService.getCollectionFromMap(usersByUsername));
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
