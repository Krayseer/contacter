package ru.anykeyers.repositories.file;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.ContactRepository;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Тесты для класса {@link ContactRepository}
 */
public class ContactRepositoryTest {

    private ContactRepository contactRepository;

    private File tempDbFile;

    private final User user = new User("user");

    private final Contact firstUserContact = new Contact(user.getUsername(), "1", "Ivan Ivanov", "79068065041");

    private final Contact secondUserContact = new Contact(user.getUsername(), "2", "Petya Petrov", "79068065042");

    @Before
    public void setUp() throws Exception {
        tempDbFile = Files.createTempFile("tempDbFile", ".txt").toFile();
        contactRepository = new FileContactRepository(tempDbFile.getPath());
    }

    /**
     * Тест метода {@link ContactRepository#existsByUsernameAndName(String, String)}
     */
    @Test
    public void testExistsByUsernameAndName() {
        saveTempContacts();

        boolean isFirstUserExistsInDb = contactRepository.existsByUsernameAndName(user.getUsername(), "Ivan Ivanov");
        boolean isNotRealUserExistsInDb = contactRepository.existsByUsernameAndName(user.getUsername(), "Empty User");

        assertTrue(isFirstUserExistsInDb);
        assertFalse(isNotRealUserExistsInDb);
    }

    /**
     * Тест метода {@link ContactRepository#findByUsername(String)} ()}
     */
    @Test
    public void testFindByUsername() {
        saveTempContacts();

        Set<Contact> actualContacts = contactRepository.findByUsername(user.getUsername());

        Set<Contact> expectedContacts = Set.of(firstUserContact, secondUserContact);
        assertEquals(2, actualContacts.size());
        assertEquals(expectedContacts, actualContacts);
    }

    /**
     * Тест метода {@link ContactRepository#findByUsernameAndName(String, String)}}
     */
    @Test
    public void testFindContactByUsernameAndName() {
        saveTempContacts();

        Contact actualContact = contactRepository.findByUsernameAndName(user.getUsername(), "Ivan Ivanov");

        assertEquals(firstUserContact, actualContact);
    }

    /**
     * Тест метода {@link ContactRepository#findByUsernameAndPhoneNumber(String, String)}
     */
    @Test
    public void testFindByUsernameAndPhoneNumber() {
        saveTempContacts();

        Contact actualContact = contactRepository.findByUsernameAndPhoneNumber(user.getUsername(), firstUserContact.getPhoneNumber());

        assertEquals(firstUserContact, actualContact);
    }

    /**
     * Тест метода {@link ContactRepository#saveOrUpdate(Contact)}
     */
    @Test
    public void testSaveOrUpdateContact() throws IOException {
        contactRepository.saveOrUpdate(firstUserContact);
        Set<Contact> actualContacts = contactRepository.findByUsername(user.getUsername());

        Set<Contact> expectedContacts = Set.of(firstUserContact);
        assertEquals(1, actualContacts.size());
        assertEquals(expectedContacts, actualContacts);

        List<String> lines = FileUtils.readLines(tempDbFile, "UTF-8");
        assertEquals(List.of("user:1,Ivan Ivanov,79068065041"), lines);
    }

    /**
     * Тест метода {@link ContactRepository#delete(Contact)}
     */
    @Test
    public void testDeleteContact() {
        saveTempContacts();

        contactRepository.delete(firstUserContact);
        Set<Contact> actualContacts = contactRepository.findByUsername(user.getUsername());

        Set<Contact> expectedSet = Set.of(secondUserContact);
        assertEquals(1, actualContacts.size());
        assertEquals(actualContacts, expectedSet);
    }

    /**
     * Сохранить временные контакты в базу данных
     */
    private void saveTempContacts() {
        contactRepository.saveOrUpdate(firstUserContact);
        contactRepository.saveOrUpdate(secondUserContact);
    }

}
