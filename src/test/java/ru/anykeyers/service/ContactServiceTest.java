package ru.anykeyers.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.anykeyers.domain.Gender;
import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.exception.BadArgumentException;
import ru.anykeyers.exception.InvalidNumberFormat;
import ru.anykeyers.exception.contact.ContactAlreadyExistsException;
import ru.anykeyers.exception.contact.ContactNotExistsException;
import ru.anykeyers.processor.state.domain.State;
import ru.anykeyers.repository.ContactRepository;
import ru.anykeyers.service.impl.ContactServiceImpl;

import java.util.Optional;

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
        String nonExistsContactName = "non-exists";

        // Действие: проверка несуществующего пользователя
        Mockito.when(contactRepository.existsByUsernameAndName(user.getUsername(), nonExistsContactName))
                .thenReturn(false);
        boolean nonExistsResult = contactService.existsContact(user, nonExistsContactName);

        // Проверка
        Assert.assertFalse(nonExistsResult);
        Mockito.verify(contactRepository, Mockito.times(1))
                .existsByUsernameAndName(user.getUsername(), nonExistsContactName);

        // Действие: проверка существующего пользователя
        Mockito.when(contactRepository.existsByUsernameAndName(user.getUsername(), contactName))
                .thenReturn(true);
        boolean existsResult = contactService.existsContact(user, contactName);

        // Проверка
        Assert.assertTrue(existsResult);
        Mockito.verify(contactRepository, Mockito.times(1))
                .existsByUsernameAndName(user.getUsername(), contactName);
    }

    /**
     * Тестирование добавления контакта пользователю
     * <ol>
     *     <li>Добавление контакта уже с существующим именем</li>
     *     <li>Добавление нового контакта</li>
     * </ol>
     */
    @Test
    public void addContactTest() {
        // Подготовка
        User user = new User("testUser");
        String contactName = "newContact";

        // Действие: добавление контакта уже с существующим именем
        Mockito.when(contactRepository.existsByUsernameAndName(user.getUsername(), contactName)).thenReturn(true);

        // Проверка
        Exception addContactException = Assert.assertThrows(
                ContactAlreadyExistsException.class, () -> contactService.addContact(user, contactName)
        );
        Assert.assertEquals("Контакт 'newContact' уже существует", addContactException.getMessage());
        Mockito.verify(contactRepository, Mockito.never()).saveOrUpdate(Mockito.any(Contact.class));

        // Действие: добавление контакта с новым именем
        Mockito.when(contactRepository.existsByUsernameAndName(user.getUsername(), contactName)).thenReturn(false);
        contactService.addContact(user, contactName);

        // Проверка
        Mockito.verify(contactRepository, Mockito.times(1)).saveOrUpdate(Mockito.any(Contact.class));
    }

    /**
     * Тестирование редактирования контакта пользователя
     * <ol>
     *     <li>Изменение имени</li>
     *     <li>
     *         Изменение номера телефона
     *         <ul>
     *             <li>Ввод некорректного номера телефона</li>
     *             <li>Ввод корректного номера телефона</li>
     *         </ul>
     *     </li>
     *     <li>
     *         Изменение возраста
     *         <ul>
     *             <li>Ввод некорректного возраста</li>
     *             <li>Ввод корректного возраста</li>
     *         </ul>
     *     </li>
     *     <li>
     *         Изменение пола
     *         <ul>
     *             <li>Выбор некорректного пункта изменения пола</li>
     *             <li>Выбор корректного пункта изменения пола</li>
     *         </ul>
     *     </li>
     *     <li>
     *         Изменение блокировки
     *         <ul>
     *             <li>Выбор некорректного пункта изменения блокировки</li>
     *             <li>Выбор корректного пункта изменения блокировки</li>
     *         </ul>
     *     </li>
     * </ol>
     */
    @Test
    public void editContactTest() {
        // Подготовка
        User user = new User("testUser");
        StateInfo userStateInfo = new StateInfo();
        String contactName = "testContact";
        String nonExistsContactName = "nonExists";
        Contact contact = new Contact(user.getUsername(), contactName);

        // Действие: изменение несуществующего контакта
        Mockito.lenient().when(contactRepository.getByUsernameAndName(user.getUsername(), nonExistsContactName))
                .thenReturn(Optional.empty());

        // Проверка
        userStateInfo.setEditInfo(nonExistsContactName);
        Exception nonExistsContactException = Assert.assertThrows(
                ContactNotExistsException.class, () -> contactService.editContact(user, userStateInfo, nonExistsContactName)
        );
        Assert.assertEquals("Не удалось найти контакт 'nonExists'", nonExistsContactException.getMessage());
        Mockito.verify(contactRepository, Mockito.never()).saveOrUpdate(contact);

        // Действие: изменение имени контакта
        userStateInfo.setState(State.EDIT_CONTACT_NAME);
        String newValue = "newName";
        userStateInfo.setEditInfo(contact.getName());
        Mockito.when(contactRepository.getByUsernameAndName(user.getUsername(), userStateInfo.getEditInfo()))
                .thenReturn(Optional.of(contact));
        contactService.editContact(user, userStateInfo, newValue);

        // Проверка
        Assert.assertEquals(newValue, contact.getName());
        Mockito.verify(contactRepository, Mockito.times(1)).saveOrUpdate(contact);

        // Действие: изменение номера телефона контакта
        userStateInfo.setState(State.EDIT_CONTACT_PHONE);
        String invalidPhoneNumber = "invalid";
        String newPhoneNumber = "79797979";
        userStateInfo.setEditInfo(contact.getName());
        Mockito.when(contactRepository.getByUsernameAndName(user.getUsername(), userStateInfo.getEditInfo()))
                .thenReturn(Optional.of(contact));

        // Проверка: ввод некорректных символов
        Exception invalidFormatExceptionName = Assert.assertThrows(
                InvalidNumberFormat.class, () -> contactService.editContact(user, userStateInfo, invalidPhoneNumber)
        );
        Assert.assertEquals("Нужно ввести числовое значение", invalidFormatExceptionName.getMessage());
        Mockito.verify(contactRepository, Mockito.times(1)).saveOrUpdate(contact);

        // Проверка: ввод корректных символов
        contactService.editContact(user, userStateInfo, newPhoneNumber);
        Mockito.verify(contactRepository, Mockito.times(2)).saveOrUpdate(contact);

        // Действие: изменение возраста контакта
        userStateInfo.setState(State.EDIT_CONTACT_AGE);
        String invalidAge = "invalid";
        String age = "79797979";
        userStateInfo.setEditInfo(contact.getName());
        Mockito.when(contactRepository.getByUsernameAndName(user.getUsername(), userStateInfo.getEditInfo()))
                .thenReturn(Optional.of(contact));

        // Проверка: ввод некорректных символов
        Exception invalidFormatExceptionPhone = Assert.assertThrows(
                InvalidNumberFormat.class, () -> contactService.editContact(user, userStateInfo, invalidAge)
        );
        Assert.assertEquals("Нужно ввести числовое значение", invalidFormatExceptionPhone.getMessage());
        Mockito.verify(contactRepository, Mockito.times(2)).saveOrUpdate(contact);

        // Проверка: ввод корректных символов
        contactService.editContact(user, userStateInfo, age);
        Mockito.verify(contactRepository, Mockito.times(3)).saveOrUpdate(contact);

        // Действие: изменение пола контакта
        userStateInfo.setState(State.EDIT_CONTACT_GENDER);
        String invalidGenderKind = "1234";
        String manGenderKind = "1";
        userStateInfo.setEditInfo(contact.getName());
        Mockito.when(contactRepository.getByUsernameAndName(user.getUsername(), userStateInfo.getEditInfo()))
                .thenReturn(Optional.of(contact));

        // Проверка: ввод некорректного аргумента
        Exception genderEditException = Assert.assertThrows(
                BadArgumentException.class, () -> contactService.editContact(user, userStateInfo, invalidGenderKind)
        );
        Assert.assertEquals("Введен неверный параметр", genderEditException.getMessage());
        Mockito.verify(contactRepository, Mockito.times(3)).saveOrUpdate(contact);

        // Проверка: ввод корректного аргумента
        contactService.editContact(user, userStateInfo, manGenderKind);
        Assert.assertEquals(Gender.MAN, contact.getGender());
        Mockito.verify(contactRepository, Mockito.times(4)).saveOrUpdate(contact);

        // Действие: изменение блокировки контакта
        userStateInfo.setState(State.EDIT_CONTACT_BLOCK);
        String invalidBlockKind = "1234";
        String blockKind = "1";
        userStateInfo.setEditInfo(contact.getName());
        Mockito.when(contactRepository.getByUsernameAndName(user.getUsername(), userStateInfo.getEditInfo()))
                .thenReturn(Optional.of(contact));

        // Проверка: ввод некорректного аргумента
        Exception blockEditException= Assert.assertThrows(
                BadArgumentException.class, () -> contactService.editContact(user, userStateInfo, invalidBlockKind)
        );
        Assert.assertEquals("Введен неверный параметр", blockEditException.getMessage());
        Mockito.verify(contactRepository, Mockito.times(4)).saveOrUpdate(contact);

        // Проверка: ввод корректного аргумента
        contactService.editContact(user, userStateInfo, blockKind);
        Assert.assertTrue(contact.isBlocked());
        Mockito.verify(contactRepository, Mockito.times(5)).saveOrUpdate(contact);
    }

    /**
     * Тестирование удаления контакта
     * <ol>
     *     <li>Удаление несуществующего контакта</li>
     *     <li>Удаление существующего контакта</li>
     * </ol>
     */
    @Test
    public void deleteContactTest() {
        // Подготовка
        User user = new User("testUser");
        String contactName = "contactToDelete";
        String nonExistsContactName = "nonExists";
        Contact contact = new Contact(user.getUsername(), contactName);

        // Действие: удаление несуществующего контакта
        Mockito.when(contactRepository.getByUsernameAndName(user.getUsername(), nonExistsContactName))
                .thenReturn(Optional.empty());

        // Проверка
        Exception nonExistsContactException = Assert.assertThrows(
                ContactNotExistsException.class, () -> contactService.deleteContact(user, nonExistsContactName)
        );
        Assert.assertEquals("Не удалось найти контакт 'nonExists'", nonExistsContactException.getMessage());
        Mockito.verify(contactRepository, Mockito.never()).delete(contact);

        // Действие: удаление существующего контакта
        Mockito.when(contactRepository.getByUsernameAndName(user.getUsername(), contactName))
                .thenReturn(Optional.of(contact));
        contactService.deleteContact(user, contactName);

        // Проверка
        Mockito.verify(contactRepository, Mockito.times(1)).delete(contact);
    }

}