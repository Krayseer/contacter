package ru.anykeyers.processor.state.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.processor.state.domain.State;
import ru.anykeyers.processor.state.domain.StateType;
import ru.anykeyers.service.ContactService;
import ru.anykeyers.service.UserStateService;

import java.util.List;

/**
 * Тесты для класса {@link ContactStateProcessor}
 */
@RunWith(MockitoJUnitRunner.class)
public class ContactStateProcessorTest {

    @Mock
    private UserStateService userStateService;

    @Mock
    private ContactService contactService;

    @InjectMocks
    private ContactStateProcessor contactStateProcessor;

    /**
     * Обработка состояния добавления контакта
     */
    @Test
    public void handleAddContactStateTest() {
        // Подготовка
        User user = new User("testUser");
        StateInfo userStateInfo = new StateInfo();
        userStateInfo.setState(State.ADD_CONTACT);
        Mockito.when(userStateService.getUserState(user)).thenReturn(userStateInfo);
        String contactName = "testContact";

        // Действие
        contactStateProcessor.processState(user, contactName);

        // Проверка
        Assert.assertEquals(State.NONE, userStateInfo.getState());
        Assert.assertEquals(StateType.NONE, userStateInfo.getStateType());
        Assert.assertNull(userStateInfo.getEditInfo());
        Mockito.verify(contactService, Mockito.times(1)).addContact(user, contactName);
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
        StateInfo userStateInfo = new StateInfo();
        userStateInfo.setState(State.EDIT_CONTACT);
        Mockito.when(userStateService.getUserState(user)).thenReturn(userStateInfo);
        String contactName = "testContact";
        String notExistContactName = "non-exists";

        // Действие: обработка состояния изменения несуществующего контакта
        Mockito.when(contactService.existsContact(user, notExistContactName)).thenReturn(false);
        contactStateProcessor.processState(user, notExistContactName);

        // Проверка
        Assert.assertEquals(State.EDIT_CONTACT, userStateInfo.getState());

        // Действие: обработка состояния изменения существующего контакта
        Mockito.when(contactService.existsContact(user, contactName)).thenReturn(true);
        contactStateProcessor.processState(user, contactName);

        // Проверка
        Assert.assertEquals(State.EDIT_CONTACT_FIELD, userStateInfo.getState());
        Assert.assertEquals(contactName, userStateInfo.getEditInfo());
    }

    /**
     * Обработка состояния выбора поля для изменения контакта
     * <ol>
     *     <li>Обработка некорректного аргумента</li>
     *     <li>Обработка аргумента редактирования имени контакта</li>
     *     <li>Обработка аргумента редактирования номера телефона контакта</li>
     *     <li>Обработка аргумента редактирования возраста контакта</li>
     *     <li>Обработка аргумента редактирования пола контакта</li>
     *     <li>Обработка аргумента редактирования блокировки контакта</li>
     * </ol>
     */
    @Test
    public void handleEditFieldContactStateTest() {
        // Подготовка
        User user = new User("testUser");
        String contactName = "testContact";
        StateInfo userStateInfo = new StateInfo();
        userStateInfo.setState(State.EDIT_CONTACT_FIELD);
        userStateInfo.setEditInfo(contactName);
        Mockito.when(userStateService.getUserState(user)).thenReturn(userStateInfo);

        // Действие: обработка некорректного аргумента при изменении контакта
        contactStateProcessor.processState(user, "111111");

        // Проверка
        Assert.assertEquals(State.EDIT_CONTACT_FIELD, userStateInfo.getState());

        // Действие: выбор имени контакта для изменения
        contactStateProcessor.processState(user, "1");

        // Проверка: выбор имени контакта для изменения
        Assert.assertEquals(State.EDIT_CONTACT_NAME, userStateInfo.getState());

        // Действие: выбор номера телефона контакта для изменения
        userStateInfo.setState(State.EDIT_CONTACT_FIELD);
        contactStateProcessor.processState(user, "2");

        // Проверка: выбор номера телефона контакта для изменения
        Assert.assertEquals(State.EDIT_CONTACT_PHONE, userStateInfo.getState());

        // Действие: выбор возраста контакта для изменения
        userStateInfo.setState(State.EDIT_CONTACT_FIELD);
        contactStateProcessor.processState(user, "3");

        // Проверка: выбор возраста контакта для изменения
        Assert.assertEquals(State.EDIT_CONTACT_AGE, userStateInfo.getState());

        // Действие: выбор пола контакта для изменения
        userStateInfo.setState(State.EDIT_CONTACT_FIELD);
        contactStateProcessor.processState(user, "4");

        // Проверка: выбор пола контакта для изменения
        Assert.assertEquals(State.EDIT_CONTACT_GENDER, userStateInfo.getState());

        // Действие: выбор блокировки контакта для изменения
        userStateInfo.setState(State.EDIT_CONTACT_FIELD);
        contactStateProcessor.processState(user, "5");

        // Проверка
        Assert.assertEquals(State.EDIT_CONTACT_BLOCK, userStateInfo.getState());
    }

    /**
     * Обработка состояния изменения полей контакта
     * <ol>
     *     <li>Обработка состояния изменения имени контакта</li>
     *     <li>Обработка состояния изменения номера телефона контакта</li>
     * </ol>
     */
    @Test
    public void handleEditSpecificFieldContactStateTest() {
        List<State> editFieldStates = List.of(
                State.EDIT_CONTACT_NAME, State.EDIT_CONTACT_PHONE, State.EDIT_CONTACT_AGE, State.EDIT_CONTACT_GENDER, State.EDIT_CONTACT_BLOCK);
        for (State editFieldState : editFieldStates) {
            // Подготовка
            User user = new User("testUser");
            String contactName = "testContact";
            StateInfo userStateInfo = new StateInfo();
            userStateInfo.setState(editFieldState);
            userStateInfo.setEditInfo(contactName);
            Mockito.when(userStateService.getUserState(user)).thenReturn(userStateInfo);
            String message = "Контакт 'testContact' успешно изменен";

            // Действие
            String result = contactStateProcessor.processState(user, "newValue");

            // Проверка
            Assert.assertEquals(message, result);
            Assert.assertEquals(State.NONE, userStateInfo.getState());
            Assert.assertEquals(StateType.NONE, userStateInfo.getStateType());
            Assert.assertNull(userStateInfo.getEditInfo());
        }
    }

    /**
     * Обработка состояния удаления контакта
     */
    @Test
    public void handleDeleteContactStateTest() {
        // Подготовка
        User user = new User("testUser");
        StateInfo userStateInfo = new StateInfo();
        userStateInfo.setState(State.DELETE_CONTACT);
        Mockito.when(userStateService.getUserState(user)).thenReturn(userStateInfo);
        String contactName = "testContact";
        String message = String.format("Контакт '%s' успешно удален", contactName);

        // Действие
        String result = contactStateProcessor.processState(user, "testContact");

        // Проверка
        Assert.assertEquals(message, result);
        Assert.assertEquals(State.NONE, userStateInfo.getState());
        Assert.assertEquals(StateType.NONE, userStateInfo.getStateType());
        Assert.assertNull(userStateInfo.getEditInfo());
    }

}