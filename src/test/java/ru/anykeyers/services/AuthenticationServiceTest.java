package ru.anykeyers.services;

import org.junit.Before;
import org.junit.Test;
import ru.anykeyers.repositories.UserRepository;
import ru.anykeyers.repositories.file.FileUserRepository;
import ru.anykeyers.domain.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.*;

/**
 * Тесты для методов класса {@link AuthenticationService}
 */
public class AuthenticationServiceTest {

    private AuthenticationService authenticationService;

    private UserRepository userRepository;

    private final User user = new User("username");

    @Before
    public void setup() throws IOException {
        File tempDbFile = Files.createTempFile("tempDbFile", ".txt").toFile();
        userRepository = new FileUserRepository(tempDbFile.getPath());
        authenticationService = new AuthenticationService(userRepository);
    }

    /**
     * Тест метода {@link AuthenticationService#authenticate(String)}
     */
    @Test
    public void testAuthenticateUser() {
        String authResult = authenticationService.authenticate(user.getUsername());

        String expectedResult = "Вы успешно авторизовались";
        assertTrue(userRepository.exists(user));
        assertTrue(authenticationService.isAuthenticated());
        assertEquals(expectedResult, authResult);
    }

    /**
     * Тест метода {@link AuthenticationService#getCurrentUser()}
     */
    @Test
    public void testGetCurrentUser() {
        authenticationService.authenticate(user.getUsername());

        assertNotNull(authenticationService.getCurrentUser());
        assertEquals(user, authenticationService.getCurrentUser());
    }

    /**
     * Тест метода {@link AuthenticationService#logoutUser()}
     */
    @Test
    public void testLogoutUser() {
        authenticationService.authenticate(user.getUsername());
        String logoutResult = authenticationService.logoutUser();

        String expectedResult = "Вы успешно вышли из аккаунта";
        assertFalse(authenticationService.isAuthenticated());
        assertEquals(expectedResult, logoutResult);
    }

}
