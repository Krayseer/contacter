package ru.anykeyers.processor;

import org.junit.Test;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.User;

import static junit.framework.TestCase.assertEquals;

/**
 * Тесты для обработчиков контактных коммандд
 */
public class ProcessContactCommandsTest extends BaseCommandProcessorTest {

    private final String contactName = "Егор Егоров";

    private final User user = new User("username");

    /**
     * Тест команды /add-contact
     */
    @Test
    public void testProcessAddContactCommand() {
        String command = "/add-contact";

        testOnCommandNeedAuthenticate(command);
        authenticationService.authenticate(user.getUsername());

        String expectedInvalidMessage = "Необходимо ввести параметры [<имя контакта>]";
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 0);
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 2);

        String commandToProcess = String.format("/add-contact %s", contactName);
        String expectedAddContactProcessCommandResult = "Контакт 'Егор Егоров' сохранен";
        assertEquals(expectedAddContactProcessCommandResult, processCommand(commandToProcess));
    }

    /**
     * Тесты команды /edit-contact-name
     */
    @Test
    public void testProcessEditContactNameCommand() {
        String command = "/edit-contact-name";

        testOnCommandNeedAuthenticate(command);
        authenticationService.authenticate(user.getUsername());

        String expectedInvalidMessage = "Необходимо ввести параметры [<старое имя контакта>, <новое имя контакта>]";
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 0);
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 3);

        contactService.addContact(user, contactName);
        String newName = "newName";
        String commandToProcess = String.format("%s %s, %s", command, contactName, newName);
        String expectedEditContactProcessCommandResult = "Контакт 'Егор Егоров' успешно изменен";
        assertEquals(expectedEditContactProcessCommandResult, processCommand(commandToProcess));
    }

    /**
     * Тесты команды /edit-contact-name
     */
    @Test
    public void testProcessEditContactPhoneCommand() {
        String command = "/edit-contact-phone";

        testOnCommandNeedAuthenticate(command);
        authenticationService.authenticate(user.getUsername());

        String expectedInvalidMessage = "Необходимо ввести параметры [<имя контакта>, <новый номер телефона>]";
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 1);
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 3);

        contactService.addContact(user, contactName);
        String newPhoneNumber = "123412342134";
        String commandToProcess = String.format("%s %s, %s", command, contactName, newPhoneNumber);
        String expectedAddContactProcessCommandResult = "Контакт 'Егор Егоров' успешно изменен";
        assertEquals(expectedAddContactProcessCommandResult, processCommand(commandToProcess));
    }

    /**
     * Тест команды /delete-contact-by-name
     */
    @Test
    public void testProcessDeleteContactByNameCommand() {
        String command = "/delete-contact-by-name";

        testOnCommandNeedAuthenticate(command);
        authenticationService.authenticate(user.getUsername());

        String expectedInvalidMessage = "Необходимо ввести параметры [<имя контакта>]";
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 0);
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 2);

        contactService.addContact(user, contactName);

        String commandToProcess = String.format("%s %s", command, contactName);
        String expectedDeleteContactProcessCommandResult = "Контакт 'Егор Егоров' успешно удален";
        assertEquals(expectedDeleteContactProcessCommandResult, processCommand(commandToProcess));
    }

    /**
     * Тест команды /delete-contact-by-phone
     */
    @Test
    public void testProcessDeleteContactByPhoneNumberCommand() {
        String command = "/delete-contact-by-phone";

        testOnCommandNeedAuthenticate(command);
        authenticationService.authenticate(user.getUsername());

        String expectedInvalidMessage = "Необходимо ввести параметры [<номер телефона>]";
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 0);
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 2);

        contactService.addContact(user, contactName);
        String phoneNumber = "123465761";
        contactService.editContact(user, contactName, "123465761", Contact.Field.CONTACT_PHONE_NUMBER);

        String commandToProcess = String.format("%s %s", command, phoneNumber);
        String expectedDeleteContactProcessCommandResult = "Контакт 'Егор Егоров' успешно удален";
        assertEquals(expectedDeleteContactProcessCommandResult, processCommand(commandToProcess));
    }

    /**
     * Тест команды /edit-contact-gender
     */
    @Test
    public void testProcessEditContactGenderCommand() {
        String command = "/edit-contact-gender";

        testOnCommandNeedAuthenticate(command);
        authenticationService.authenticate(user.getUsername());

        String expectedInvalidMessage = "Необходимо ввести параметры [<имя контакта>, <новый пол>]";
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 0);
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 3);

        contactService.addContact(user, contactName);
        String gender = "Егор Егоров,Мужчина";
        contactService.editContact(user, contactName, "Мужской", Contact.Field.CONTACT_GENDER);

        String commandToProcess = String.format("%s %s", command, gender);
        String expectedDeleteContactProcessCommandResult = "Контакт 'Егор Егоров' успешно изменен";
        assertEquals(expectedDeleteContactProcessCommandResult, processCommand(commandToProcess));
    }

    /**
     * Тест команды /edit-contact-age
     */
    @Test
    public void testProcessEditContactAgeCommand() {
        String command = "/edit-contact-age";

        testOnCommandNeedAuthenticate(command);
        authenticationService.authenticate(user.getUsername());

        String expectedInvalidMessage = "Необходимо ввести параметры [<имя контакта>, <новый возраст>]";
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 0);
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 3);

        contactService.addContact(user, contactName);
        String gender = "Егор Егоров,20";
        contactService.editContact(user, contactName, "20", Contact.Field.CONTACT_GENDER);

        String commandToProcess = String.format("%s %s", command, gender);
        String expectedDeleteContactProcessCommandResult = "Контакт 'Егор Егоров' успешно изменен";
        assertEquals(expectedDeleteContactProcessCommandResult, processCommand(commandToProcess));
    }

    /**
     * Тест команды /block-contact
     */
    @Test
    public void testProcessBlockContactCommand() {
        String command = "/block-contact";

        testOnCommandNeedAuthenticate(command);
        authenticationService.authenticate(user.getUsername());

        String expectedInvalidMessage = "Необходимо ввести параметры [<имя контакта>]";
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 0);
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 2);

        contactService.addContact(user, contactName);
        String gender = "Егор Егоров";

        String commandToProcess = String.format("%s %s", command, gender);
        String expectedDeleteContactProcessCommandResult = "Контакт 'Егор Егоров' успешно заблокирован";
        assertEquals(expectedDeleteContactProcessCommandResult, processCommand(commandToProcess));
    }

    /**
     * Тест команды /unblock-contact
     */
    @Test
    public void testProcessUnblockContactCommand() {
        String command = "/unblock-contact";

        testOnCommandNeedAuthenticate(command);
        authenticationService.authenticate(user.getUsername());

        String expectedInvalidMessage = "Необходимо ввести параметры [<имя контакта>]";
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 0);
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 2);

        contactService.addContact(user, contactName);
        String gender = "Егор Егоров";
        contactService.blockContact(user, contactName);

        String commandToProcess = String.format("%s %s", command, gender);
        String expectedDeleteContactProcessCommandResult = "Контакт 'Егор Егоров' успешно разблокирован";
        assertEquals(expectedDeleteContactProcessCommandResult, processCommand(commandToProcess));
    }

}
