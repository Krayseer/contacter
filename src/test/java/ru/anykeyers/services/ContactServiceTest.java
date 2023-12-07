package ru.anykeyers.services;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.User;
import ru.anykeyers.processors.states.domain.State;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.services.impl.ContactServiceImpl;

/**
 * Тесты для сервиса {@link ContactService}
 */
@RunWith(MockitoJUnitRunner.class)
public class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactServiceImpl contactService;

    /**
     * Проверка существования контакта у пользователя
     */
    @Test
    public void existsContactTest() {
        // Подготовка
        User user = new User("testUser");
        String contactName = "existingContact";

        // Действие
        Mockito.when(contactRepository.existsByUsernameAndName(user.getUsername(), contactName)).thenReturn(true);
        boolean result = contactService.existsContact(user, contactName);

        // Проверка
        Assert.assertTrue(result);
        Mockito.verify(contactRepository, Mockito.times(1)).existsByUsernameAndName(user.getUsername(), contactName);
    }

    /**
     * Тестирование добавления контакта пользователю
     */
    @Test
    public void addContactTest() {
        // Подготовка
        User user = new User("testUser");
        String contactName = "newContact";

        // Действие
        Mockito.when(contactRepository.existsByUsernameAndName(user.getUsername(), contactName)).thenReturn(false);
        String result = contactService.addContact(user, contactName);

        // Проверка
        Assert.assertEquals("Контакт 'newContact' сохранен", result);
        Mockito.verify(contactRepository, Mockito.times(1)).saveOrUpdate(Mockito.any(Contact.class));
    }

    /**
     * Тестрирование редактирования контакта пользователя<br/>
     * <ol>
     *     <li>Изменение имени контакта</li>
     *     <li>Изменение номера телефона пользователя</li>
     * </ol>
     */
    @Test
    public void editContactTest() {
        // Подготовка
        User user = new User("testUser");
        Contact contact = new Contact(user.getUsername(), "name");

        // Действие: изменение имени контакта
        user.setState(State.EDIT_CONTACT_NAME);
        String newValue = "newName";
        user.setEditInfo(contact.getName());
        Mockito.when(contactRepository.findByUsernameAndName(user.getUsername(), user.getEditInfo())).thenReturn(contact);
        String editNameResult = contactService.editContact(user, newValue);

        // Проверка: изменение имени контакта
        Assert.assertEquals("Контакт 'name' успешно изменен", editNameResult);
        Assert.assertEquals(newValue, contact.getName());
        Mockito.verify(contactRepository, Mockito.times(1)).saveOrUpdate(contact);

        // Действие: изменение номера телефона контакта
        user.setState(State.EDIT_CONTACT_PHONE);
        String newPhoneNumber = "123123123";
        user.setEditInfo(contact.getName());
        Mockito.when(contactRepository.findByUsernameAndName(user.getUsername(), user.getEditInfo())).thenReturn(contact);
        String editPhoneNumberResult = contactService.editContact(user, newPhoneNumber);

        // Проверка: изменение номера телефона контакта
        Assert.assertEquals("Контакт 'newName' успешно изменен", editPhoneNumberResult);
        Assert.assertEquals(newPhoneNumber, contact.getPhoneNumber());
        Mockito.verify(contactRepository, Mockito.times(2)).saveOrUpdate(contact);
    }

    /**
     * Тестирование удаления контакта
     */
    @Test
    public void deleteContactTest() {
        // Подготовка
        User user = new User("testUser");
        String contactName = "contactToDelete";
        Contact contactToDelete = new Contact(user.getUsername(), contactName);

        // Действие
        Mockito.when(contactRepository.findByUsernameAndName(user.getUsername(), contactName)).thenReturn(contactToDelete);
        String result = contactService.deleteContact(user, contactName);

        // Проверка
        Assert.assertEquals("Контакт 'contactToDelete' успешно удален", result);
        Mockito.verify(contactRepository, Mockito.times(1)).delete(contactToDelete);
    }

}