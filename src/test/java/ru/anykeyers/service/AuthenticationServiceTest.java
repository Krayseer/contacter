package ru.anykeyers.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.anykeyers.bot.BotType;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.repository.UserRepository;
import ru.anykeyers.service.impl.AuthenticationServiceImpl;

/**
 * Тесты для сервиса {@link AuthenticationService}
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    /**
     * Тестирование получения экземпляра {@link User} по username
     */
    @Test
    public void getUserByUsernameTest() {
        // Подготовка
        User expectedUser = new User("testUser", BotType.CONSOLE);

        // Действие
        Mockito.when(userRepository.getUserByUsernameAndBotType(expectedUser.getUsername(), expectedUser.getBotType()))
                .thenReturn(expectedUser);
        User actualUser = authenticationService.getUserByUsernameAndBotType(expectedUser.getUsername(), expectedUser.getBotType());

        // Проверка
        Assert.assertEquals(expectedUser, actualUser);
        Mockito.verify(userRepository, Mockito.times(1))
                .getUserByUsernameAndBotType(expectedUser.getUsername(), expectedUser.getBotType());
    }

    /**
     * Тестирование сохранения пользователя
     */
    @Test
    public void saveOrUpdateUserTest() {
        // Подготовка
        User user = new User("testUser");

        // Действие
        authenticationService.saveOrUpdateUser(user);

        // Проверка
        Mockito.verify(userRepository, Mockito.times(1)).saveOrUpdate(user);
    }

    /**
     * Проверить существование пользователя
     */
    @Test
    public void existsUserTest() {
        // Подготовка
        String existingUsername = "existingUser";
        String nonExistingUsername = "nonExistingUser";
        Mockito.when(userRepository.existsByUsernameAndBotType(existingUsername, BotType.CONSOLE)).thenReturn(true);
        Mockito.when(userRepository.existsByUsernameAndBotType(nonExistingUsername, BotType.CONSOLE)).thenReturn(false);

        // Действие
        boolean existingUserExists = authenticationService.existsUserByUsernameAndBotType(existingUsername, BotType.CONSOLE);
        boolean nonExistingUserExists = authenticationService.existsUserByUsernameAndBotType(nonExistingUsername, BotType.CONSOLE);

        // Проверка
        Assert.assertTrue(existingUserExists);
        Assert.assertFalse(nonExistingUserExists);
        Mockito.verify(userRepository, Mockito.times(1)).existsByUsernameAndBotType(existingUsername, BotType.CONSOLE);
        Mockito.verify(userRepository, Mockito.times(1)).existsByUsernameAndBotType(nonExistingUsername, BotType.CONSOLE);
    }

}