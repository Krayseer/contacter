package ru.anykeyers.processor;

import org.junit.Before;
import ru.anykeyers.commands.CommandProcessor;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.repositories.GroupRepository;
import ru.anykeyers.repositories.UserRepository;
import ru.anykeyers.repositories.file.FileContactRepository;
import ru.anykeyers.repositories.file.FileGroupRepository;
import ru.anykeyers.repositories.file.FileUserRepository;
import ru.anykeyers.services.*;
import ru.anykeyers.services.AuthenticationService;
import ru.anykeyers.services.ContactService;
import ru.anykeyers.services.GroupService;
import ru.anykeyers.services.ImportExportService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static junit.framework.TestCase.assertEquals;

/**
 * Базовый класс для работы с тестами у класса {@link CommandProcessor} <br/>
 * Включает в себя инициализацию компонентов перед тестами и общие тестовые случаи
 */
abstract class BaseCommandProcessorTest {

    private CommandProcessor commandProcessor;

    protected AuthenticationService authenticationService;

    protected ContactService contactService;

    protected GroupService groupService;

    protected SortService sortService;

    protected ImportExportService importExportService;

    @Before
    public void setup() throws IOException {
        File tempDbFile = Files.createTempFile("tempDbFile", ".txt").toFile();

        UserRepository userRepository = new FileUserRepository(tempDbFile.getPath());
        ContactRepository contactRepository = new FileContactRepository(tempDbFile.getPath());
        GroupRepository groupRepository = new FileGroupRepository(tempDbFile.getPath());

        authenticationService = new AuthenticationService(userRepository);
        contactService = new ContactService(contactRepository);
        groupService = new GroupService(groupRepository, contactRepository);
        importExportService = new ImportExportService(contactRepository);
        SearchService contactSearch = new SearchService(contactRepository);
        FilterService filterService = new FilterService(contactRepository);
        SortService sortService = new SortService(contactRepository);

        commandProcessor = new CommandProcessor(
                authenticationService,
                contactService,
                groupService,
                contactSearch,
                filterService,
                sortService,
                importExportService
        );
    }

    /**
     * Обработка команды
     */
    protected String processCommand(String message) {
        return commandProcessor.processCommand(message);
    }

    /**
     * Протестировать процесс выполнения команды без авторизованного пользователя с последующей авторизацией
     */
    protected void testOnCommandNeedAuthenticate(String message) {
        String expectedNonAuthenticatedProcessMessageResult = "Необходимо авторизоваться";
        assertEquals(expectedNonAuthenticatedProcessMessageResult, processCommand(message));
    }

    /**
     * Протестировать процесс обработки команды с невалидным колличеством аргументов для комманды
     * @param command команда для обработки
     * @param expectedErrorMessage ожидаемое сообщение ошибки
     * @param argumentsCount количество аргументов
     */
    protected void testCommandWithInvalidArguments(String command, String expectedErrorMessage, int argumentsCount) {
        String[] arguments = generateStringArray(argumentsCount);
        String formattedString = arguments.length != 0
                ? String.format("%s %s", command, String.join(",", arguments))
                : command;
        assertEquals(expectedErrorMessage, processCommand(formattedString));
    }

    /**
     * Сгенерировать массив строк длиной, заданной в argumentsCount
     * @param argumentsCount количество аргументов
     */
    private String[] generateStringArray(int argumentsCount) {
        String[] result = new String[argumentsCount];
        for (int i = 0; i < argumentsCount; i++) {
            result[i] = String.valueOf(i + 1);
        }
        return result;
    }

}
