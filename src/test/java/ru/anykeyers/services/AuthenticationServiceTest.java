package ru.anykeyers.services;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.anykeyers.repositories.UserRepository;
import ru.anykeyers.domain.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;
import static ru.anykeyers.utils.StreamUtils.setSystemOutputStream;
import static ru.anykeyers.utils.StreamUtils.writeDataInSystemInputStream;

/**
 * Тесты для методов класса {@link AuthenticationService}
 */
public class AuthenticationServiceTest {

    private UserRepository userRepository;

    private AuthenticationService authenticationService;

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setup() throws IOException {
        File tempDbFile = temporaryFolder.newFile("tempDbFile.txt");
        userRepository = new UserRepository(tempDbFile.getPath());
        authenticationService = new AuthenticationService(userRepository);

        setSystemOutputStream(outputStream);
    }

    /**
     * Тест метода {@link AuthenticationService#authenticate()} с валидным введённым пользователем
     */
    @Test
    public void authenticateWithValidUserTest() {
        String userInfo = "user\npassword\n";
        writeDataInSystemInputStream(userInfo);

        authenticationService.authenticate();

        String expectedResult = "Вы успешно авторизовались";
        assertTrue(authenticationService.isAuthenticated());
        assertTrue(outputStream.toString().contains(expectedResult));
    }

    /**
     * Тест метода {@link AuthenticationService#authenticate()} c невалидным введённым пользователем(с неверно введённым паролем)
     */
    @Test
    public void authenticateWithInvalidUserTest() {
        String invalidUserInfo = "username\nwrong_password\n";
        writeDataInSystemInputStream(invalidUserInfo);

        User validUser = new User("username", "password");
        userRepository.save(validUser);

        authenticationService.authenticate();

        String expectedResult = "Пароль был введён неверно";
        assertFalse(authenticationService.isAuthenticated());
        assertTrue(outputStream.toString().contains(expectedResult));
    }

    /**
     * Тест метода {@link AuthenticationService#getCurrentUser()}
     */
    @Test
    public void getCurrentUserTest() {
        String userInfo = "username\npassword\n";
        writeDataInSystemInputStream(userInfo);

        assertNull(authenticationService.getCurrentUser());

        authenticationService.authenticate();

        User expectedUser = new User("username", "password");
        assertNotNull(authenticationService.getCurrentUser());
        assertEquals(expectedUser, authenticationService.getCurrentUser());
    }

    /**
     * Тест метода {@link AuthenticationService#logoutUser()}
     */
    @Test
    public void logoutUserTest() {
        String userInfo = "username\npassword\n";
        writeDataInSystemInputStream(userInfo);

        User user = new User("username", "password");
        userRepository.save(user);

        authenticationService.authenticate();
        assertTrue(authenticationService.isAuthenticated());

        String expectedResult = "Вы успешно вышли из аккаунта";
        authenticationService.logoutUser();
        assertFalse(authenticationService.isAuthenticated());
        assertTrue(outputStream.toString().contains(expectedResult));
    }

}
