package ru.anykeyers;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.anykeyers.commands.CommandProcessor;
import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.repositories.GroupRepository;
import ru.anykeyers.repositories.UserRepository;
import ru.anykeyers.services.AuthenticationService;
import ru.anykeyers.services.ContactService;
import ru.anykeyers.services.GroupService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

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

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setup() throws IOException {
        File tempDbFile = temporaryFolder.newFile("tempDbFile.txt");
        UserRepository userRepository = new UserRepository(tempDbFile.getPath());
        ContactRepository contactRepository = new ContactRepository(tempDbFile.getPath());
        GroupRepository groupRepository = new GroupRepository(tempDbFile.getPath());
        authenticationService = new AuthenticationService(userRepository);
        ContactService contactService = new ContactService(contactRepository, authenticationService);
        GroupService groupService = new GroupService(authenticationService, contactService, groupRepository);
        commandProcessor = new CommandProcessor(authenticationService, contactService, groupService,
                groupRepository, contactRepository, userRepository);

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
