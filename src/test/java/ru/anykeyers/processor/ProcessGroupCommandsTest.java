package ru.anykeyers.processor;

import org.junit.Test;
import ru.anykeyers.domain.User;

import static junit.framework.TestCase.assertEquals;

/**
 * Тесты для обработчиков групповых команд
 */
public class ProcessGroupCommandsTest extends BaseCommandProcessorTest {

    private final User user = new User("krebs");

    private final String groupName = "Учеба";

    private final String contactName = "Иван";

    /**
     * Тест команды /add-group
     */
    @Test
    public void testProcessAddGroupCommand() {
        String command = "/add-group";

        testOnCommandNeedAuthenticate(command);
        authenticationService.authenticate(user.getUsername());

        String expectedInvalidMessage = "Необходимо ввести параметры [<название группы>]";
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 0);
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 2);

        String commandToProcess = String.format("/add-group %s", groupName);
        String expectedAddContactProcessCommandResult = "Группа 'Учеба' сохранена";
        assertEquals(expectedAddContactProcessCommandResult, processCommand(commandToProcess));
    }

    /**
     * Тест команды /edit-group-name
     */
    @Test
    public void testProcessEditGroupNameCommand() {
        String command = "/edit-group-name";

        testOnCommandNeedAuthenticate(command);
        authenticationService.authenticate(user.getUsername());

        String expectedInvalidMessage = "Необходимо ввести параметры [<старое название группы>, <новое название группы>]";
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 0);
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 3);

        groupService.addGroup(user, groupName);
        String newName = "newName";
        String commandToProcess = String.format("%s %s, %s", command, groupName, newName);
        String expectedEditContactProcessCommandResult = "Группа 'Учеба' успешно изменена";
        assertEquals(expectedEditContactProcessCommandResult, processCommand(commandToProcess));
    }

    /**
     * Тест команды /group-add-contact
     */
    @Test
    public void testProcessAddContactInGroupCommand() {
        String command = "/group-add-contact";

        testOnCommandNeedAuthenticate(command);
        authenticationService.authenticate(user.getUsername());

        String expectedInvalidMessage = "Необходимо ввести параметры [<название группы>, <имя контакта>]";
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 0);
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 3);

        contactService.addContact(user, contactName);
        groupService.addGroup(user, groupName);

        String commandToProcess = String.format("%s %s, %s", command, groupName, contactName);
        String expectedEditContactProcessCommandResult = "Контакт 'Иван' успешно добавлен в группу 'Учеба'";
        assertEquals(expectedEditContactProcessCommandResult, processCommand(commandToProcess));
    }

    /**
     * Тест команды /group-delete-contact
     */
    @Test
    public void testProcessDeleteContactFromGroupCommand() {
        String command = "/group-delete-contact";

        testOnCommandNeedAuthenticate(command);
        authenticationService.authenticate(user.getUsername());

        String expectedInvalidMessage = "Необходимо ввести параметры [<название группы>, <имя контакта>]";
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 0);
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 3);

        contactService.addContact(user, contactName);
        groupService.addGroup(user, groupName);

        String commandToProcess = String.format("%s %s, %s", command, groupName, contactName);
        String expectedEditContactProcessCommandResult = "Контакт 'Иван' успешно удален из группы 'Учеба'";
        assertEquals(expectedEditContactProcessCommandResult, processCommand(commandToProcess));
    }

    /**
     * Тест команды /delete-group
     */
    @Test
    public void testProcessDeleteGroupTestCommand() {
        String command = "/delete-group";

        testOnCommandNeedAuthenticate(command);
        authenticationService.authenticate(user.getUsername());

        String expectedInvalidMessage = "Необходимо ввести параметры [<название группы>]";
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 0);
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 2);

        groupService.addGroup(user, groupName);
        String commandToProcess = String.format("/delete-group %s", groupName);
        String expectedAddContactProcessCommandResult = "Группа 'Учеба' успешно удалена";
        assertEquals(expectedAddContactProcessCommandResult, processCommand(commandToProcess));
    }

}
