package ru.anykeyers.repository.file;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.anykeyers.bot.BotType;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.repository.UserRepository;
import ru.anykeyers.repository.file.mapper.FileUserMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Stream;

/**
 * Тесты для класса {@link FileUserRepository}
 */
public class FileUserRepositoryTest {

    private UserRepository userRepository;

    private File tempDbFile;

    private final FileUserMapper formatter;

    public FileUserRepositoryTest() {
        formatter = new FileUserMapper();
    }

    @Before
    public void setUp() throws IOException {
        tempDbFile = Files.createTempFile("tempDbFile", ".txt").toFile();
        userRepository = new FileUserRepository(tempDbFile.getPath());
    }

    /**
     * Тест метода {@link UserRepository#saveOrUpdate(User)}
     */
    @Test
    public void saveUserTest() throws IOException {
        // Подготовка
        User user1 = new User("user1", BotType.CONSOLE);
        User user2 = new User("user2", BotType.CONSOLE);

        // Действие
        userRepository.saveOrUpdate(user1);
        userRepository.saveOrUpdate(user2);

        // Проверка
        Set<String> actualUsersLines = new HashSet<>(Files.readAllLines(tempDbFile.toPath()));
        Stream.of(user1, user2)
                .map(formatter::format)
                .forEach(user -> Assert.assertTrue(actualUsersLines.contains(user)));
    }

}
