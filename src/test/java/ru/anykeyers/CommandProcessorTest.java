package ru.anykeyers;

import org.junit.Before;
import org.junit.Test;
import ru.anykeyers.commands.CommandProcessor;
import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.UserRepository;
import ru.anykeyers.services.AuthenticationService;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertTrue;
import static ru.anykeyers.utils.StreamUtils.setSystemOutputStream;
import static ru.anykeyers.utils.StreamUtils.writeDataInSystemInputStream;

/**
 * Тесты для методов класса {@link CommandProcessor}
 */
public class CommandProcessorTest {

    private CommandProcessor commandProcessor;

    private AuthenticationService authenticationService;

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @Before
    public void setup() {
        String testPropertiesFilePath = "src/test/resources/application-test.properties";
        ApplicationProperties applicationProperties = new ApplicationProperties(testPropertiesFilePath);

        UserRepository userRepository = new UserRepository(applicationProperties);
        authenticationService = new AuthenticationService(userRepository);
        commandProcessor = new CommandProcessor(authenticationService, userRepository);

        setSystemOutputStream(outputStream);
    }

    /**
     * Тест метода {@link CommandProcessor#processCommand(String)} с валидной(существующей) командой
     */
    @Test
    public void processCommandTest_ValidCommand() {
        String userInfo = "username\npassword\n";
        writeDataInSystemInputStream(userInfo);

        String command = "/login";
        commandProcessor.processCommand(command);

        User expectedUser = new User("username", "password");
        String expectedCommandHandleResult = "Вы успешно авторизовались";
        assertTrue(authenticationService.isAuthenticated() && expectedUser.equals(authenticationService.getCurrentUser()));
        assertTrue(outputStream.toString().contains(expectedCommandHandleResult));
    }

    /**
     * Тест метода {@link CommandProcessor#processCommand(String)} с несуществующей командой
     */
    @Test
    public void processCommandTest_InvalidCommand() {
        String command = "/invalid";
        commandProcessor.processCommand(command);

        String expectedCommandHandleResult = "Такой команды не существует, введите /help для просмтора возможных задач";
        assertTrue(outputStream.toString().contains(expectedCommandHandleResult));
    }

}
