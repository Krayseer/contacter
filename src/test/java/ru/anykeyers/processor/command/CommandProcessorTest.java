package ru.anykeyers.processor.command;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.anykeyers.bot.BotType;
import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.processor.state.domain.State;
import ru.anykeyers.processor.state.domain.StateType;
import ru.anykeyers.service.UserStateService;

/**
 * Тесты для класса {@link CommandProcessor}
 */
@RunWith(MockitoJUnitRunner.class)
public class CommandProcessorTest {

    @Mock
    private UserStateService userStateService;

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
        Mockito.when(userStateService.getUserState(user)).thenReturn(new StateInfo());
        String result = commandProcessor.processCommand(user, Command.ADD_CONTACT_COMMAND);

        // Проверка
        Assert.assertEquals("Введите имя нового контакта", result);
        StateInfo userStateInfo = userStateService.getUserState(user);
        Assert.assertEquals(State.ADD_CONTACT, userStateInfo.getState());
        Assert.assertEquals(StateType.CONTACT, userStateInfo.getStateType());
    }

    /**
     * Обработка команды редактирования контакта
     */
    @Test
    public void processCommandEditContactTest() {
        // Подготовка
        User user = new User("testUser", BotType.CONSOLE);

        // Действие
        Mockito.when(userStateService.getUserState(user)).thenReturn(new StateInfo());
        String result = commandProcessor.processCommand(user, Command.EDIT_CONTACT_COMMAND);

        // Проверка
        Assert.assertEquals("Введите имя контакта, который хотите изменить", result);
        StateInfo userStateInfo = userStateService.getUserState(user);
        Assert.assertEquals(State.EDIT_CONTACT, userStateInfo.getState());
        Assert.assertEquals(StateType.CONTACT, userStateInfo.getStateType());
    }

    /**
     * Обработка команды удаления контакта
     */
    @Test
    public void processCommandDeleteContactTest() {
        // Подготовка
        User user = new User("testUser", BotType.CONSOLE);

        // Действие
        Mockito.when(userStateService.getUserState(user)).thenReturn(new StateInfo());
        String result = commandProcessor.processCommand(user, Command.DELETE_CONTACT_COMMAND);

        // Проверка
        Assert.assertEquals("Введите имя контакта, который хотите удалить", result);
        StateInfo userStateInfo = userStateService.getUserState(user);
        Assert.assertEquals(State.DELETE_CONTACT, userStateInfo.getState());
        Assert.assertEquals(StateType.CONTACT, userStateInfo.getStateType());
    }

    /**
     * Обработка команды добавления группы
     */
    @Test
    public void processCommandAddGroupTest() {
        // Подготовка
        User user = new User("testUser", BotType.CONSOLE);

        // Действие
        Mockito.when(userStateService.getUserState(user)).thenReturn(new StateInfo());
        String result = commandProcessor.processCommand(user, Command.ADD_GROUP_COMMAND);

        // Проверка
        Assert.assertEquals("Введите название группы", result);
        StateInfo userStateInfo = userStateService.getUserState(user);
        Assert.assertEquals(State.ADD_GROUP, userStateInfo.getState());
        Assert.assertEquals(StateType.GROUP, userStateInfo.getStateType());
    }

    /**
     * Обработка команды редактирования группы
     */
    @Test
    public void processCommandEditGroupTest() {
        // Подготовка
        User user = new User("testUser", BotType.CONSOLE);

        // Действие
        Mockito.when(userStateService.getUserState(user)).thenReturn(new StateInfo());
        String result = commandProcessor.processCommand(user, Command.EDIT_GROUP_COMMAND);

        // Проверка
        Assert.assertEquals("Введите название группы, состояние которой вы хотите изменить", result);
        StateInfo userStateInfo = userStateService.getUserState(user);
        Assert.assertEquals(State.EDIT_GROUP, userStateInfo.getState());
        Assert.assertEquals(StateType.GROUP, userStateInfo.getStateType());
    }

    /**
     * Обработка команды удаления группы
     */
    @Test
    public void processCommandDeleteGroupTest() {
        // Подготовка
        User user = new User("testUser", BotType.CONSOLE);

        // Действие
        Mockito.when(userStateService.getUserState(user)).thenReturn(new StateInfo());
        String result = commandProcessor.processCommand(user, Command.DELETE_GROUP_COMMAND);

        // Проверка
        Assert.assertEquals("Введите название группы, которую хотите удалить", result);
        StateInfo userStateInfo = userStateService.getUserState(user);
        Assert.assertEquals(State.DELETE_GROUP, userStateInfo.getState());
        Assert.assertEquals(StateType.GROUP, userStateInfo.getStateType());
    }

    /**
     * Обработка команды получения информации
     */
    @Test
    public void processCommandGetTest() {
        // Подготовка
        User user = new User("testUser", BotType.CONSOLE);

        // Действие
        Mockito.when(userStateService.getUserState(user)).thenReturn(new StateInfo());
        String result = commandProcessor.processCommand(user, Command.GET_COMMAND);

        // Проверка
        Assert.assertEquals("""
                Выберите действие:
                1. Получить все контакты
                2. Получить все группы
                3. Получить все контакты группы""", result);
        StateInfo userStateInfo = userStateService.getUserState(user);
        Assert.assertEquals(State.GET_KIND, userStateInfo.getState());
        Assert.assertEquals(StateType.OPERATION, userStateInfo.getStateType());
    }

    /**
     * Обработка команды поиска
     */
    @Test
    public void processCommandSearchTest() {
        // Подготовка
        User user = new User("testUser", BotType.CONSOLE);

        // Действие
        Mockito.when(userStateService.getUserState(user)).thenReturn(new StateInfo());
        String result = commandProcessor.processCommand(user, Command.SEARCH_COMMAND);

        // Проверка
        Assert.assertEquals("""
                Выберите действие:
                1. Найти контакты по имени
                2. Найти контакты по номеру телефона
                3. Найти контакты в группе по имени""", result);
        StateInfo userStateInfo = userStateService.getUserState(user);
        Assert.assertEquals(State.SEARCH_KIND, userStateInfo.getState());
        Assert.assertEquals(StateType.OPERATION, userStateInfo.getStateType());
    }

    /**
     * Обработка команды фильтрации
     */
    @Test
    public void processCommandFilterTest() {
        // Подготовка
        User user = new User("testUser", BotType.CONSOLE);

        // Действие
        Mockito.when(userStateService.getUserState(user)).thenReturn(new StateInfo());
        String result = commandProcessor.processCommand(user, Command.FILTER_COMMAND);

        // Проверка
        Assert.assertEquals("""
                Выберите поле для фильтрации:
                1. Возраст
                2. Пол
                3. Блокировка""", result);
        StateInfo userStateInfo = userStateService.getUserState(user);
        Assert.assertEquals(State.FILTER_KIND, userStateInfo.getState());
        Assert.assertEquals(StateType.OPERATION, userStateInfo.getStateType());
    }

    /**
     * Обработка команды сортировки
     */
    @Test
    public void processCommandSortTest() {
        // Подготовка
        User user = new User("testUser", BotType.CONSOLE);

        // Действие
        Mockito.when(userStateService.getUserState(user)).thenReturn(new StateInfo());
        String result = commandProcessor.processCommand(user, Command.SORT_COMMAND);

        // Проверка
        Assert.assertEquals("""
                Выберите параметр сортировки:
                1. Имя
                2. Возраст""", result);
        StateInfo userStateInfo = userStateService.getUserState(user);
        Assert.assertEquals(State.SORT_KIND, userStateInfo.getState());
        Assert.assertEquals(StateType.OPERATION, userStateInfo.getStateType());
    }

    /**
     * Обработка команды получения всех команд, зарегистрированных в приложении
     */
    @Test
    public void processCommandGetAllCommandsTest() {
        // Подготовка
        User user = new User("testUser", BotType.CONSOLE);

        // Действие
        Mockito.when(userStateService.getUserState(user)).thenReturn(new StateInfo());
        String result = commandProcessor.processCommand(user, Command.HELP_COMMAND);

        // Проверка
        Assert.assertEquals("""
                /add_contact : Добавить контакт
                /edit_contact : Изменить контакт
                /delete_contact : Удалить контакт
                /add_group : Добавить группу
                /edit_group : Изменить состояние группы
                /delete_group : Удалить группу
                /get : Получить информацию
                /search : Поиск по контактам и группам
                /filter : Фильтрация контактов
                /sort : Сортировка контактов
                /help : Показать все возможные команды""", result);
        StateInfo userStateInfo = userStateService.getUserState(user);
        Assert.assertEquals(State.NONE, userStateInfo.getState());
        Assert.assertEquals(StateType.NONE, userStateInfo.getStateType());
    }

}