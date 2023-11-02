package ru.anykeyers.repositories;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import ru.anykeyers.ApplicationProperties;
import ru.anykeyers.domain.User;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class UserRepositoryTest {

    private UserRepository userRepository;

    @Before
    public void setUp() throws IOException {
        String testPropertiesFilePath = "src/test/resources/application-test.properties";
        ApplicationProperties applicationProperties = new ApplicationProperties(testPropertiesFilePath);
        userRepository = new UserRepository(applicationProperties);
        FileUtils.write(new File(userRepository.getDbFilePath()), "", "UTF-8");
    }

    @Test
    public void existsByUsernameTest() {
        User newUser = new User("user1", "password1");

        userRepository.save(newUser);

        assertTrue(userRepository.existsByUsername("user1"));
        assertFalse(userRepository.existsByUsername("nonexistent_user"));
    }

    @Test
    public void getPasswordByUsernameTest() {
        User user = new User("user", "password");

        userRepository.save(user);

        assertEquals("password", userRepository.getPasswordByUsername("user"));
        assertNull(userRepository.getPasswordByUsername("nonexistent_user"));
    }

    @Test
    public void saveUserTest() {
        User user = new User("new_user", "new_password");

        userRepository.save(user);

        assertTrue(userRepository.existsByUsername("new_user"));
        assertEquals("new_password", userRepository.getPasswordByUsername("new_user"));
    }

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
