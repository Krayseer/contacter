package ru.anykeyers.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.User;
import ru.anykeyers.processors.states.domain.State;
import ru.anykeyers.repositories.ContactRepository;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Тесты для класса {@link ContactService}
 */
@RunWith(MockitoJUnitRunner.class)
public class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactService contactService;

    /**
     * Проверка существования контакта у пользователя
     */
    @Test
    public void testExistsContact() {
        User user = new User("testUser");
        String contactName = "existingContact";
        when(contactRepository.existsByUsernameAndName(user.getUsername(), contactName)).thenReturn(true);

        boolean result = contactService.existsContact(user, contactName);

        assertTrue(result);
        verify(contactRepository, times(1)).existsByUsernameAndName(user.getUsername(), contactName);
    }

    /**
     * Проверка получения всех контактов пользователя
     */
    @Test
    public void testGetAllContacts() {
        User user = new User("testUser");
        when(contactRepository.findByUsername(user.getUsername())).thenReturn(Set.of(new Contact(user.getUsername(), "contact1")));

        Set<Contact> result = contactService.getAllContacts(user);

        assertEquals(1, result.size());
        verify(contactRepository, times(1)).findByUsername(user.getUsername());
    }

    /**
     * Тестирование добавления контакта пользователю
     */
    @Test
    public void testAddContact() {
        User user = new User("testUser");
        String contactName = "newContact";
        when(contactRepository.existsByUsernameAndName(user.getUsername(), contactName)).thenReturn(false);

        String result = contactService.addContact(user, contactName);

        assertEquals("Контакт 'newContact' сохранен", result);
        verify(contactRepository, times(1)).saveOrUpdate(any(Contact.class));
    }

    /**
     * Тестрирование редактирования контакта пользователя<br/>
     * <ul>
     *     <li>Изменение имени контакта</li>
     *     <li>Изменение номера телефона пользователя</li>
     * </ul>
     */
    @Test
    public void testEditContact() {
        User user = new User("testUser");
        Contact contact = new Contact(user.getUsername(), "name");

        user.setState(State.EDIT_CONTACT_NAME);
        String newValue = "newName";
        user.setContactNameToEdit(contact.getName());
        when(contactRepository.findByUsernameAndName(user.getUsername(), user.getContactNameToEdit())).thenReturn(contact);
        String editNameResult = contactService.editContact(user, newValue);
        assertEquals("Контакт 'name' успешно изменен", editNameResult);
        assertEquals(newValue, contact.getName());
        verify(contactRepository, times(1)).saveOrUpdate(contact);

        user.setState(State.EDIT_CONTACT_PHONE);
        String newPhoneNumber = "123123123";
        user.setContactNameToEdit(contact.getName());
        when(contactRepository.findByUsernameAndName(user.getUsername(), user.getContactNameToEdit())).thenReturn(contact);
        String editPhoneNumberResult = contactService.editContact(user, newPhoneNumber);
        assertEquals("Контакт 'newName' успешно изменен", editPhoneNumberResult);
        assertEquals(newPhoneNumber, contact.getPhoneNumber());
        verify(contactRepository, times(2)).saveOrUpdate(contact);

    }

    /**
     * Тестирование удаления контакта
     */
    @Test
    public void testDeleteContact() {
        User user = new User("testUser");
        String contactName = "contactToDelete";
        Contact contactToDelete = new Contact(user.getUsername(), contactName);
        when(contactRepository.findByUsernameAndName(user.getUsername(), contactName)).thenReturn(contactToDelete);

        String result = contactService.deleteContact(user, contactName);

        assertEquals("Контакт 'contactToDelete' успешно удален", result);
        verify(contactRepository, times(1)).delete(contactToDelete);
    }

}