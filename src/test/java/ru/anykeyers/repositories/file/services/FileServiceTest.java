package ru.anykeyers.repositories.file.services;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import ru.anykeyers.bots.BotType;
import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.file.mapper.Mapper;
import ru.anykeyers.repositories.file.mapper.UserMapper;
import ru.anykeyers.repositories.file.services.impl.FileServiceImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Тесты для класса {@link FileService}
 */
public class FileServiceTest {

    /**
     * Тестирование составления карты вида [имя пользователя -> коллекция данных] из файловой БД
     */
    @Test
    public void initUsersDataFromFileTest() throws IOException {
        // Подготовка
        User firstUser = new User("testUser", BotType.TELEGRAM_BOT);
        User secondUser = new User("secondUser", BotType.CONSOLE);
        List<User> users = List.of(firstUser, secondUser);

        Mapper<User> mapper = new UserMapper();
        FileService<User> fileService = new FileServiceImpl<>(mapper);

        File dbFile = Files.createTempFile("tempDbFile", "txt").toFile();
        FileUtils.writeLines(dbFile, users.stream().map(mapper::format).collect(Collectors.toList()));

        // Действие
        Collection<User> actualUsers = fileService.initDataFromFile(dbFile);

        // Проверка
        actualUsers.forEach(actualUser -> Assert.assertTrue(users.contains(actualUser)));
    }

    /**
     * Тестирование сохранения измененных данных в файловую БД
     */
    @Test
    public void saveOrUpdateUsersFileTest() throws IOException {
        // Подготовка
        User firstUser = new User("testUser", BotType.TELEGRAM_BOT);
        User secondUser = new User("secondUser", BotType.CONSOLE);

        Map<String, Set<User>> usersMap = new HashMap<>();
        usersMap.put(firstUser.getUsername(), Collections.singleton(firstUser));
        usersMap.put(secondUser.getUsername(), Collections.singleton(secondUser));

        Mapper<User> mapper = new UserMapper();
        FileService<User> fileService = new FileServiceImpl<>(mapper);

        File dbFile = Files.createTempFile("tempDbFile", "txt").toFile();

        // Действие
        fileService.saveOrUpdateFile(dbFile, usersMap);
        List<String> actualLines = FileUtils.readLines(dbFile, "UTF-8");

        // Проверка
        List<String> expectedUsersStrings = new ArrayList<>();
        expectedUsersStrings.add(mapper.format(firstUser));
        expectedUsersStrings.add(mapper.format(secondUser));
        actualLines.forEach(line -> Assert.assertTrue(expectedUsersStrings.contains(line)));
    }

}