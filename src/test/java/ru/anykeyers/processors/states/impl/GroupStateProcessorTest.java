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
import ru.anykeyers.services.impl.GroupServiceImpl;

import java.util.List;

/**
 * Тесты для класса {@link GroupStateProcessor}
 */
@RunWith(MockitoJUnitRunner.class)
public class GroupStateProcessorTest {

    @Mock
    private AuthenticationServiceImpl authenticationService;

    @Mock
    private GroupServiceImpl groupService;

    @InjectMocks
    private GroupStateProcessor groupStateProcessor;

    /**
     * Обработка состояния добавления группы
     */
    @Test
    public void handleAddGroupStateTest() {
        // Подготовка
        User user = new User("testUser");
        user.setState(State.ADD_GROUP);
        String groupName = "testGroup";
        String message = String.format("Группа '%s' сохранена", groupName);

        // Действие
        Mockito.when(groupService.addGroup(user, groupName)).thenReturn(message);
        String result = groupStateProcessor.processState(user, groupName);

        // Проверка
        Assert.assertEquals(message, result);
        Assert.assertEquals(State.NONE, user.getState());
        Assert.assertEquals(StateType.NONE, user.getStateType());
        Assert.assertNull(user.getContactNameToEdit());
        Assert.assertNull(user.getGroupNameToEdit());
        Mockito.verify(authenticationService, Mockito.times(1)).saveOrUpdateUser(user);
    }

    /**
     * Обработка состояния редактирования группы
     * <ol>
     *     <li>Обработка состояния несуществующей группы</li>
     *     <li>Обработка состояния существующей группы</li>
     * </ol>
     */
    @Test
    public void handleEditContactStateTest() {
        // Подготовка
        User user = new User("testUser");
        user.setState(State.EDIT_GROUP);
        String groupName = "testGroup";

        // Действие: обработка состояния изменения несуществующей группы
        Mockito.when(groupService.existsGroup(user, "non-exists")).thenReturn(false);
        String nonExistsGroupEditResult = groupStateProcessor.processState(user, "non-exists");

        // Проверка: обработка состояния изменения несуществующей группы
        Assert.assertEquals("Не удалось найти группу 'non-exists'", nonExistsGroupEditResult);
        Assert.assertEquals(State.EDIT_GROUP, user.getState());

        // Действие: обработка состояния изменения существующей группы
        Mockito.when(groupService.existsGroup(user, groupName)).thenReturn(true);
        String existsGroupEditResult = groupStateProcessor.processState(user, groupName);

        // Проверка: обработка состояния изменения существующей группы
        Assert.assertEquals(
                "Выберите действие: \n1. изменить название\n2. добавить контакт\n3. удалить контакт", existsGroupEditResult
        );
        Assert.assertEquals(State.EDIT_GROUP_FIELD, user.getState());
        Assert.assertEquals(groupName, user.getGroupNameToEdit());
        Mockito.verify(authenticationService, Mockito.times(1)).saveOrUpdateUser(user);
    }

    /**
     * Обработка состояния выбора поля для изменения группы
     * <ol>
     *     <li>Обработка некорректного аргумента</li>
     *     <li>Обработка аргумента редактирования имени группы</li>
     *     <li>Обработка аргумента добавления контакта в группу</li>
     * </ol>
     */
    @Test
    public void handleEditFieldGroupStateTest() {
        // Подготовка
        User user = new User("testUser");
        String groupName = "testGroup";
        user.setState(State.EDIT_GROUP_FIELD);
        user.setGroupNameToEdit(groupName);

        // Действие: обработка некорректного аргумента при изменении группы
        String invalidArgumentProcess = groupStateProcessor.processState(user, "111111");

        // Проверка: обработка некорректного аргумента при изменении группы
        Assert.assertEquals("Введен неверный параметр", invalidArgumentProcess);
        Assert.assertEquals(State.EDIT_GROUP_FIELD, user.getState());
        Mockito.verify(authenticationService, Mockito.times(0)).saveOrUpdateUser(user);

        // Действие: выбор имени группы для изменения
        String editContactNameProcess = groupStateProcessor.processState(user, "1");

        // Проверка: выбор имени группы для изменения
        Assert.assertEquals(State.EDIT_GROUP_NAME, user.getState());
        Assert.assertEquals("Введите новое название группы", editContactNameProcess);
        Mockito.verify(authenticationService, Mockito.times(1)).saveOrUpdateUser(user);

        // Действие: добавление контакта в группу
        user.setState(State.EDIT_GROUP_FIELD);
        String addContactInGroupProcess = groupStateProcessor.processState(user, "2");

        // Проверка: добавление контакта в группу
        Assert.assertEquals(State.EDIT_GROUP_ADD_CONTACT, user.getState());
        Assert.assertEquals("Введите имя контакта, который вы хотите добавить в группу", addContactInGroupProcess);
        Mockito.verify(authenticationService, Mockito.times(2)).saveOrUpdateUser(user);

        // Действие: удаление контакта из группы
        user.setState(State.EDIT_GROUP_FIELD);
        String deleteContactFromGroupProcess = groupStateProcessor.processState(user, "3");

        // Проверка: удаление контакта из группы
        Assert.assertEquals(State.EDIT_GROUP_DELETE_CONTACT, user.getState());
        Assert.assertEquals("Введите имя контакта, который вы хотите удалить из группы", deleteContactFromGroupProcess);
        Mockito.verify(authenticationService, Mockito.times(3)).saveOrUpdateUser(user);
    }

    /**
     * Обработка состояния изменения полей группы
     * <ol>
     *     <li>Обработка состояния изменения имени группы</li>
     *     <li>Обрабокта состояния добавления контакта в группу</li>
     *     <li>Обрабокта состояния удаления контакта из группы</li>
     * </ol>
     */
    @Test
    public void handleEditSpecificFieldGroupStateTest() {
        List<State> editFieldStates = List.of(
                State.EDIT_GROUP_NAME, State.EDIT_GROUP_ADD_CONTACT, State.EDIT_GROUP_DELETE_CONTACT
        );
        for (int i = 0; i < editFieldStates.size(); i++) {
            // Подготовка
            User user = new User("testUser");
            String groupName = "testGroup";
            user.setState(editFieldStates.get(i));
            user.setGroupNameToEdit(groupName);
            String message = "Группа 'testGroup' успешно изменена";

            // Действие
            Mockito.when(groupService.editGroup(user, "newValue")).thenReturn(message);
            String result = groupStateProcessor.processState(user, "newValue");

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
     * Обработка состояния удаления группы
     */
    @Test
    public void handleDeleteGroupStateTest() {
        // Подготовка
        User user = new User("testUser");
        user.setState(State.DELETE_GROUP);
        String groupName = "testGroup";
        String message = String.format("Группа '%s' успешно удалена", groupName);

        // Действие
        Mockito.when(groupService.deleteGroup(user, groupName)).thenReturn(message);
        String result = groupStateProcessor.processState(user, groupName);

        // Проверка
        Assert.assertEquals(message, result);
        Assert.assertEquals(State.NONE, user.getState());
        Assert.assertEquals(StateType.NONE, user.getStateType());
        Assert.assertNull(user.getContactNameToEdit());
        Assert.assertNull(user.getGroupNameToEdit());
        Mockito.verify(authenticationService, Mockito.times(1)).saveOrUpdateUser(user);
    }

}
