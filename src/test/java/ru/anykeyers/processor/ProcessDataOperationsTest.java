package ru.anykeyers.processor;

import org.junit.Before;
import org.junit.Test;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.repositories.file.FileContactRepository;
import ru.anykeyers.services.ContactService;
import ru.anykeyers.services.SortService;

import java.io.File;
import java.nio.file.Files;

import static junit.framework.TestCase.assertEquals;

public class ProcessDataOperationsTest extends BaseCommandProcessorTest {

    private final String contactName = "Никита Бураков";

    private SortService sortService;

    private User user = new User("nikita");

    private Contact firstContact = new Contact("nikita", "1", "Никита Бураков",
            35, "Женщина", "BLOCK", "7906806");
    private Contact secondContact = new Contact("nikita", "2", "Иван Бураков",
            19, "Мужчина", "UNBLOCK", "7906");

    @Before
    public void setUp() throws Exception {
        File tempDbFile = Files.createTempFile("tempDbFile", ".txt").toFile();
    }

    /**
     * Тест команды /search-contact-by-name
     */
    @Test
    public void testProcessSearchContactByNameCommand() {
        String command = "/search-contact-by-name";

        testOnCommandNeedAuthenticate(command);
        authenticationService.authenticate(user.getUsername());

        String expectedInvalidMessage = "Необходимо ввести параметры [<имя контакта>]";
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 0);
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 2);
        contactService.addContact(user, contactName);
        contactService.addContact(user, "Иван Иванов");


        String commandToProcess = String.format("/search-contact-by-name %s", contactName);
        String expectedAddContactProcessCommandResult = "Найденный контакт: 'Имя: Никита Бураков, Возраст: Возраст не определен, Пол: Пол не определен, Номер телефона: Номер телефона не определен'";
        assertEquals(expectedAddContactProcessCommandResult, processCommand(commandToProcess));
    }

    /**
     * Тест команды /search-contact-by-phone
     */
    @Test
    public void testProcessSearchContactByPhoneCommand() {
        String command = "/search-contact-by-phone";

        testOnCommandNeedAuthenticate(command);
        authenticationService.authenticate(user.getUsername());

        String expectedInvalidMessage = "Необходимо ввести параметры [<номер телефона>]";
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 0);
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 2);
        contactService.addContact(user, contactName);
        contactService.addContact(user, "Иван Иванов");
        contactService.editContact(user, "Никита Бураков", "79068065041", Contact.Field.CONTACT_PHONE_NUMBER);


        String commandToProcess = String.format("/search-contact-by-phone %s", "79068065041");
        String expectedAddContactProcessCommandResult = "Найденный контакт: 'Имя: Никита Бураков, Возраст: Возраст не определен, Пол: Пол не определен, Номер телефона: 79068065041'";
        assertEquals(expectedAddContactProcessCommandResult, processCommand(commandToProcess));
    }

    /**
     * Тест команды /filter-gender
     */
    @Test
    public void testProcessFilterByGenderCommand() {
        String command = "/filter-gender";

        testOnCommandNeedAuthenticate(command);
        authenticationService.authenticate(user.getUsername());

        String expectedInvalidMessage = "Необходимо ввести параметры [<пол>]";
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 0);
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 2);
        contactService.addContact(user, contactName);
        contactService.addContact(user, "Иван Иванов");
        contactService.editContact(user, "Никита Бураков", "Мужчина", Contact.Field.CONTACT_GENDER);


        String commandToProcess = String.format("/filter-gender %s", "Мужчина");
        String expectedAddContactProcessCommandResult = "Имя: Никита Бураков, Возраст: Возраст не определен, Пол: Мужчина, Номер телефона: Номер телефона не определен";
        assertEquals(expectedAddContactProcessCommandResult, processCommand(commandToProcess));
    }

    /**
     * Тест команды /filter-block
     */
    @Test
    public void testProcessFilterByBlockCommand() {
        String command = "/filter-block";

        testOnCommandNeedAuthenticate(command);
        authenticationService.authenticate(user.getUsername());
        contactService.addContact(user, contactName);
        contactService.addContact(user, "Иван Иванов");
        contactService.blockContact(user, "Никита Бураков");


        String commandToProcess = "/filter-block";
        String expectedAddContactProcessCommandResult = "Имя: Никита Бураков, Возраст: Возраст не определен, Пол: Пол не определен, Номер телефона: Номер телефона не определен";
        assertEquals(expectedAddContactProcessCommandResult, processCommand(commandToProcess));
    }

    /**
     * Тест команды /filter-non-block
     */
    @Test
    public void testProcessFilterByUnblockCommand() {
        String command = "/filter-non-block";

        testOnCommandNeedAuthenticate(command);
        authenticationService.authenticate(user.getUsername());
        contactService.addContact(user, contactName);
        contactService.addContact(user, "Иван Иванов");
        contactService.blockContact(user, "Иван Иванов");


        String commandToProcess = "/filter-non-block";
        String expectedAddContactProcessCommandResult = "Имя: Никита Бураков, Возраст: Возраст не определен, Пол: Пол не определен, Номер телефона: Номер телефона не определен";
        assertEquals(expectedAddContactProcessCommandResult, processCommand(commandToProcess));
    }

    /**
     * Тест команды /filter-age
     */
    @Test
    public void testProcessFilterByAgeCommand() {
        String command = "/filter-age";

        testOnCommandNeedAuthenticate(command);
        authenticationService.authenticate(user.getUsername());
        String expectedInvalidMessage = "Необходимо ввести параметры [<выражение>, <возраст>]";
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 0);
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 3);
        contactService.addContact(user, contactName);
        contactService.addContact(user, "Иван Иванов");
        contactService.editContact(user, contactName, "50", Contact.Field.CONTACT_AGE);
        contactService.editContact(user, "Иван Иванов", "25", Contact.Field.CONTACT_AGE);


        String commandToProcess = "/filter-age >,30";
        String expectedAddContactProcessCommandResult = "Имя: Никита Бураков, Возраст: 50, Пол: Пол не определен, Номер телефона: Номер телефона не определен";
        assertEquals(expectedAddContactProcessCommandResult, processCommand(commandToProcess));
    }

    /**
     * Тест команды /sort-name
     */
    @Test
    public void testProcessSortByNameCommand() {
        String command = "/sort-name";

        testOnCommandNeedAuthenticate(command);
        authenticationService.authenticate(user.getUsername());
        String expectedInvalidMessage = "Необходимо ввести параметры [<порядок сортировки>]";
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 0);
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 2);
        contactService.addContact(user, contactName);
        contactService.addContact(user, "Иван Иванов");


        String commandToProcess = "/sort-name asc";
        String expectedAddContactProcessCommandResult = "Имя: Иван Иванов, Возраст: Возраст не определен, Пол: Пол не определен, Номер телефона: Номер телефона не определен\n" +
                "Имя: Никита Бураков, Возраст: Возраст не определен, Пол: Пол не определен, Номер телефона: Номер телефона не определен";
        assertEquals(expectedAddContactProcessCommandResult, processCommand(commandToProcess));
    }

    /**
     * Тест команды /sort-age
     */
    @Test
    public void testProcessSortByAgeCommand() {
        String command = "/sort-age";

        testOnCommandNeedAuthenticate(command);
        authenticationService.authenticate(user.getUsername());
        String expectedInvalidMessage = "Необходимо ввести параметры [<порядок сортировки>]";
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 0);
        testCommandWithInvalidArguments(command, expectedInvalidMessage, 2);
        contactService.addContact(user, contactName);
        contactService.addContact(user, "Иван Иванов");
        contactService.editContact(user, contactName, "50", Contact.Field.CONTACT_AGE);
        contactService.editContact(user, "Иван Иванов", "25", Contact.Field.CONTACT_AGE);


        String commandToProcess = "/sort-age asc";
        String expectedAddContactProcessCommandResult = "Имя: Иван Иванов, Возраст: 25, Пол: Пол не определен, Номер телефона: Номер телефона не определен\n" +
                "Имя: Никита Бураков, Возраст: 50, Пол: Пол не определен, Номер телефона: Номер телефона не определен";
        assertEquals(expectedAddContactProcessCommandResult, processCommand(commandToProcess));
    }
}
