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
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.exception.BadArgumentException;
import ru.anykeyers.processor.state.domain.State;
import ru.anykeyers.repository.ContactRepository;
import ru.anykeyers.service.impl.SortContactServiceImpl;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Тесты для сервиса {@link SearchService}
 */
@RunWith(MockitoJUnitRunner.class)
public class SortContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private SortContactServiceImpl sortContactService;

    private Contact firstContact;

    private Contact secondContact;

    private User user;

    @Before
    public void setUp() {
        user = new User("testUser");

        firstContact = new Contact("testId", "Андрей");
        firstContact.setAge(15);

        secondContact = new Contact("testId2", "Илья");
        secondContact.setAge(25);
    }

    /**
     * Тестирование сортировки контактов по имени
     * <ol>
     *     <li>с некорректным аргументом</li>
     *     <li>по возрастанию</li>
     *     <li>по убыванию</li>
     * </ol>
     */
    @Test
    public void sortContactsByNameTest() {
        // Подготовка
        StateInfo userStateInfo = new StateInfo();
        userStateInfo.setState(State.SORT_NAME);

        // Действие: сортировка при неверном введенном аргументе
        Mockito.when(contactRepository.findByUsername(user.getUsername()))
                .thenReturn(Set.of(firstContact, secondContact));

        // Проверка
        Exception badArgumentException = Assert.assertThrows(
                BadArgumentException.class, () -> sortContactService.sortByUserStateAndKind(user, userStateInfo, "222")
        );
        Assert.assertEquals("Введен неверный параметр", badArgumentException.getMessage());

        // Действие: сортировка по возрастанию
        List<Contact> sortedContacts = new LinkedList<>(sortContactService.sortByUserStateAndKind(user, userStateInfo, "1"));

        // Проверка
        List<Contact> expectedSortedContacts = new LinkedList<>();
        expectedSortedContacts.add(firstContact);
        expectedSortedContacts.add(secondContact);
        Assert.assertEquals(expectedSortedContacts, sortedContacts);

        // Действие: сортировка по убыванию
        List<Contact> sortedContactsReverse = new LinkedList<>(sortContactService.sortByUserStateAndKind(user, userStateInfo, "2"));

        // Проверка
        List<Contact> expectedSortedContactsReverse = new LinkedList<>();
        expectedSortedContactsReverse.add(secondContact);
        expectedSortedContactsReverse.add(firstContact);
        Assert.assertEquals(expectedSortedContactsReverse, sortedContactsReverse);
    }

    /**
     * Тестирование сортировки контактов по возрасту
     * <ol>
     *     <li>с некорректным аргументом</li>
     *     <li>по возрастанию</li>
     *     <li>по убыванию</li>
     * </ol>
     */
    @Test
    public void sortContactsByAgeTest() {
        // Подготовка
        StateInfo userStateInfo = new StateInfo();
        userStateInfo.setState(State.SORT_AGE);

        // Действие: сортировка по возрастанию
        Mockito.when(contactRepository.findByUsername(user.getUsername()))
                .thenReturn(Set.of(firstContact, secondContact));
        List<Contact> sortedContacts = new LinkedList<>(sortContactService.sortByUserStateAndKind(user, userStateInfo, "1"));

        // Проверка
        List<Contact> expectedSortedContacts = new LinkedList<>();
        expectedSortedContacts.add(firstContact);
        expectedSortedContacts.add(secondContact);
        Assert.assertEquals(expectedSortedContacts, sortedContacts);

        // Действие: сортировка по убыванию
        List<Contact> sortedContactsReverse = new LinkedList<>(sortContactService.sortByUserStateAndKind(user, userStateInfo, "2"));

        // Проверка
        List<Contact> expectedSortedContactsReverse = new LinkedList<>();
        expectedSortedContactsReverse.add(secondContact);
        expectedSortedContactsReverse.add(firstContact);
        Assert.assertEquals(expectedSortedContactsReverse, sortedContactsReverse);
    }

}
