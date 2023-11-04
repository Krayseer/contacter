package ru.anykeyers.services;

import org.junit.Before;
import org.junit.Test;
import ru.anykeyers.domain.User;

import java.io.ByteArrayOutputStream;

import static junit.framework.TestCase.assertEquals;
import static ru.anykeyers.utils.StreamUtils.setSystemOutputStream;
import static ru.anykeyers.utils.StreamUtils.writeDataInSystemInputStream;

/**
 * Тесты для методов класса {@link ConsoleService}
 */
public class ConsoleServiceTest {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    private final ConsoleService consoleService = new ConsoleService();

    @Before
    public void setUp() {
        setSystemOutputStream(outputStream);
    }

    /**
     * Тест метода {@link ConsoleService#readUserFromConsole()}
     */
    @Test
    public void readUserFromConsoleTest() {
        String userInfo = "user\npassword\n";
        writeDataInSystemInputStream(userInfo);

        User user = consoleService.readUserFromConsole();

        User expectedUser = new User("user", "password");
        assertEquals(expectedUser, user);
    }

}
