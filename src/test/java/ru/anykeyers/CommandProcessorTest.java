package ru.anykeyers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.anykeyers.commands.CommandHandler;
import ru.anykeyers.commands.CommandProcessor;
import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.UserRepository;
import ru.anykeyers.services.AuthenticationService;
import ru.anykeyers.services.ConsoleService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CommandProcessorTest {

    private CommandProcessor commandProcessor;
    private AuthenticationService authService;
    private UserRepository userRepository;
    private ConsoleService consoleService;

    @Before
    public void setup() {
        authService = mock(AuthenticationService.class);
        userRepository = mock(UserRepository.class);
        consoleService = mock(ConsoleService.class);
        commandProcessor = new CommandProcessor(authService, userRepository, consoleService);
    }

    @Test
    public void processCommandTest_ValidCommand() {
        String commandValue = "/login";
        CommandHandler commandHandler = mock(CommandHandler.class);

        Mockito.lenient().when(consoleService.readUserFromConsole()).thenReturn(new User("username", "password"));
        Mockito.lenient().when(userRepository.existsByUsername(anyString())).thenReturn(true);
        Mockito.lenient().when(userRepository.getPasswordByUsername(anyString())).thenReturn("password");
        Mockito.lenient().when(authService.isAuthenticated()).thenReturn(true);

        commandProcessor.processCommand(commandValue);
    }

    @Test
    public void processCommandTest_InvalidCommand() {
        String commandValue = "/invalid";
        CommandHandler commandHandler = mock(CommandHandler.class);

        Mockito.lenient().when(consoleService.readUserFromConsole()).thenReturn(new User("username", "password"));
        Mockito.lenient().when(userRepository.existsByUsername(anyString())).thenReturn(false);
        Mockito.lenient().when(userRepository.getPasswordByUsername(anyString())).thenReturn("password");
        Mockito.lenient().when(authService.isAuthenticated()).thenReturn(false);

        commandProcessor.processCommand(commandValue);
    }

}
