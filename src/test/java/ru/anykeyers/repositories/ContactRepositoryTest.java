//package ru.anykeyers.repositories;
//
//import org.apache.commons.io.FileUtils;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.rules.TemporaryFolder;
//import ru.anykeyers.ApplicationProperties;
//import ru.anykeyers.domain.Contact;
//import ru.anykeyers.domain.User;
//
//import java.io.*;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import static org.junit.Assert.*;
//
//public class ContactRepositoryTest {
//
//    private ContactRepository contactRepository;
//
//    @Rule
//    public TemporaryFolder temporaryFolder = new TemporaryFolder();
//
//    @Before
//    public void setUp() throws Exception {
//        File tempDbFile = temporaryFolder.newFile("tempDbFile.txt");
//        contactRepository = new ContactRepository(tempDbFile.getPath());
//    }
//
//    /**
//     * Тест метода {@link ContactRepository#saveAll()}
//     */
//    @Test
//    public void saveContactsTest() throws IOException {
//        User user = new User("nikita", "123");
//        Contact firstUserContact = new Contact("Ivan", "Ivanov", "79068065041");
//        contactRepository.save(user.getUsername(), firstUserContact);
//        contactRepository.saveAll();
//
//        File dbFile = new File(contactRepository.getContactFilePath());
//        assertTrue(dbFile.exists());
//        List<String> lines = FileUtils.readLines(dbFile, "UTF-8");
//
//        assertTrue(lines.contains("nikita: Ivan Ivanov 79068065041"));
//    }
//
//    /**
//     * Тест метода {@link ContactRepository#findContactsByUsername(String)} ()}
//     */
//    @Test
//    public void findContactsByUsername() {
//        User user = new User("nikita", "123");
//        Contact firstUserContact = new Contact("Ivan", "Ivanov", "79068065041");
//        contactRepository.save(user.getUsername(), firstUserContact);
//        Contact secondUserContact = new Contact("Petya", "Petrov", "79068065042");
//        contactRepository.save(user.getUsername(), secondUserContact);
//        Set<Contact> expectedSet = Set.of(new Contact("Ivan", "Ivanov", "79068065041"),
//                new Contact("Petya", "Petrov", "79068065042"));
//        Set<Contact> actualUserContacts = contactRepository.findContactsByUsername(user.getUsername());
//        assertEquals(expectedSet, actualUserContacts);
//    }
//
//    /**
//     * Тест метода {@link ContactRepository#findContactByNameOrNumber(User, String)}
//     */
//    @Test
//    public void findContactByNameOrNumber() {
//        User user = new User("nikita", "123");
//        Contact userContact = new Contact("Ivan", "Ivanov", "79068065041");
//        contactRepository.save(user.getUsername(), userContact);
//        Contact actualContactByName = contactRepository.findContactByNameOrNumber(user, "Ivan Ivanov");
//        Contact actualContactByNumber = contactRepository.findContactByNameOrNumber(user, "79068065041");
//        Contact expectedContact = new Contact("Ivan", "Ivanov", "79068065041");
//        assertEquals(actualContactByName, expectedContact);
//        assertEquals(actualContactByNumber, expectedContact);
//    }
//
//    /**
//     * Тест метода {@link ContactRepository#removeContact(User, Contact)}
//     */
//    @Test
//    public void removeContact() {
//        User user = new User("nikita", "123");
//        Contact userContact = new Contact("Ivan", "Ivanov", "79068065041");
//        contactRepository.save(user.getUsername(), userContact);
//        contactRepository.removeContact(user, userContact);
//        Set<Contact> actualContacts = contactRepository.findContactsByUsername(user.getUsername());
//        Set<Contact> expectedSet = new HashSet<>();
//        assertEquals(0, actualContacts.size());
//        assertEquals(actualContacts, expectedSet);
//    }
//
//    /**
//     * Тест метода {@link ContactRepository#save(String, Contact)}
//     */
//    @Test
//    public void save() {
//        String userName = "nikita";
//        Contact userContact = new Contact("Ivan", "Ivanov", "79068065041");
//        contactRepository.save(userName, userContact);
//        Set<Contact> actualContacts = contactRepository.findContactsByUsername(userName);
//        Set<Contact> expectedSet = Set.of(new Contact("Ivan", "Ivanov", "79068065041"));
//        assertEquals(expectedSet, actualContacts);
//        assertEquals(1, actualContacts.size());
//    }
//}