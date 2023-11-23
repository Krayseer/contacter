package ru.anykeyers.processor;

import org.junit.Test;
import ru.anykeyers.domain.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static junit.framework.TestCase.assertEquals;

/**
 * Тесты для обработчиков команд импорта экспорта
 */
public class ProcessImportExportCommandsTest extends BaseCommandProcessorTest {

    private final User user = new User("peter");

    /**
     * Тест команды /import
     */
    @Test
    public void testProcessImportCommand() {
        String command = "/import";
        String contactsFilePath = "src/test/resources/files/contacts.xml";

        testOnCommandNeedAuthenticate(command);
        authenticationService.authenticate(user.getUsername());

        String expectedInvalidMessage = "Необходимо ввести параметры [<путь до файла импорта>]";
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 0);
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 2);

        String commandToProcess = String.format("/import %s", contactsFilePath);
        String expectedImportCommandProcessResult =
                "Импорт контактов из файла 'src/test/resources/files/contacts.xml' успешно выполнен";
        assertEquals(expectedImportCommandProcessResult, processCommand(commandToProcess));
    }

    /**
     * Тест команды /export
     */
    @Test
    public void testProcessExportCommand() throws IOException {
        String command = "/export";
        File tempFile = Files.createTempFile("temp_file", ".xml").toFile();

        testOnCommandNeedAuthenticate(command);
        authenticationService.authenticate(user.getUsername());

        String expectedInvalidMessage = "Необходимо ввести параметры [<путь до файла экспорта>]";
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 0);
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 2);

        String commandToProcess = String.format("/export %s", tempFile.getPath());
        String expectedImportCommandProcessResult =
                String.format("Экспорт контактов в файл '%s' успешно выполнен", tempFile.getPath());
        assertEquals(expectedImportCommandProcessResult, processCommand(commandToProcess));
    }

}
