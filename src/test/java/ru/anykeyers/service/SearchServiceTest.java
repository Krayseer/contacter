package ru.anykeyers.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.domain.entity.Group;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.exception.group.GroupNotExistsException;
import ru.anykeyers.processor.state.domain.State;
import ru.anykeyers.repository.ContactRepository;
import ru.anykeyers.repository.GroupRepository;
import ru.anykeyers.service.impl.SearchServiceImpl;

import java.util.Optional;
import java.util.Set;

/**
 * Тесты для сервиса {@link SearchService}
 */
@RunWith(MockitoJUnitRunner.class)
public class SearchServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private SearchServiceImpl searchService;

    private Contact firstContact;

    private Contact secondContact;

    private User user;

    @Before
    public void setUp() {
        user = new User("testUser");

        firstContact = new Contact("testId", "testFirst");
        firstContact.setPhoneNumber("798");

        secondContact = new Contact("testId2", "testSecond");
        secondContact.setPhoneNumber("799");
    }

    /**
     * Тестирование поиска контактов по имени
     * <ol>
     *     <li>По несуществующему имени</li>
     *     <li>По вхождении подстроки имени</li>
     *     <li>По полному вхождению имени</li>
     * </ol>
     */
    @Test
    public void findContactsByNameTest() {
        // Подготовка
        StateInfo userStateInfo = new StateInfo();
        userStateInfo.setState(State.SEARCH_NAME);

        // Действие: поиск по несуществующему имени
        Mockito.when(contactRepository.findByUsername(user.getUsername()))
                .thenReturn(Set.of(firstContact, secondContact));
        Set<Contact> getContactsByNonExistsName = searchService.findContactsByArgument(user, userStateInfo, "kkk");

        // Проверка
        Assert.assertEquals(0, getContactsByNonExistsName.size());
        Assert.assertEquals(Set.of(), getContactsByNonExistsName);

        // Действие: поиск по полному вхождению имени
        Set<Contact> getContactsByFullName = searchService.findContactsByArgument(user, userStateInfo, "testFirst");

        // Проверка
        Assert.assertEquals(1, getContactsByFullName.size());
        Assert.assertEquals(Set.of(firstContact), getContactsByFullName);

        // Действие: поиск по вхождению подстроки имени
        Set<Contact> getContactsByContains = searchService.findContactsByArgument(user, userStateInfo, "test");

        // Проверка
        Assert.assertEquals(2, getContactsByContains.size());
        Assert.assertEquals(Set.of(firstContact, secondContact), getContactsByContains);
    }

    /**
     * Тестирование поиска контактов по номеру телефона
     * <ol>
     *     <li>По несуществующему номеру телефона</li>
     *     <li>По вхождению подстроки номера телефона</li>
     *     <li>По полному вхождению номера телефона</li>
     * </ol>
     */
    @Test
    public void findContactsByPhoneNumberTest() {
        // Подготовка
        StateInfo userStateInfo = new StateInfo();
        userStateInfo.setState(State.SEARCH_PHONE);
        // Действие: получение контактов по несуществующему номеру телефона
        Mockito.when(contactRepository.findByUsername(user.getUsername()))
                .thenReturn(Set.of(firstContact, secondContact));
        Set<Contact> getContactsByNonExistsName = searchService.findContactsByArgument(user, userStateInfo, "888");

        // Проверка
        Assert.assertEquals(0, getContactsByNonExistsName.size());
        Assert.assertEquals(Set.of(), getContactsByNonExistsName);

        // Действие: получение контактов по полному вхождению номера телефона
        Set<Contact> getContactsByFullName = searchService.findContactsByArgument(user, userStateInfo, "798");

        // Проверка
        Assert.assertEquals(1, getContactsByFullName.size());
        Assert.assertEquals(Set.of(firstContact), getContactsByFullName);

        // Действие: получение контактов по вхождению подстроки номера телефона
        Set<Contact> getContactsByContains = searchService.findContactsByArgument(user, userStateInfo, "79");

        // Проверка
        Assert.assertEquals(2, getContactsByContains.size());
        Assert.assertEquals(Set.of(firstContact, secondContact), getContactsByContains);
    }

    /**
     * Тестирование поиска контактов в группе
     * <ol>
     *     <li>Поиск контактов в несуществующей группе</li>
     *     <li>Поиск контактов в существующей группе</li>
     * </ol>
     */
    @Test
    public void findContactsInGroupByNameTest() {
        // Подготовка
        StateInfo userStateInfo = new StateInfo();
        userStateInfo.setState(State.SEARCH_GROUP_CONTACTS_BY_NAME);
        String groupName = "testGroup";
        String nonExistsGroupName = "nonExists";
        Group group = new Group(groupName);
        group.addContactInGroup(firstContact);
        group.addContactInGroup(secondContact);
        userStateInfo.setEditInfo(groupName);

        // Действие: поиск контакта в несуществующей группе
        Mockito.lenient().when(groupRepository.getByUsernameAndName(user.getUsername(), nonExistsGroupName))
                .thenReturn(Optional.empty());

        // Проверка
        Exception groupNotExistsExceptionAssert = Assert.assertThrows(
                GroupNotExistsException.class, () -> searchService.findContactsByArgument(user, userStateInfo, "test")
        );
        Assert.assertEquals("Не удалось найти группу 'testGroup'", groupNotExistsExceptionAssert.getMessage());

        // Действие: поиск контакта в существующей группе
        Mockito.when(groupRepository.getByUsernameAndName(user.getUsername(), groupName))
                .thenReturn(Optional.of(group));
        Set<Contact> contacts = searchService.findContactsByArgument(user, userStateInfo, "test");

        // Проверка
        Assert.assertEquals(2, contacts.size());
        Assert.assertEquals(Set.of(firstContact, secondContact), contacts);
    }

}
