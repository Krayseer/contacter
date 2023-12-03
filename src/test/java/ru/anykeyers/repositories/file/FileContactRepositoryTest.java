package ru.anykeyers.repositories.file;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.ContactRepository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;

/**
 * Тесты для класса {@link FileContactRepository}
 */
public class FileContactRepositoryTest {

    private ContactRepository contactRepository;

    private File tempDbFile;

    private User user;

    @Before
    public void setUp() throws Exception {
        tempDbFile = Files.createTempFile("tempDbFile", ".txt").toFile();
        contactRepository = new FileContactRepository(tempDbFile.getPath());
        user = new User("user");
    }

    /**
     * Тест метода {@link ContactRepository#existsByUsernameAndName(String, String)}
     */
    @Test
    public void existsByUsernameAndNameTest() {
        // Подготовка
        Contact firstUserContact = new Contact(user.getUsername(), "Ivan Ivanov");
        Contact secondUserContact = new Contact(user.getUsername(), "Petya Petrov");
        contactRepository.saveOrUpdate(firstUserContact);
        contactRepository.saveOrUpdate(secondUserContact);

        // Действие
        boolean isFirstUserExistsInDb = contactRepository.existsByUsernameAndName(user.getUsername(), "Ivan Ivanov");
        boolean isNotRealUserExistsInDb = contactRepository.existsByUsernameAndName(user.getUsername(), "Empty User");

        // Проверка
        Assert.assertTrue(isFirstUserExistsInDb);
        Assert.assertFalse(isNotRealUserExistsInDb);
    }

    /**
     * Тест метода {@link ContactRepository#findByUsername(String)}
     */
    @Test
    public void findByUsernameTest() {
        // Подготовка
        Contact firstUserContact = new Contact(user.getUsername(), "Ivan Ivanov");
        Contact secondUserContact = new Contact(user.getUsername(), "Petya Petrov");
        contactRepository.saveOrUpdate(firstUserContact);
        contactRepository.saveOrUpdate(secondUserContact);

        // Действие
        Set<Contact> actualContacts = contactRepository.findByUsername(user.getUsername());

        // Проверка
        Set<Contact> expectedContacts = Set.of(firstUserContact, secondUserContact);
        Assert.assertEquals(2, actualContacts.size());
        Assert.assertEquals(expectedContacts, actualContacts);
    }

    /**
     * Тест метода {@link ContactRepository#findByUsernameAndName(String, String)}
     */
    @Test
    public void findContactByUsernameAndNameTest() {
        // Подготовка
        Contact contact = new Contact(user.getUsername(), "Ivan Ivanov");
        contactRepository.saveOrUpdate(contact);

        // Действие
        Contact actualContact = contactRepository.findByUsernameAndName(user.getUsername(), "Ivan Ivanov");

        // Проверка
        Assert.assertEquals(contact, actualContact);
    }

    /**
     * Тест метода {@link ContactRepository#findByUsernameAndPhoneNumber(String, String)}
     */
    @Test
    public void findByUsernameAndPhoneNumberTest() {
        // Подготовка
        Contact contact = new Contact(user.getUsername(), "Ivan Ivanov");
        contact.setPhoneNumber("+77777777");
        contactRepository.saveOrUpdate(contact);

        // Действие
        Contact actualContact = contactRepository.findByUsernameAndPhoneNumber(
                user.getUsername(), contact.getPhoneNumber()
        );

        // Проверка
        Assert.assertEquals(contact, actualContact);
    }

    /**
     * Тест метода {@link ContactRepository#saveOrUpdate(Contact)}
     */
    @Test
    public void saveOrUpdateContactTest() throws IOException {
        // Подготовка
        Contact contact = new Contact(user.getUsername(), "Ivan Ivanov");

        // Действие
        contactRepository.saveOrUpdate(contact);

        // Проверка
        List<String> actualFileLines = FileUtils.readLines(tempDbFile, StandardCharsets.UTF_8);
        String expectedFileLines = String.format("user:id=%s;name=Ivan Ivanov;phone_number=null", contact.getId());
        Assert.assertEquals(List.of(expectedFileLines), actualFileLines);
    }

    /**
     * Тест метода {@link ContactRepository#delete(Contact)}
     */
    @Test
    public void deleteContactTest() {
        // Подготовка
        Contact firstUserContact = new Contact(user.getUsername(), "Ivan Ivanov");
        Contact secondUserContact = new Contact(user.getUsername(), "Petya Petrov");
        contactRepository.saveOrUpdate(firstUserContact);
        contactRepository.saveOrUpdate(secondUserContact);

        // Действие
        contactRepository.delete(firstUserContact);
        Set<Contact> actualContacts = contactRepository.findByUsername(user.getUsername());

        // Проверка
        Set<Contact> expectedSet = Set.of(secondUserContact);
        Assert.assertEquals(1, actualContacts.size());
        Assert.assertEquals(actualContacts, expectedSet);
    }

}
