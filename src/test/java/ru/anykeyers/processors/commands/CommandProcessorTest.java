package ru.anykeyers.processors.commands;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.anykeyers.bots.BotType;
import ru.anykeyers.domain.User;
import ru.anykeyers.processors.states.domain.State;
import ru.anykeyers.processors.states.domain.StateType;
import ru.anykeyers.services.impl.AuthenticationServiceImpl;

/**
 * Тесты для класса {@link CommandProcessor}
 */
@RunWith(MockitoJUnitRunner.class)
public class CommandProcessorTest {

    @Mock
    private AuthenticationServiceImpl authenticationService;

    @InjectMocks
    private CommandProcessor commandProcessor;

    /**
     * Обработка команды по добавлению контакта
     */
    @Test
    public void processCommandAddContactTest() {
        // Подготовка
        User user = new User("testUser", BotType.CONSOLE);

        // Действие
        String addContactResult = commandProcessor.processCommand(user, Command.ADD_CONTACT_COMMAND);

        // Проверка
        Assert.assertEquals("Введите имя нового контакта", addContactResult);
        Assert.assertEquals(State.ADD_CONTACT, user.getState());
        Assert.assertEquals(StateType.CONTACT, user.getStateType());
        Mockito.verify(authenticationService, Mockito.times(1)).saveOrUpdateUser(user);
    }

    /**
     * Обработка команды редактирования контакта
     */
    @Test
    public void processCommandEditContactTest() {
        // Подготовка
        User user = new User("testUser", BotType.CONSOLE);

        // Действие
        String editContactResult = commandProcessor.processCommand(user, Command.EDIT_CONTACT_COMMAND);

        // Проверка
        Assert.assertEquals("Введите имя контакта, который хотите изменить", editContactResult);
        Assert.assertEquals(State.EDIT_CONTACT, user.getState());
        Assert.assertEquals(StateType.CONTACT, user.getStateType());
        Mockito.verify(authenticationService, Mockito.times(1)).saveOrUpdateUser(user);
    }

    /**
     * Обработка команды удаления контакта
     */
    @Test
    public void processCommandDeleteContactTest() {
        // Подготовка
        User user = new User("testUser", BotType.CONSOLE);

        // Действие
        String deleteContactResult = commandProcessor.processCommand(user, Command.DELETE_CONTACT_COMMAND);

        // Проверка
        Assert.assertEquals("Введите имя контакта, который хотите удалить", deleteContactResult);
        Assert.assertEquals(State.DELETE_CONTACT, user.getState());
        Assert.assertEquals(StateType.CONTACT, user.getStateType());
        Mockito.verify(authenticationService, Mockito.times(1)).saveOrUpdateUser(user);
    }

    /**
     * Обработка команды добавления группы
     */
    @Test
    public void processCommandAddGroupTest() {
        // Подготовка
        User user = new User("testUser", BotType.CONSOLE);

        // Действие
        String addGroupResult = commandProcessor.processCommand(user, Command.ADD_GROUP_COMMAND);

        // Проверка
        Assert.assertEquals("Введите название группы", addGroupResult);
        Assert.assertEquals(State.ADD_GROUP, user.getState());
        Assert.assertEquals(StateType.GROUP, user.getStateType());
        Mockito.verify(authenticationService, Mockito.times(1)).saveOrUpdateUser(user);
    }

    /**
     * Обработка команды редактирования группы
     */
    @Test
    public void processCommandEditGroupTest() {
        // Подготовка
        User user = new User("testUser", BotType.CONSOLE);

        // Действие
        String editGroupResult = commandProcessor.processCommand(user, Command.EDIT_GROUP_COMMAND);

        // Проверка
        Assert.assertEquals("Введите название группы, состояние которой вы хотите изменить", editGroupResult);
        Assert.assertEquals(State.EDIT_GROUP, user.getState());
        Assert.assertEquals(StateType.GROUP, user.getStateType());
        Mockito.verify(authenticationService, Mockito.times(1)).saveOrUpdateUser(user);
    }

    /**
     * Обработка команды удаления группы
     */
    @Test
    public void processCommandDeleteGroupTest() {
        // Подготовка
        User user = new User("testUser", BotType.CONSOLE);

        // Действие
        String deleteGroupResult = commandProcessor.processCommand(user, Command.DELETE_GROUP_COMMAND);

        // Проверка
        Assert.assertEquals("Введите название группы, которую хотите удалить", deleteGroupResult);
        Assert.assertEquals(State.DELETE_GROUP, user.getState());
        Assert.assertEquals(StateType.GROUP, user.getStateType());
        Mockito.verify(authenticationService, Mockito.times(1)).saveOrUpdateUser(user);
    }

    /**
     * Обработка команды получения всех команд, зарегистрированных в приложении
     */
    @Test
    public void processCommandGetAllCommandsTest() {
        // Подготовка
        User user = new User("testUser", BotType.CONSOLE);

        // Действие
        String getAllCommandsResult = commandProcessor.processCommand(user, Command.HELP_COMMAND);

        // Проверка
        String expectedResult = """
                /add_contact : Добавить контакт
                /edit_contact : Изменить контакт
                /delete_contact : Удалить контакт
                /add_group : Добавить группу
                /edit_group : Изменить состояние группы
                /delete_group : Удалить группу
                /search : Поиск по контактам и группам
                /filter : Фильтрация контактов
                /sort : Сортировка контактов
                /help : Показать возможные комманды""";
        Assert.assertEquals(expectedResult, getAllCommandsResult);
        Assert.assertEquals(State.NONE, user.getState());
        Assert.assertEquals(StateType.NONE, user.getStateType());
        Mockito.verify(authenticationService, Mockito.times(1)).saveOrUpdateUser(user);
    }

}