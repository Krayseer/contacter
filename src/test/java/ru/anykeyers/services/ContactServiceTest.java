package ru.anykeyers.services;

import org.junit.Before;
import org.junit.Test;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.file.FileContactRepository;

import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.*;

/**
 * Тесты для класса {@link ContactService}
 */
public class ContactServiceTest {

    private ContactService contactService;

    private FileContactRepository contactRepository;

    private final User user = new User("username");

    private final Contact contact = new Contact(user.getUsername(), "Ivan Ivanov");

    @Before
    public void setUp() throws Exception {
        File tempDbFile = Files.createTempFile("tempDbFile", ".txt").toFile();
        contactRepository = new FileContactRepository(tempDbFile.getPath());
        contactService = new ContactService(contactRepository);
    }

    /**
     * Тест метода {@link ContactService#addContact(User, String)}
     */
    @Test
    public void testAddContact() {
        String addContactResult = contactService.addContact(user, contact.getName());
        String expectedResult = "Контакт 'Ivan Ivanov' сохранен";
        assertEquals(expectedResult, addContactResult);

        String repeatAddContact = contactService.addContact(user, contact.getName());
        String expectedRepeatResult = String.format("Контакт '%s' уже существует", contact.getName());
        assertEquals(expectedRepeatResult, repeatAddContact);
    }

    /**
     * Тест метода {@link ContactService#editContact(User, String, Object, Contact.Field)}
     */
    @Test
    public void testEditContact() {
        contactService.addContact(user, contact.getName());
        String nonExistsContactName = "non-exists";
        String newName = "Petr Petrov";
        String newPhoneNumber = "8999999999";

        String actualNameEditResultNonExistsContact = contactService.editContact(
                user, nonExistsContactName, newName, Contact.Field.CONTACT_NAME
        );
        String expectedNameEditResultNonExistsContact = String.format("Не удалось найти контакт '%s'", nonExistsContactName);
        assertEquals(expectedNameEditResultNonExistsContact, actualNameEditResultNonExistsContact);

        String actualNameEditResult = contactService.editContact(
                user, contact.getName(), newName, Contact.Field.CONTACT_NAME
        );
        String expectedNameEditResult = "Контакт 'Ivan Ivanov' успешно изменен";
        assertTrue(contactRepository.existsByUsernameAndName(user.getUsername(), newName));
        assertEquals(expectedNameEditResult, actualNameEditResult);

        String actualPhoneEditResult = contactService.editContact(
                user, newName, newPhoneNumber, Contact.Field.CONTACT_PHONE_NUMBER
        );
        String expectedPhoneEditResult = "Контакт 'Petr Petrov' успешно изменен";
        assertNotNull(contactRepository.findByUsernameAndPhoneNumber(user.getUsername(), newPhoneNumber));
        assertEquals(expectedPhoneEditResult, actualPhoneEditResult);
    }

    /**
     * Тест метода {@link ContactService#deleteContact(User, String, Contact.Field)}
     */
    @Test
    public void testDeleteContact() {
        String phoneNumber = "7999999999";
        String nonExistsContactName = "non-exists";
        String nonExistsContactPhone = "1111111111";

        contactService.addContact(user, contact.getName());

        String resultDeleteByNotRealName = contactService.deleteContact(user, nonExistsContactName, Contact.Field.CONTACT_NAME);
        String expectedDeleteByNotRealNameResult = String.format("Не удалось найти контакт '%s'", nonExistsContactName);
        assertEquals(expectedDeleteByNotRealNameResult, resultDeleteByNotRealName);

        String resultDeleteByNotRealPhone = contactService.deleteContact(user, nonExistsContactPhone, Contact.Field.CONTACT_PHONE_NUMBER);
        String expectedDeleteByNotRealPhoneResult = String.format("Не удалось найти контакт '%s'", nonExistsContactPhone);
        assertEquals(expectedDeleteByNotRealPhoneResult, resultDeleteByNotRealPhone);

        String resultDeleteByName = contactService.deleteContact(user, contact.getName(), Contact.Field.CONTACT_NAME);
        String expectedDeleteByNameResult = String.format("Контакт '%s' успешно удален", contact.getName());
        assertEquals(expectedDeleteByNameResult, resultDeleteByName);

        contactService.addContact(user, contact.getName());
        contactService.editContact(user, contact.getName(), phoneNumber, Contact.Field.CONTACT_PHONE_NUMBER);

        String resultDeleteByPhone = contactService.deleteContact(user, phoneNumber, Contact.Field.CONTACT_PHONE_NUMBER);
        String expectedDeleteByPhoneResult = String.format("Контакт '%s' успешно удален", contact.getName());
        assertEquals(expectedDeleteByPhoneResult, resultDeleteByPhone);
    }

}