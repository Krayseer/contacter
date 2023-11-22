package ru.anykeyers.processor;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Тесты для обработчиков общих команд
 */
public class ProcessCommonCommandsTest extends BaseCommandProcessorTest {

    /**
     * Тест обработки несуществующей команды
     */
    @Test
    public void notExistsCommandProcessTest() {
        String nonExistsCommand = "/non-exists";

        String expectedNonExistsCommandProcess = "Такой команды не существует, введите /help для просмотра возможных команд";
        assertEquals(expectedNonExistsCommandProcess, processCommand(nonExistsCommand));
    }

}
