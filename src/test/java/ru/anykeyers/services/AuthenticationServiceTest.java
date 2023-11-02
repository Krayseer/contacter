package ru.anykeyers.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ru.anykeyers.ApplicationProperties;
import ru.anykeyers.repositories.UserRepository;
import ru.anykeyers.domain.User;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationServiceTest {

    @Mock
    private ConsoleService consoleService;

    private AuthenticationService authenticationService;

    private UserRepository userRepository;

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @Before
    public void setup() {
        String testPropertiesFilePath = "src/test/resources/application-test.properties";
        userRepository = new UserRepository(new ApplicationProperties(testPropertiesFilePath));
        authenticationService = new AuthenticationService(consoleService, userRepository);
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    public void isAuthenticatedTest() {
        User user = new User("username", "password");

        when(consoleService.readUserFromConsole()).thenReturn(user);
        authenticationService.authenticate();

        assertTrue(authenticationService.isAuthenticated());
    }

    @Test
    public void authenticateWithValidUserTest() {
        User validUser = new User("username", "password");

        when(consoleService.readUserFromConsole()).thenReturn(validUser);
        authenticationService.authenticate();

        assertTrue(authenticationService.isAuthenticated());
        assertTrue(outputStream.toString().contains("Вы успешно авторизовались"));
    }

    @Test
    public void authenticateWithInvalidUserTest() {
        User validUser = new User("username", "password");
        User invalidUser = new User("username", "wrong_password");

        userRepository.save(validUser);
        when(consoleService.readUserFromConsole()).thenReturn(invalidUser);
        authenticationService.authenticate();

        assertFalse(authenticationService.isAuthenticated());
        assertTrue(outputStream.toString().contains("Пароль был введён неверно"));
    }

    @Test
    public void logoutUserTest() {
        User user = new User("username", "password");

        when(consoleService.readUserFromConsole()).thenReturn(user);
        authenticationService.authenticate();
        authenticationService.logoutUser();

        assertFalse(authenticationService.isAuthenticated());
        assertTrue(outputStream.toString().contains("Вы успешно вышли из аккаунта"));
    }

}
