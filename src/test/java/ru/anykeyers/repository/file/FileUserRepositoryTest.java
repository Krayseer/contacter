package ru.anykeyers.repository.file;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.anykeyers.bot.BotType;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.repository.UserRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * Тесты для класса {@link FileUserRepository}
 */
public class FileUserRepositoryTest {

    private UserRepository userRepository;

    private File tempDbFile;

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
        List<String> actualUsersLines = Files.readAllLines(tempDbFile.toPath());
        List<String> expectedFileLines = new ArrayList<>();
        expectedFileLines.add("user1-CONSOLE:bot_type=CONSOLE;chat_id=null");
        expectedFileLines.add("user2-CONSOLE:bot_type=CONSOLE;chat_id=null");
        Assert.assertTrue(actualUsersLines.containsAll(expectedFileLines));
    }

}
