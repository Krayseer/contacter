package ru.anykeyers.services;

import org.junit.Before;
import org.junit.Test;
import ru.anykeyers.domain.User;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertTrue;

public class ConsoleServiceTest {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    private ConsoleService consoleService;

    @Before
    public void setUp() {
        System.setOut(new PrintStream(output));
    }

    @Test
    public void readUserFromConsoleTest() {
        setConsoleArgs("user\npassword\n");

        User user = consoleService.readUserFromConsole();

        assertEquals("user", user.getUsername());
        assertEquals("password", user.getPassword());
    }

    @Test
    public void readCommandTest() {
        setConsoleArgs("/login");

        String command = consoleService.readCommand();

        assertEquals("/login", command);
    }

    @Test
    public void readInvalidCommandTest() {
        setConsoleArgs("/nothing");

        String command = consoleService.readCommand();

        assertNull(command);
        assertTrue(output.toString().contains("Такой команды не существует, введите /help для просмтора возможных задач"));
    }

    /**
     * Инициировать запись в консоль данных
     */
    private void setConsoleArgs(String args) {
        InputStream in = new ByteArrayInputStream(args.getBytes());
        System.setIn(in);
        consoleService = new ConsoleService(System.in);

    }

}
