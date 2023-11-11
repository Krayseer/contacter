package ru.anykeyers.services;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.anykeyers.ApplicationProperties;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.repositories.UserRepository;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Set;

import static org.junit.Assert.*;
import static ru.anykeyers.utils.StreamUtils.setSystemOutputStream;
import static ru.anykeyers.utils.StreamUtils.writeDataInSystemInputStream;

public class ContactServiceTest {

    private ContactService contactService;

    private ContactRepository contactRepository;

    private AuthenticationService authenticationService;

    private UserRepository userRepository;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        File tempDbFile = temporaryFolder.newFile("tempDbFile.txt");
        contactRepository = new ContactRepository(tempDbFile.getPath());
        userRepository = new UserRepository(tempDbFile.getPath());
        authenticationService = new AuthenticationService(userRepository);
        contactService = new ContactService(contactRepository, authenticationService);
    }

    /**
     * Тест метода {@link ContactService#findContact(User, String)}
     */
    @Test
    public void findContact() {
        User user = new User("nikita", "123");
        Contact userContact = new Contact("Ivan", "Ivanov", "79068065041");
        contactRepository.save(user.getUsername(), userContact);

        String contactName = userContact.getFirstname() + " " + userContact.getLastname();
        Contact actualContact = contactService.findContact(user, contactName);
        Contact expectedContact = new Contact("Ivan", "Ivanov", "79068065041");
        assertEquals(expectedContact, actualContact);
    }

    /**
     * Тест метода {@link ContactService#addContact()}
     */
    @Test
    public void addContact() {
        String userInfo = "nikita\n123\n";
        writeDataInSystemInputStream(userInfo);
        authenticationService.authenticate();
        String contactInfo = "Ivan\nIvanov\n79068065041";
        writeDataInSystemInputStream(contactInfo);
        contactService.addContact();
        Set<Contact> actualSet = contactRepository.findContactsByUsername("nikita");
        Set<Contact> expectedSet = Set.of(new Contact("Ivan", "Ivanov", "79068065041"));
        assertEquals(expectedSet, actualSet);
    }

    /**
     * Тест метода {@link ContactService#deleteContact()}
     */
    @Test
    public void deleteContact() {
        String userInfo = "nikita\n123\n";
        writeDataInSystemInputStream(userInfo);
        authenticationService.authenticate();
        contactRepository.save("nikita", new Contact("Ivan", "Ivanov", "79068065041"));
        contactRepository.save("nikita", new Contact("Petya", "Petrov", "79068065042"));
        String deleteContact = "Ivan Ivanov\n";
        writeDataInSystemInputStream(deleteContact);
        contactService.deleteContact();

        Set<Contact> expectedSet = Set.of(new Contact("Petya", "Petrov", "79068065042"));
        Set<Contact> actualSet = contactRepository.findContactsByUsername("nikita");
        assertEquals(expectedSet, actualSet);
    }

    /**
     * Тест метода {@link ContactService#editContact()}
     */
    @Test
    public void editContact() {
        String userInfo = "nikita\n123\n";
        writeDataInSystemInputStream(userInfo);
        authenticationService.authenticate();
        contactRepository.save("nikita", new Contact("Ivan", "Ivanov", "79068065041"));
        String editContact = "Ivan Ivanov\n1\nPetya Petrov";
        writeDataInSystemInputStream(editContact);
        contactService.editContact();

        Set<Contact> expectedSet = Set.of(new Contact("Petya", "Petrov", "79068065041"));
        Set<Contact> actualSet = contactRepository.findContactsByUsername("nikita");
        assertEquals(expectedSet, actualSet);
    }
}