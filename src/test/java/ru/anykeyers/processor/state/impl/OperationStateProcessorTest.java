package ru.anykeyers.processor.state.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.anykeyers.domain.Gender;
import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.domain.entity.Group;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.processor.state.domain.State;
import ru.anykeyers.service.*;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Тесты для класса {@link OperationStateProcessor}
 */
@RunWith(MockitoJUnitRunner.class)
public class OperationStateProcessorTest {

    @Mock
    private UserStateService userStateService;

    @Mock
    private DataRetrievalService dataRetrievalService;

    @Mock
    private SearchService searchService;

    @Mock
    private FilterContactService filterService;

    @Mock
    private SortContactService sortService;

    @InjectMocks
    private OperationStateProcessor operationStateProcessor;

    private User user;

    private Contact contact;

    private Group group;

    @Before
    public void setUp() {
        user = new User("testUser");

        contact = new Contact("testId", "testContact");
        contact.setPhoneNumber("799999");
        contact.setAge(30);
        contact.setGender(Gender.MAN);

        group = new Group("testId", "testGroup");
        group.addContactInGroup(contact);
    }

    /**
     * Обработка состояния получения информации
     * <ol>
     *     <li>Получение всех контактов пользователя</li>
     *     <li>Получение всех групп пользователя</li>
     *     <li>Получение всех контактов группы</li>
     *     <li>Обработка неверно введенного аргумента</li>
     * </ol>
     */
    @Test
    public void handleGetKindStateTest() {
        // Подготовка
        StateInfo userStateInfo = new StateInfo();
        userStateInfo.setState(State.GET_KIND);

        // Действие: получение всех контактов пользователя
        Mockito.when(dataRetrievalService.getAllContacts(user))
                .thenReturn(Set.of(contact));
        Mockito.when(userStateService.getUserState(user)).thenReturn(userStateInfo);
        String resultContact = operationStateProcessor.processState(user, "1");

        // Проверка
        Assert.assertEquals(State.NONE, userStateInfo.getState());
        Assert.assertNull(userStateInfo.getEditInfo());
        Assert.assertEquals("""
                Имя: testContact
                Номер телефона: 799999
                Возраст: 30
                Пол: Мужской
                Заблокирован: Нет""", resultContact);

        // Действие: получение всех групп пользователя
        userStateInfo.setState(State.GET_KIND);
        Mockito.when(dataRetrievalService.getAllGroups(user))
                .thenReturn(Set.of(group));
        String resultGroup = operationStateProcessor.processState(user, "2");

        // Проверка
        Assert.assertEquals(State.NONE, userStateInfo.getState());
        Assert.assertEquals("""
                Название: testGroup. Количество участников: 1.""", resultGroup);

        // Действие: получение всех контактов группы
        userStateInfo.setState(State.GET_KIND);
        String resultGroupContacts = operationStateProcessor.processState(user, "3");

        // Проверка
        Assert.assertEquals(State.GET_GROUP_CONTACTS, userStateInfo.getState());
        Assert.assertNull(userStateInfo.getEditInfo());
        Assert.assertEquals("Введите название группы, контакты которой хотите получить", resultGroupContacts);

        // Действие: обработка некорректного аргумента
        userStateInfo.setState(State.GET_KIND);
        String resultError = operationStateProcessor.processState(user, "4151");

        // Проверка
        Assert.assertEquals("Введен неверный параметр", resultError);
    }

    /**
     * Обработка состояния получения контактов группы
     */
    @Test
    public void handleGetGroupContactsStateTest() {
        // Подготовка
        StateInfo userStateInfo = new StateInfo();
        userStateInfo.setState(State.GET_GROUP_CONTACTS);

        // Действие
        Mockito.when(dataRetrievalService.getAllGroupContacts(user, group.getName()))
                .thenReturn(Set.of(contact));
        Mockito.when(userStateService.getUserState(user)).thenReturn(userStateInfo);
        String resultGroupContacts = operationStateProcessor.processState(user, group.getName());

        // Проверка
        Assert.assertEquals(State.NONE, userStateInfo.getState());
        Assert.assertNull(userStateInfo.getEditInfo());
        Assert.assertEquals("""
                Имя: testContact
                Номер телефона: 799999
                Возраст: 30
                Пол: Мужской
                Заблокирован: Нет""", resultGroupContacts);
    }

    /**
     * Обработка состояния поиска контактов (выбор действия)
     * <ol>
     *     <li>По имени</li>
     *     <li>По номеру телефона</li>
     *     <li>В группе</li>
     *     <li>Выбор некорректного действия</li>
     * </ol>
     */
    @Test
    public void handleSearchKindStateTest() {
        // Подготовка
        StateInfo userStateInfo = new StateInfo();
        userStateInfo.setState(State.SEARCH_KIND);

        // Действие: поиск по имени контакта
        Mockito.when(userStateService.getUserState(user)).thenReturn(userStateInfo);
        String searchContactByNameResult = operationStateProcessor.processState(user, "1");

        // Проверка
        Assert.assertEquals(State.SEARCH_NAME, userStateInfo.getState());
        Assert.assertEquals(
                "Введите имя контакта, информацию о котором нужно вывести", searchContactByNameResult
        );

        // Действие: поиск по номеру контакта
        userStateInfo.setState(State.SEARCH_KIND);
        String searchContactByPhoneResult = operationStateProcessor.processState(user, "2");

        // Проверка
        Assert.assertEquals(State.SEARCH_PHONE, userStateInfo.getState());
        Assert.assertEquals(
                "Введите номер телефона контакта, информацию о котором нужно вывести", searchContactByPhoneResult
        );

        // Действие: поиск контактов в группе
        userStateInfo.setState(State.SEARCH_KIND);
        String searchGroupContacts = operationStateProcessor.processState(user, "3");

        // Проверка
        Assert.assertEquals(State.SEARCH_GROUP_CONTACTS, userStateInfo.getState());
        Assert.assertEquals("Введите название группы, контакты которой нужно вывести", searchGroupContacts);

        // Действие: ввод некорректного аргумента
        userStateInfo.setState(State.SEARCH_KIND);
        String searchError = operationStateProcessor.processState(user, "4");

        // Проверка
        Assert.assertEquals(State.SEARCH_KIND, userStateInfo.getState());
        Assert.assertEquals("Введен неверный параметр", searchError);
    }

    /**
     * Обработка состояния поиска контактов в группе по имени
     */
    @Test
    public void handleSearchGroupContactsTest() {
        // Подготовка
        StateInfo userStateInfo = new StateInfo();
        userStateInfo.setState(State.SEARCH_GROUP_CONTACTS);

        // Действие
        Mockito.when(userStateService.getUserState(user)).thenReturn(userStateInfo);
        String result = operationStateProcessor.processState(user, "testGroup");

        // Проверка
        Assert.assertEquals(State.SEARCH_GROUP_CONTACTS_BY_NAME, userStateInfo.getState());
        Assert.assertEquals("testGroup", userStateInfo.getEditInfo());
        Assert.assertEquals(
                "Введите имя контакта", result
        );
    }

    /**
     * Обработка состояния получения контактов в результате поиска
     * <ol>
     *     <li>По имени</li>
     *     <li>По номеру телефона</li>
     *     <li>В группе</li>
     * </ol>
     */
    @Test
    public void handleSearchByArgumentTest() {
        // Подготовка
        StateInfo userStateInfo = new StateInfo();
        userStateInfo.setState(State.SEARCH_NAME);
        Mockito.when(userStateService.getUserState(user)).thenReturn(userStateInfo);

        // Действие: найти контакты по имени
        Mockito.when(searchService.findContactsByArgument(user, userStateInfo, "test"))
                .thenReturn(Set.of(contact));
        String searchNameResult = operationStateProcessor.processState(user, "test");

        // Проверка
        Assert.assertEquals(State.NONE, userStateInfo.getState());
        Assert.assertEquals("""
                Имя: testContact
                Номер телефона: 799999
                Возраст: 30
                Пол: Мужской
                Заблокирован: Нет""", searchNameResult);

        // Действие: найти контакты по номеру телефона
        userStateInfo.setState(State.SEARCH_PHONE);
        Mockito.when(searchService.findContactsByArgument(user, userStateInfo, "7"))
                .thenReturn(Set.of(contact));
        String searchPhoneResult = operationStateProcessor.processState(user, "7");

        // Проверка
        Assert.assertEquals(State.NONE, userStateInfo.getState());
        Assert.assertEquals("""
                Имя: testContact
                Номер телефона: 799999
                Возраст: 30
                Пол: Мужской
                Заблокирован: Нет""", searchPhoneResult);

        // Действие: найти контакты в группе по имени
        userStateInfo.setState(State.SEARCH_GROUP_CONTACTS_BY_NAME);
        userStateInfo.setEditInfo(group.getName());
        Mockito.when(searchService.findContactsByArgument(user, userStateInfo, "test"))
                .thenReturn(Set.of(contact));
        String searchGroupContactsResult = operationStateProcessor.processState(user, "test");

        // Проверка
        Assert.assertEquals(State.NONE, userStateInfo.getState());
        Assert.assertEquals("""
                Имя: testContact
                Номер телефона: 799999
                Возраст: 30
                Пол: Мужской
                Заблокирован: Нет""", searchGroupContactsResult);
    }

    /**
     * Обработка состояния по фильтрации контактов (выбор действия)
     * <ol>
     *     <li>По возрасту</li>
     *     <li>По полу</li>
     *     <li>По блокировке</li>
     *     <li>Выбор некорректного действия</li>
     * </ol>
     */
    @Test
    public void handleFilterKindStateTest() {
        // Подготовка
        StateInfo userStateInfo = new StateInfo();
        userStateInfo.setState(State.FILTER_KIND);
        Mockito.when(userStateService.getUserState(user)).thenReturn(userStateInfo);

        // Действие: выбор действия фильтрации по возрасту
        String filterByAge = operationStateProcessor.processState(user, "1");

        // Проверка
        Assert.assertEquals(State.FILTER_AGE, userStateInfo.getState());
        Assert.assertEquals("Введите возраст, по которому будет происходить фильтрация", filterByAge);

        // Действие: выбор действия фильтрации по полу
        userStateInfo.setState(State.FILTER_KIND);
        String filterByGender = operationStateProcessor.processState(user, "2");

        // Проверка
        Assert.assertEquals(State.FILTER_GENDER, userStateInfo.getState());
        Assert.assertEquals("""
                Выберите пол:
                1. Мужской
                2. Женский""", filterByGender);

        // Действие: выбор действия фильтрации по блокировке
        userStateInfo.setState(State.FILTER_KIND);
        String filterByBlock = operationStateProcessor.processState(user, "3");

        // Проверка
        Assert.assertEquals(State.FILTER_BLOCK, userStateInfo.getState());
        Assert.assertEquals("""
                Выберите параметр:
                1. Заблокированные контакты
                2. Не заблокированные контакты""", filterByBlock);

        // Действие: выбор некорректного аргумента действия
        userStateInfo.setState(State.FILTER_KIND);
        String filterError = operationStateProcessor.processState(user, "5143");

        // Проверка
        Assert.assertEquals(State.FILTER_KIND, userStateInfo.getState());
        Assert.assertEquals("Введен неверный параметр", filterError);
    }

    /**
     * Обработка состояния фильтрации контактов по возрасту (ввод возраста)
     */
    @Test
    public void handleFilterAgeStateTest() {
        // Подготовка
        StateInfo userStateInfo = new StateInfo();
        userStateInfo.setState(State.FILTER_AGE);
        Mockito.when(userStateService.getUserState(user)).thenReturn(userStateInfo);

        // Действие
        String result = operationStateProcessor.processState(user, "25");

        // Проверка
        Assert.assertEquals(State.FILTER_AGE_KIND, userStateInfo.getState());
        Assert.assertEquals("25", userStateInfo.getEditInfo());
        Assert.assertEquals("""
                Выберите аргумент:
                1. Старше
                2. Младше
                3. Равен""", result);
    }

    /**
     * Обработка состояния фильтрации
     * <ol>
     *     <li>По возрасту</li>
     *     <li>По полу</li>
     *     <li>По блокировке</li>
     * </ol>
     */
    @Test
    public void handleFilterByStateAndKindTest() {
        // Подготовка
        StateInfo userStateInfo = new StateInfo();
        userStateInfo.setState(State.FILTER_AGE_KIND);
        Mockito.when(userStateService.getUserState(user)).thenReturn(userStateInfo);

        // Действие: фильтрация по контактам по возрасту (старше)
        Mockito.when(filterService.filterByUserStateAndKind(user, userStateInfo, "1"))
                .thenReturn(Set.of(contact));
        String ageGreaterFilterResult = operationStateProcessor.processState(user, "1");

        // Проверка
        Assert.assertEquals(State.NONE, userStateInfo.getState());
        Assert.assertEquals("""
                Имя: testContact
                Номер телефона: 799999
                Возраст: 30
                Пол: Мужской
                Заблокирован: Нет""", ageGreaterFilterResult);

        // Действие: фильтрация по контактам с мужским полом
        userStateInfo.setState(State.FILTER_GENDER);
        Mockito.when(filterService.filterByUserStateAndKind(user, userStateInfo, "1"))
                .thenReturn(Set.of(contact));
        String genderFilterResult = operationStateProcessor.processState(user, "1");

        // Проверка
        Assert.assertEquals(State.NONE, userStateInfo.getState());
        Assert.assertEquals("""
                Имя: testContact
                Номер телефона: 799999
                Возраст: 30
                Пол: Мужской
                Заблокирован: Нет""", genderFilterResult);

        // Действие: фильтрация по заблокированным контактам
        userStateInfo.setState(State.FILTER_BLOCK);
        Mockito.when(filterService.filterByUserStateAndKind(user, userStateInfo, "1"))
                .thenReturn(Set.of());
        String blockFilterResult = operationStateProcessor.processState(user, "1");

        // Проверка
        Assert.assertEquals(State.NONE, userStateInfo.getState());
        Assert.assertEquals("Нет данных", blockFilterResult);
    }

    /**
     * Обработка состояния по сортировке контактов (выбор действия)
     * <ol>
     *     <li>По имени</li>
     *     <li>По возрасту</li>
     *     <li>Выбор некорректного действия</li>
     * </ol>
     */
    @Test
    public void handleSortKindTest() {
        // Подготовка
        StateInfo userStateInfo = new StateInfo();
        userStateInfo.setState(State.SORT_KIND);
        Mockito.when(userStateService.getUserState(user)).thenReturn(userStateInfo);

        // Действие: выбор сортировки по имени
        String nameSortResult = operationStateProcessor.processState(user, "1");

        // Проверка
        Assert.assertEquals(State.SORT_NAME, userStateInfo.getState());
        Assert.assertEquals("""
                Выберите режим:
                1. По возрастанию (А-Я/0-9)
                2. По убыванию (Я-А/9-0)""", nameSortResult);

        // Действие: выбор сортировки по возрасту
        userStateInfo.setState(State.SORT_KIND);
        String ageSortResult = operationStateProcessor.processState(user, "2");

        // Проверка
        Assert.assertEquals(State.SORT_AGE, userStateInfo.getState());
        Assert.assertEquals("""
                Выберите режим:
                1. По возрастанию (А-Я/0-9)
                2. По убыванию (Я-А/9-0)""", ageSortResult);

        // Действие: выбор некорректного аргумента действия
        userStateInfo.setState(State.SORT_KIND);
        String filterError = operationStateProcessor.processState(user, "5143");

        // Проверка
        Assert.assertEquals(State.SORT_KIND, userStateInfo.getState());
        Assert.assertEquals("Введен неверный параметр", filterError);
    }

    /**
     * Обработка состояния по получению контактов в результате сортировки
     * <ol>
     *     <li>По имени (возрастание)</li>
     *     <li>По возрасту (убывание)</li>
     * </ol>
     */
    @Test
    public void handleSortByArgumentTest() {
        // Подготовка
        StateInfo userStateInfo = new StateInfo();
        userStateInfo.setState(State.SORT_NAME);
        Mockito.when(userStateService.getUserState(user)).thenReturn(userStateInfo);
        Contact firstContact = new Contact("testIdA", "a");
        firstContact.setAge(1);
        Contact secondContact = new Contact("testIdB", "b");
        secondContact.setAge(2);

        // Действие: сортировка по имени (возрастание)
        Set<Contact> nameSortContacts = new LinkedHashSet<>();
        nameSortContacts.add(firstContact);
        nameSortContacts.add(secondContact);
        Mockito.when(sortService.sortByUserStateAndKind(user, userStateInfo, "1")).thenReturn(nameSortContacts);
        String nameSortResult = operationStateProcessor.processState(user, "1");

        // Проверка
        Assert.assertEquals(State.NONE, userStateInfo.getState());
        Assert.assertEquals("""
                 Имя: a
                 Номер телефона:\s
                 Возраст: 1
                 Пол:\s
                 Заблокирован: Нет
                 
                 Имя: b
                 Номер телефона:\s
                 Возраст: 2
                 Пол:\s
                 Заблокирован: Нет""", nameSortResult);

        // Действие: сортировка по возрасту (убывание)
        Set<Contact> ageSortContacts = new LinkedHashSet<>();
        ageSortContacts.add(secondContact);
        ageSortContacts.add(firstContact);
        userStateInfo.setState(State.SORT_AGE);
        Mockito.when(sortService.sortByUserStateAndKind(user, userStateInfo, "2")).thenReturn(ageSortContacts);
        String ageSortResult = operationStateProcessor.processState(user, "2");

        // Проверка
        Assert.assertEquals(State.NONE, userStateInfo.getState());
        Assert.assertEquals("""
                Имя: b
                Номер телефона:\s
                Возраст: 2
                Пол:\s
                Заблокирован: Нет
                 
                Имя: a
                Номер телефона:\s
                Возраст: 1
                Пол:\s
                Заблокирован: Нет""", ageSortResult);
    }

}
