package ru.anykeyers.processors.states.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.anykeyers.domain.User;
import ru.anykeyers.processors.states.domain.State;
import ru.anykeyers.processors.states.domain.StateType;
import ru.anykeyers.services.impl.AuthenticationServiceImpl;
import ru.anykeyers.services.impl.ContactServiceImpl;

import java.util.List;

/**
 * Тесты для класса {@link ContactStateProcessor}
 */
@RunWith(MockitoJUnitRunner.class)
public class ContactStateProcessorTest {

    @Mock
    private AuthenticationServiceImpl authenticationService;

    @Mock
    private ContactServiceImpl contactService;

    @InjectMocks
    private ContactStateProcessor contactStateProcessor;

    /**
     * Обработка состояния добавления контакта
     */
    @Test
    public void handleAddContactStateTest() {
        // Подготовка
        User user = new User("testUser");
        user.setState(State.ADD_CONTACT);
        String contactName = "testContact";
        String message = String.format("Контакт '%s' сохранен", contactName);

        // Действие
        Mockito.when(contactService.addContact(user, contactName)).thenReturn(message);
        String result = contactStateProcessor.processState(user, contactName);

        // Проверка
        Assert.assertEquals(message, result);
        Assert.assertEquals(State.NONE, user.getState());
        Assert.assertEquals(StateType.NONE, user.getStateType());
        Assert.assertNull(user.getContactNameToEdit());
        Assert.assertNull(user.getGroupNameToEdit());
        Mockito.verify(authenticationService, Mockito.times(1)).saveOrUpdateUser(user);
    }

    /**
     * Обработка состояния редактирования контакта
     * <ol>
     *     <li>Обработка состояния с несуществующим контактом</li>
     *     <li>Обработка состояния с существующим контактом</li>
     * </ol>
     */
    @Test
    public void handleEditContactStateTest() {
        // Подготовка
        User user = new User("testUser");
        user.setState(State.EDIT_CONTACT);
        String contactName = "testContact";

        // Действие: обработка состояния изменения несуществующего контакта
        Mockito.when(contactService.existsContact(user, "non-exists")).thenReturn(false);
        String nonExistsContactEditResult = contactStateProcessor.processState(user, "non-exists");

        // Проверка: обработка состояния изменения несуществующего контакта
        Assert.assertEquals("Не удалось найти контакт 'non-exists'", nonExistsContactEditResult);
        Assert.assertEquals(State.EDIT_CONTACT, user.getState());

        // Действие: обработка состояния изменения существующего контакта
        Mockito.when(contactService.existsContact(user, contactName)).thenReturn(true);
        String existsContactEditResult = contactStateProcessor.processState(user, contactName);

        // Проверка: обработка состояния изменения существующего контакта
        Assert.assertEquals("Введите поле, которое вы хотите изменить: \n1. имя\n2. номер", existsContactEditResult);
        Assert.assertEquals(State.EDIT_CONTACT_FIELD, user.getState());
        Assert.assertEquals(contactName, user.getContactNameToEdit());
        Mockito.verify(authenticationService, Mockito.times(1)).saveOrUpdateUser(user);
    }

    /**
     * Обработка состояния выбора поля для изменения контакта
     * <ol>
     *     <li>Обработка некорректного аргумента</li>
     *     <li>Обработка аргумента редактирования имени контакта</li>
     *     <li>Обработка аргумента редактирования номера телефона контакта</li>
     * </ol>
     */
    @Test
    public void handleEditFieldContactStateTest() {
        // Подготовка
        User user = new User("testUser");
        String contactName = "testContact";
        user.setState(State.EDIT_CONTACT_FIELD);
        user.setContactNameToEdit(contactName);

        // Действие: обработка некорректного аргумента при изменении контакта
        String invalidArgumentProcess = contactStateProcessor.processState(user, "111111");

        // Проверка: обработка некорректного аргумента при изменении контакта
        Assert.assertEquals("Введен неверный параметр", invalidArgumentProcess);
        Assert.assertEquals(State.EDIT_CONTACT_FIELD, user.getState());
        Mockito.verify(authenticationService, Mockito.times(0)).saveOrUpdateUser(user);

        // Действие: выбор имени контакта для изменения
        String editContactNameProcess = contactStateProcessor.processState(user, "1");

        // Проверка: выбор имени контакта для изменения
        Assert.assertEquals(State.EDIT_CONTACT_NAME, user.getState());
        Assert.assertEquals("Введите новое имя", editContactNameProcess);
        Mockito.verify(authenticationService, Mockito.times(1)).saveOrUpdateUser(user);

        // Действие: выбор номера телефона контакта для изменения
        user.setState(State.EDIT_CONTACT_FIELD);
        String editContactPhoneNumberProcess = contactStateProcessor.processState(user, "2");

        // Проверка: выбор номера телеофна контакта для изменения
        Assert.assertEquals(State.EDIT_CONTACT_PHONE, user.getState());
        Assert.assertEquals("Введите новый номер телефона", editContactPhoneNumberProcess);
        Mockito.verify(authenticationService, Mockito.times(2)).saveOrUpdateUser(user);
    }

    /**
     * Обработка состояния изменения полей контакта
     * <ol>
     *     <li>Обработка состояния изменения имени контакта</li>
     *     <li>Обрабокта состояния изменения номера телефона контакта</li>
     * </ol>
     */
    @Test
    public void handleEditSpecificFieldContactStateTest() {
        List<State> editFieldStates = List.of(State.EDIT_CONTACT_NAME, State.EDIT_CONTACT_PHONE);
        for (int i = 0; i < editFieldStates.size(); i++) {
            // Подготовка
            User user = new User("testUser");
            String contactName = "testContact";
            user.setState(editFieldStates.get(i));
            user.setContactNameToEdit(contactName);
            String message = "Контакт 'testContact' успешно изменен";

            // Действие
            Mockito.when(contactService.editContact(user, "newValue")).thenReturn(message);
            String result = contactStateProcessor.processState(user, "newValue");

            // Проверка
            Assert.assertEquals(message, result);
            Assert.assertEquals(State.NONE, user.getState());
            Assert.assertEquals(StateType.NONE, user.getStateType());
            Assert.assertNull(user.getContactNameToEdit());
            Assert.assertNull(user.getGroupNameToEdit());
            Mockito.verify(authenticationService, Mockito.times(i + 1)).saveOrUpdateUser(user);
        }
    }

    /**
     * Обработка состояния удаления контакта
     */
    @Test
    public void handleDeleteContactStateTest() {
        // Подготовка
        User user = new User("testUser");
        user.setState(State.DELETE_CONTACT);
        String contactName = "testContact";
        String message = String.format("Контакт '%s' успешно удален", contactName);

        // Действие
        Mockito.when(contactService.deleteContact(user, contactName)).thenReturn(message);
        String result = contactStateProcessor.processState(user, "testContact");

        // Проверка
        Assert.assertEquals(message, result);
        Assert.assertEquals(State.NONE, user.getState());
        Assert.assertEquals(StateType.NONE, user.getStateType());
        Assert.assertNull(user.getContactNameToEdit());
        Assert.assertNull(user.getGroupNameToEdit());
        Mockito.verify(authenticationService, Mockito.times(1)).saveOrUpdateUser(user);
    }

}