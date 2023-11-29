package ru.anykeyers.repositories.file;

import org.junit.Before;
import org.junit.Test;
import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.UserRepository;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.*;

/**
 * Тесты для методов класса {@link UserRepository}
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
    public void testUserSave() throws IOException {
        User user1 = new User("user1");
        User user2 = new User("user2");

        userRepository.saveOrUpdate(user1);
        userRepository.saveOrUpdate(user2);

        String expected = String.format("%s%s", user1, user2);
        String actual = Files.readString(tempDbFile.toPath(), StandardCharsets.UTF_8).replace("\r\n", "");
        assertEquals(expected, actual);
    }

}
