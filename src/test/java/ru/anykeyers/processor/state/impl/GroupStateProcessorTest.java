package ru.anykeyers.processor.state.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.anykeyers.domain.Message;
import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.processor.state.domain.State;
import ru.anykeyers.processor.state.domain.StateType;
import ru.anykeyers.service.GroupService;
import ru.anykeyers.service.UserStateService;

import java.util.List;

/**
 * Тесты для класса {@link GroupStateProcessor}
 */
@RunWith(MockitoJUnitRunner.class)
public class GroupStateProcessorTest {

    @Mock
    private UserStateService userStateService;

    @Mock
    private GroupService groupService;

    @InjectMocks
    private GroupStateProcessor groupStateProcessor;

    /**
     * Обработка состояния добавления группы
     */
    @Test
    public void handleAddGroupStateTest() {
        // Подготовка
        User user = new User("testUser");
        StateInfo userStateInfo = new StateInfo();
        userStateInfo.setState(State.ADD_GROUP);
        Mockito.when(userStateService.getUserState(user)).thenReturn(userStateInfo);
        String groupName = "testGroup";
        Message message = new Message(String.format("Группа '%s' сохранена", groupName));

        // Действие
        Message result = groupStateProcessor.processState(user, groupName);

        // Проверка
        Assert.assertEquals(message.getText(), result.getText());
        Assert.assertEquals(State.NONE, userStateInfo.getState());
        Assert.assertEquals(StateType.NONE, userStateInfo.getStateType());
        Assert.assertNull(userStateInfo.getEditInfo());
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
        StateInfo userStateInfo = new StateInfo();
        userStateInfo.setState(State.EDIT_GROUP);
        Mockito.when(userStateService.getUserState(user)).thenReturn(userStateInfo);
        String groupName = "testGroup";

        // Действие: обработка состояния изменения несуществующей группы
        Mockito.when(groupService.existsGroup(user, "non-exists")).thenReturn(false);
        Message nonExistsGroupEditResult = groupStateProcessor.processState(user, "non-exists");

        // Проверка: обработка состояния изменения несуществующей группы
        Assert.assertEquals("Не удалось найти группу 'non-exists'", nonExistsGroupEditResult.getText());
        Assert.assertEquals(State.EDIT_GROUP, userStateInfo.getState());

        // Действие: обработка состояния изменения существующей группы
        Mockito.when(groupService.existsGroup(user, groupName)).thenReturn(true);
        Message existsGroupEditResult = groupStateProcessor.processState(user, groupName);

        // Проверка: обработка состояния изменения существующей группы
        Assert.assertEquals(
                "Выберите действие: \n1. изменить название\n2. добавить контакт\n3. удалить контакт",
                existsGroupEditResult.getText()
        );
        Assert.assertEquals(State.EDIT_GROUP_FIELD, userStateInfo.getState());
        Assert.assertEquals(groupName, userStateInfo.getEditInfo());
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
        StateInfo userStateInfo = new StateInfo();
        userStateInfo.setState(State.EDIT_GROUP_FIELD);
        Mockito.when(userStateService.getUserState(user)).thenReturn(userStateInfo);
        userStateInfo.setEditInfo(groupName);

        // Действие: обработка некорректного аргумента при изменении группы
        Message invalidArgumentProcess = groupStateProcessor.processState(user, "111111");

        // Проверка: обработка некорректного аргумента при изменении группы
        Assert.assertEquals("Введен неверный параметр", invalidArgumentProcess.getText());
        Assert.assertEquals(State.EDIT_GROUP_FIELD, userStateInfo.getState());

        // Действие: выбор имени группы для изменения
        Message editContactNameProcess = groupStateProcessor.processState(user, "1");

        // Проверка: выбор имени группы для изменения
        Assert.assertEquals(State.EDIT_GROUP_NAME, userStateInfo.getState());
        Assert.assertEquals("Введите новое название группы", editContactNameProcess.getText());

        // Действие: добавление контакта в группу
        userStateInfo.setState(State.EDIT_GROUP_FIELD);
        Message addContactInGroupProcess = groupStateProcessor.processState(user, "2");

        // Проверка: добавление контакта в группу
        Assert.assertEquals(State.EDIT_GROUP_ADD_CONTACT, userStateInfo.getState());
        Assert.assertEquals(
                "Введите имя контакта, который вы хотите добавить в группу",
                addContactInGroupProcess.getText()
        );

        // Действие: удаление контакта из группы
        userStateInfo.setState(State.EDIT_GROUP_FIELD);
        Message deleteContactFromGroupProcess = groupStateProcessor.processState(user, "3");

        // Проверка: удаление контакта из группы
        Assert.assertEquals(State.EDIT_GROUP_DELETE_CONTACT, userStateInfo.getState());
        Assert.assertEquals(
                "Введите имя контакта, который вы хотите удалить из группы",
                deleteContactFromGroupProcess.getText()
        );
    }

    /**
     * Обработка состояния изменения полей группы
     * <ol>
     *     <li>Обработка состояния изменения имени группы</li>
     *     <li>Обработка состояния добавления контакта в группу</li>
     *     <li>Обработка состояния удаления контакта из группы</li>
     * </ol>
     */
    @Test
    public void handleEditSpecificFieldGroupStateTest() {
        List<State> editFieldStates = List.of(
                State.EDIT_GROUP_NAME, State.EDIT_GROUP_ADD_CONTACT, State.EDIT_GROUP_DELETE_CONTACT
        );
        for (State editFieldState : editFieldStates) {
            // Подготовка
            User user = new User("testUser");
            String groupName = "testGroup";
            StateInfo userStateInfo = new StateInfo();
            userStateInfo.setState(editFieldState);
            Mockito.when(userStateService.getUserState(user)).thenReturn(userStateInfo);
            userStateInfo.setEditInfo(groupName);
            Message message = new Message("Группа 'testGroup' успешно изменена");

            // Действие
            Message result = groupStateProcessor.processState(user, "newValue");

            // Проверка
            Assert.assertEquals(message.getText(), result.getText());
            Assert.assertEquals(State.NONE, userStateInfo.getState());
            Assert.assertEquals(StateType.NONE, userStateInfo.getStateType());
            Assert.assertNull(userStateInfo.getEditInfo());
        }
    }

    /**
     * Обработка состояния удаления группы
     */
    @Test
    public void handleDeleteGroupStateTest() {
        // Подготовка
        User user = new User("testUser");
        StateInfo userStateInfo = new StateInfo();
        userStateInfo.setState(State.DELETE_GROUP);
        Mockito.when(userStateService.getUserState(user)).thenReturn(userStateInfo);
        String groupName = "testGroup";
        Message message = new Message(String.format("Группа '%s' успешно удалена", groupName));

        // Действие
        Message result = groupStateProcessor.processState(user, groupName);

        // Проверка
        Assert.assertEquals(message.getText(), result.getText());
        Assert.assertEquals(State.NONE, userStateInfo.getState());
        Assert.assertEquals(StateType.NONE, userStateInfo.getStateType());
        Assert.assertNull(userStateInfo.getEditInfo());
    }

}
