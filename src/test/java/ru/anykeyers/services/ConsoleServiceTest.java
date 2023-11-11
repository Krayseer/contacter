package ru.anykeyers.services;

import org.junit.Before;
import org.junit.Test;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.Group;
import ru.anykeyers.domain.User;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

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


    /**
     * Тест метода {@link ConsoleService#readContactFromConsole(Scanner)}
     */
    @Test
    public void readContactFromConsole() {
        String contactInfo = "Ivan\nIvanov\n79068065041\n";
        writeDataInSystemInputStream(contactInfo);
        Scanner scanner = new Scanner(System.in);

        Contact contact = consoleService.readContactFromConsole(scanner);
        Contact expectedContact = new Contact("Ivan", "Ivanov", "79068065041");
        assertEquals(expectedContact, contact);
    }

    /**
     * Тест метода {@link ConsoleService#readGroupFromConsole(Scanner)}
     */
    @Test
    public void readGroupFromConsole() {
        String groupInfo = "Work\n";
        writeDataInSystemInputStream(groupInfo);
        Scanner scanner = new Scanner(System.in);

        Group group = consoleService.readGroupFromConsole(scanner);
        Group expectedGroup = new Group("Work", new ArrayList<>());

        assertEquals(expectedGroup, group);
    }

    /**
     * Тест метода {@link ConsoleService#deleteContactFromConsole(Scanner)}
     */
    @Test
    public void deleteContactFromConsole() {
        String deleteContactInfo = "Ivan Ivanov\n";
        writeDataInSystemInputStream(deleteContactInfo);
        Scanner scanner = new Scanner(System.in);

        String deletedContactString = consoleService.deleteContactFromConsole(scanner);
        String expectedString = "Ivan Ivanov";
        assertEquals(expectedString, deletedContactString);
    }

    /**
     * Тест метода {@link ConsoleService#deleteGroupFromConsole(Scanner)}
     */
    @Test
    public void deleteGroupFromConsole() {
        String deleteGroupInfo = "Work\n";
        writeDataInSystemInputStream(deleteGroupInfo);
        Scanner scanner = new Scanner(System.in);

        String deletedContactString = consoleService.deleteGroupFromConsole(scanner);
        String expectedString = "Work";
        assertEquals(expectedString, deletedContactString);
    }

    /**
     * Тест метода {@link ConsoleService#editContactFromConsole(Scanner)}
     */
    @Test
    public void editContactFromConsole() {
        String editContactInfo = "Ivan Ivanov\n";
        writeDataInSystemInputStream(editContactInfo);
        Scanner scanner = new Scanner(System.in);

        String deletedContactString = consoleService.deleteGroupFromConsole(scanner);
        String expectedString = "Ivan Ivanov";
        assertEquals(expectedString, deletedContactString);
    }

    /**
     * Тест метода {@link ConsoleService#editGroupFromConsole(Scanner)}
     */
    @Test
    public void editGroupFromConsole() {
        String editGroupInfo = "Work\n";
        writeDataInSystemInputStream(editGroupInfo);
        Scanner scanner = new Scanner(System.in);

        String deletedContactString = consoleService.deleteGroupFromConsole(scanner);
        String expectedString = "Work";
        assertEquals(expectedString, deletedContactString);
    }

    /**
     * Тест метода {@link ConsoleService#editInfo(Scanner)}
     */
    @Test
    public void editInfo() {
        String choice = "1";
        writeDataInSystemInputStream(choice);
        Scanner scanner = new Scanner(System.in);

        int actualChoice = consoleService.editInfo(scanner);
        int expectedChoice = 1;
        assertEquals(expectedChoice, actualChoice);
    }

    /**
     * Тест метода {@link ConsoleService#editGroupInfo(Scanner)}
     */
    @Test
    public void editGroupInfo() {
        String choice = "1";
        writeDataInSystemInputStream(choice);
        Scanner scanner = new Scanner(System.in);

        int actualChoice = consoleService.editGroupInfo(scanner);
        int expectedChoice = 1;
        assertEquals(expectedChoice, actualChoice);
    }

    /**
     * Тест метода {@link ConsoleService#editName(Scanner)}
     */
    @Test
    public void editName() {
        String name = "Ivan Ivanov\n";
        writeDataInSystemInputStream(name);
        Scanner scanner = new Scanner(System.in);

        String actualName = consoleService.editName(scanner);
        String expectedName = "Ivan Ivanov";
        assertEquals(expectedName, actualName);
    }
    /**
     * Тест метода {@link ConsoleService#editPhoneNumber(Scanner)}
     */
    @Test
    public void editPhoneNumber() {
        String phoneNumber = "Ivan Ivanov\n";
        writeDataInSystemInputStream(phoneNumber);
        Scanner scanner = new Scanner(System.in);

        String actualPhoneNumber = consoleService.editPhoneNumber(scanner);
        String expectedPhoneNumber = "Ivan Ivanov";
        assertEquals(expectedPhoneNumber, actualPhoneNumber);
    }

    /**
     * Тест метода {@link ConsoleService#editGroupName(Scanner)}
     */
    @Test
    public void editGroupName() {
        String groupName = "Work\n";
        writeDataInSystemInputStream(groupName);
        Scanner scanner = new Scanner(System.in);

        String actualGroupName = consoleService.editGroupName(scanner);
        String expectedGroupName = "Work";
        assertEquals(expectedGroupName, actualGroupName);
    }

    /**
     * Тест метода {@link ConsoleService#addContactInGroup(Scanner)}
     */
    @Test
    public void addContactInGroup() {
        String contactName = "Ivan Ivanov\n";
        writeDataInSystemInputStream(contactName);
        Scanner scanner = new Scanner(System.in);

        String actualContactName = consoleService.addContactInGroup(scanner);
        String expectedContactName = "Ivan Ivanov";
        assertEquals(expectedContactName, actualContactName);
    }

    /**
     * Тест метода {@link ConsoleService#deleteContactFromGroup(Scanner)}
     */
    @Test
    public void deleteContactFromGroup() {
        String contactNameToDelete = "Ivan Ivanov\n";
        writeDataInSystemInputStream(contactNameToDelete);
        Scanner scanner = new Scanner(System.in);

        String actualContactNameToDelete = consoleService.deleteContactFromGroup(scanner);
        String expectedContactNameToDelete = "Ivan Ivanov";
        assertEquals(expectedContactNameToDelete, actualContactNameToDelete);
    }

}
