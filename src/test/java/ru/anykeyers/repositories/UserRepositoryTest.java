package ru.anykeyers.repositories;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.anykeyers.domain.User;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Тесты для методов класса {@link UserRepository}
 */
public class UserRepositoryTest {

    private UserRepository userRepository;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() throws IOException {
        File tempDbFile = temporaryFolder.newFile("tempDbFile.txt");
        userRepository = new UserRepository(tempDbFile.getPath());
    }

    /**
     * Тест метода {@link UserRepository#save(User)}
     */
    @Test
    public void saveUserTest() {
        User user = new User("new_user", "new_password");

        userRepository.save(user);

        assertTrue(userRepository.existsByUsername("new_user"));
        assertEquals("new_password", userRepository.findPasswordByUsername("new_user"));
    }

    /**
     * Тест метода {@link UserRepository#findPasswordByUsername(String)}
     */
    @Test
    public void getPasswordByUsernameTest() {
        User user = new User("user", "password");

        userRepository.save(user);

        assertEquals("password", userRepository.findPasswordByUsername("user"));
        assertNull(userRepository.findPasswordByUsername("nonexistent_user"));
    }

    /**
     * Тест метода {@link UserRepository#existsByUsername(String)}
     */
    @Test
    public void existsByUsernameTest() {
        User newUser = new User("user1", "password1");

        userRepository.save(newUser);

        assertTrue(userRepository.existsByUsername("user1"));
        assertFalse(userRepository.existsByUsername("nonexistent_user"));
    }

    /**
     * Тест метода {@link UserRepository#saveAll()}
     */
    @Test
    public void testSaveAll() throws IOException {
        User user1 = new User("user1", "password1");
        User user2 = new User("user2", "password2");

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.saveAll();

        File dbFile = new File(userRepository.getDbFilePath());
        assertTrue(dbFile.exists());
        List<String> lines = FileUtils.readLines(dbFile, "UTF-8");
        assertEquals(2, lines.size());
        assertTrue(lines.contains("user1:password1"));
        assertTrue(lines.contains("user2:password2"));
    }

}
