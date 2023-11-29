package ru.anykeyers.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;
import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.UserRepository;

import javax.inject.Inject;

import static org.junit.Assert.*;

/**
 * Тесты для класса {@link AuthenticationService}
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    /**
     * Тестирование получения экземпляра {@link User} по username
     */
    @Test
    public void getUserByUsername() {
        String username = "testUser";
        User expectedUser = new User(username);
        Mockito.when(userRepository.getUserByUsername(username)).thenReturn(expectedUser);

        User actualUser = authenticationService.getUserByUsername(username);

        assertEquals(expectedUser, actualUser);
        Mockito.verify(userRepository, Mockito.times(1)).getUserByUsername(username);
    }

    /**
     * Тестирование сохранения пользователя
     */
    @Test
    public void saveOrUpdateUser() {
        User user = new User("testUser");

        authenticationService.saveOrUpdateUser(user);

        Mockito.verify(userRepository, Mockito.times(1)).saveOrUpdate(user);
    }

    /**
     * Проверить существование пользователя
     */
    @Test
    public void existsUser() {
        String existingUsername = "existingUser";
        String nonExistingUsername = "nonExistingUser";
        Mockito.when(userRepository.existsByUsername(existingUsername)).thenReturn(true);
        Mockito.when(userRepository.existsByUsername(nonExistingUsername)).thenReturn(false);

        boolean existingUserExists = authenticationService.existsUser(existingUsername);
        boolean nonExistingUserExists = authenticationService.existsUser(nonExistingUsername);

        assertTrue(existingUserExists);
        assertFalse(nonExistingUserExists);
        Mockito.verify(userRepository, Mockito.times(1)).existsByUsername(existingUsername);
        Mockito.verify(userRepository, Mockito.times(1)).existsByUsername(nonExistingUsername);
    }

}