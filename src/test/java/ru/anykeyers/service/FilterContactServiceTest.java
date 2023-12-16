package ru.anykeyers.service;

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
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.exception.BadArgumentException;
import ru.anykeyers.processor.state.domain.State;
import ru.anykeyers.repository.ContactRepository;
import ru.anykeyers.service.impl.FilterContactServiceImpl;

import java.util.Set;

/**
 * Тесты для сервиса {@link FilterContactService}
 */
@RunWith(MockitoJUnitRunner.class)
public class FilterContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private FilterContactServiceImpl filterContactService;

    private Contact firstContact;

    private Contact secondContact;

    private User user;

    @Before
    public void setUp() {
        user = new User("testUser");

        firstContact = new Contact("testId", "testFirst");
        firstContact.setAge(15);
        firstContact.setGender(Gender.MAN);

        secondContact = new Contact("testId2", "testSecond");
        secondContact.setAge(25);
        secondContact.setGender(Gender.WOMAN);
        secondContact.setBlocked(true);
    }

    /**
     * Тестирование фильтрации контактов по возрасту
     * <ol>
     *     <li>Фильтрация по возрасту с некорректным аргументом</li>
     *     <li>Фильтрация по возрасту старше</li>
     *     <li>Фильтрация по возрасту младше</li>
     *     <li>Фильтрация по возрасту равному</li>
     * </ol>
     */
    @Test
    public void filterAgeTest() {
        // Подготовка
        StateInfo userStateInfo = new StateInfo();
        userStateInfo.setState(State.FILTER_AGE_KIND);
        userStateInfo.setEditInfo("18");

        // Действие
        Mockito.when(contactRepository.findByUsername(user.getUsername()))
                .thenReturn(Set.of(firstContact, secondContact));

        // Проверка: фильтрация при неверном введенном аргументе
        Exception badArgumentException = Assert.assertThrows(
                BadArgumentException.class, () -> filterContactService.filterByUserStateAndKind(user, userStateInfo, "222")
        );
        Assert.assertEquals("Введен неверный параметр", badArgumentException.getMessage());

        // Проверка: фильтрация по возрасту старше 18
        Set<Contact> contactsGreaterThan = filterContactService.filterByUserStateAndKind(user, userStateInfo, "1");
        Assert.assertEquals(1, contactsGreaterThan.size());
        Assert.assertEquals(Set.of(secondContact), contactsGreaterThan);

        // Проверка: фильтрация по возрасту меньше 18
        Set<Contact> contactsLessThan = filterContactService.filterByUserStateAndKind(user, userStateInfo, "2");
        Assert.assertEquals(1, contactsLessThan.size());
        Assert.assertEquals(Set.of(firstContact), contactsLessThan);

        // Проверка: фильтрация по возрасту равному 15
        userStateInfo.setEditInfo("15");
        Set<Contact> contactsEquals = filterContactService.filterByUserStateAndKind(user, userStateInfo, "3");
        Assert.assertEquals(1, contactsEquals.size());
        Assert.assertEquals(Set.of(firstContact), contactsEquals);
    }

    /**
     * Тестирование фильтрации контактов по полу
     * <ol>
     *     <li>Фильтрация по полу с некорректным аргументом</li>
     *     <li>Фильтрация по мужскому полу</li>
     *     <li>Фильтрация по женскому полу</li>
     * </ol>
     */
    @Test
    public void filterGenderTest() {
        // Подготовка
        StateInfo userStateInfo = new StateInfo();
        userStateInfo.setState(State.FILTER_GENDER);

        // Действие
        Mockito.when(contactRepository.findByUsername(user.getUsername()))
                .thenReturn(Set.of(firstContact, secondContact));

        // Проверка: фильтрация при неверном введенном аргументе
        Exception badArgumentException = Assert.assertThrows(
                BadArgumentException.class, () -> filterContactService.filterByUserStateAndKind(user, userStateInfo, "222")
        );
        Assert.assertEquals("Введен неверный параметр", badArgumentException.getMessage());

        // Проверка: фильтрация по мужчинам
        Set<Contact> manSet = filterContactService.filterByUserStateAndKind(user, userStateInfo, "1");
        Assert.assertEquals(1, manSet.size());
        Assert.assertEquals(Set.of(firstContact), manSet);

        // Проверка: фильтрация по женщинам
        Set<Contact> womanSet = filterContactService.filterByUserStateAndKind(user, userStateInfo, "2");
        Assert.assertEquals(1, womanSet.size());
        Assert.assertEquals(Set.of(secondContact), womanSet);
    }

    /**
     * Тестирование фильтрации контактов по блокировке
     * <ol>
     *     <li>Фильтрация по полу с некорректным аргументом</li>
     *     <li>Фильтрация по заблокированным контактам</li>
     *     <li>Фильтрация по не заблокированным контактам</li>
     * </ol>
     */
    @Test
    public void filterBlockTest() {
        // Подготовка
        StateInfo userStateInfo = new StateInfo();
        userStateInfo.setState(State.FILTER_BLOCK);

        // Действие
        Mockito.when(contactRepository.findByUsername(user.getUsername()))
                .thenReturn(Set.of(firstContact, secondContact));

        // Проверка: фильтрация при неверном введенном аргументе
        Exception badArgumentException = Assert.assertThrows(
                BadArgumentException.class, () -> filterContactService.filterByUserStateAndKind(user, userStateInfo, "222")
        );
        Assert.assertEquals("Введен неверный параметр", badArgumentException.getMessage());

        // Проверка: фильтрация по заблокированным
        Set<Contact> blockSet = filterContactService.filterByUserStateAndKind(user, userStateInfo, "1");
        Assert.assertEquals(1, blockSet.size());
        Assert.assertEquals(Set.of(secondContact), blockSet);

        // Проверка: фильтрация по не заблокированным
        Set<Contact> unblockSet = filterContactService.filterByUserStateAndKind(user, userStateInfo, "2");
        Assert.assertEquals(1, unblockSet.size());
        Assert.assertEquals(Set.of(firstContact), unblockSet);
    }

}
