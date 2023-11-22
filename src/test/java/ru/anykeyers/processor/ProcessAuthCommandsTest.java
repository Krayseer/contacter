package ru.anykeyers.processor;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Тесты для обработчиков авторизационных команд
 */
public class ProcessAuthCommandsTest extends BaseCommandProcessorTest {

    /**
     * Тесты для команды /login
     */
    @Test
    public void loginTest() {
        String messageWithoutArguments = "/login";
        String expectedWithoutArgumentsResult = "Необходимо ввести параметры [<имя пользователя>]";
        assertEquals(expectedWithoutArgumentsResult, processCommand(messageWithoutArguments));

        String messageWithManyArguments = "/login username, none";
        String expectedWithManyArgumentsResult = "Необходимо ввести параметры [<имя пользователя>]";
        assertEquals(expectedWithManyArgumentsResult, processCommand(messageWithManyArguments));

        String validMessage = "/login username";
        String expectedValidMessageResult = "Вы успешно авторизовались";
        assertEquals(expectedValidMessageResult, processCommand(validMessage));
    }

    /**
     * Тесты для команды /logout
     */
    @Test
    public void logoutTest() {
        String logoutMessage = "/logout";

        String expectedResultWithoutAuthenticate = "Необходимо авторизоваться";
        assertEquals(expectedResultWithoutAuthenticate, processCommand(logoutMessage));

        authenticationService.authenticate("username");
        String expectedResultWithAuthenticate = "Вы успешно вышли из аккаунта";
        assertEquals(expectedResultWithAuthenticate, processCommand(logoutMessage));

        authenticationService.authenticate("username");
        String logoutMessageWithArguments = String.format("%s %s", logoutMessage, "args");
        String expectedResultAuthenticateWithArguments = "Такой команды не существует, введите /help для просмотра возможных команд";
        assertEquals(expectedResultAuthenticateWithArguments, processCommand(logoutMessageWithArguments));
    }

}