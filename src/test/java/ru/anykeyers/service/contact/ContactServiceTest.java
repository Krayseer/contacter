package ru.anykeyers.service.contact;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.anykeyers.domain.FileFormat;
import ru.anykeyers.domain.Gender;
import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.exception.BadArgumentException;
import ru.anykeyers.exception.InvalidNumberFormat;
import ru.anykeyers.exception.contact.ContactAlreadyExistsException;
import ru.anykeyers.exception.contact.ContactNotExistsException;
import ru.anykeyers.service.impl.contact.import_export.FileServiceFactory;
import ru.anykeyers.processor.state.domain.State;
import ru.anykeyers.processor.state.domain.kinds.sort.SortDirectionKind;
import ru.anykeyers.repository.ContactRepository;
import ru.anykeyers.service.ContactService;
import ru.anykeyers.service.FileService;
import ru.anykeyers.service.impl.contact.ContactServiceImpl;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Тесты для сервиса {@link ContactService}
 */
@RunWith(MockitoJUnitRunner.class)
public class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private FileService<Contact> fileService;

    @Mock
    private FileServiceFactory fileServiceFactory;

    @InjectMocks
    private ContactServiceImpl contactService;

    private Contact firstContact;

    private Contact secondContact;

    private User user;

    @Before
    public void setUp() {
        user = new User("testUser");

        firstContact = new Contact("testId", "testFirst");
        firstContact.setPhoneNumber("798");
        firstContact.setAge(15);
        firstContact.setGender(Gender.MAN);

        secondContact = new Contact("testId2", "testSecond");
        secondContact.setPhoneNumber("799");
        secondContact.setAge(25);
        secondContact.setGender(Gender.WOMAN);
        secondContact.setBlocked(true);
    }

    /**
     * Проверка существования контакта у пользователя
     */
    @Test
    public void existsContactTest() {
        // Подготовка
        User user = new User("testUser");
        String contactName = "existingContact";
        String nonExistsContactName = "non-exists";

        // Действие: проверка несуществующего пользователя
        Mockito.when(contactRepository.existsByUsernameAndName(user.getUsername(), nonExistsContactName))
                .thenReturn(false);
        boolean nonExistsResult = contactService.existsContact(user, nonExistsContactName);

        // Проверка
        Assert.assertFalse(nonExistsResult);
        Mockito.verify(contactRepository, Mockito.times(1))
                .existsByUsernameAndName(user.getUsername(), nonExistsContactName);

        // Действие: проверка существующего пользователя
        Mockito.when(contactRepository.existsByUsernameAndName(user.getUsername(), contactName))
                .thenReturn(true);
        boolean existsResult = contactService.existsContact(user, contactName);

        // Проверка
        Assert.assertTrue(existsResult);
        Mockito.verify(contactRepository, Mockito.times(1))
                .existsByUsernameAndName(user.getUsername(), contactName);
    }

    /**
     * Тестирование добавления контакта пользователю
     * <ol>
     *     <li>Добавление контакта уже с существующим именем</li>
     *     <li>Добавление нового контакта</li>
     * </ol>
     */
    @Test
    public void addContactTest() {
        // Подготовка
        User user = new User("testUser");
        String contactName = "newContact";

        // Действие: добавление контакта уже с существующим именем
        Mockito.when(contactRepository.existsByUsernameAndName(user.getUsername(), contactName)).thenReturn(true);

        // Проверка
        Exception addContactException = Assert.assertThrows(
                ContactAlreadyExistsException.class, () -> contactService.addContact(user, contactName)
        );
        Assert.assertEquals("Контакт 'newContact' уже существует", addContactException.getMessage());
        Mockito.verify(contactRepository, Mockito.never()).saveOrUpdate(Mockito.any(Contact.class));

        // Действие: добавление контакта с новым именем
        Mockito.when(contactRepository.existsByUsernameAndName(user.getUsername(), contactName)).thenReturn(false);
        contactService.addContact(user, contactName);

        // Проверка
        Mockito.verify(contactRepository, Mockito.times(1)).saveOrUpdate(Mockito.any(Contact.class));
    }

    /**
     * Тестирование редактирования контакта пользователя
     * <ol>
     *     <li>Изменение имени</li>
     *     <li>
     *         Изменение номера телефона
     *         <ul>
     *             <li>Ввод некорректного номера телефона</li>
     *             <li>Ввод корректного номера телефона</li>
     *         </ul>
     *     </li>
     *     <li>
     *         Изменение возраста
     *         <ul>
     *             <li>Ввод некорректного возраста</li>
     *             <li>Ввод корректного возраста</li>
     *         </ul>
     *     </li>
     *     <li>
     *         Изменение пола
     *         <ul>
     *             <li>Выбор некорректного пункта изменения пола</li>
     *             <li>Выбор корректного пункта изменения пола</li>
     *         </ul>
     *     </li>
     *     <li>
     *         Изменение блокировки
     *         <ul>
     *             <li>Выбор некорректного пункта изменения блокировки</li>
     *             <li>Выбор корректного пункта изменения блокировки</li>
     *         </ul>
     *     </li>
     * </ol>
     */
    @Test
    public void editContactTest() {
        // Подготовка
        User user = new User("testUser");
        StateInfo userStateInfo = new StateInfo();
        String contactName = "testContact";
        String nonExistsContactName = "nonExists";
        Contact contact = new Contact(user.getUsername(), contactName);

        // Действие: изменение несуществующего контакта
        Mockito.lenient().when(contactRepository.getByUsernameAndName(user.getUsername(), nonExistsContactName))
                .thenReturn(Optional.empty());

        // Проверка
        userStateInfo.setEditInfo(nonExistsContactName);
        Exception nonExistsContactException = Assert.assertThrows(
                ContactNotExistsException.class, () -> contactService.editContact(user, userStateInfo, nonExistsContactName)
        );
        Assert.assertEquals("Не удалось найти контакт 'nonExists'", nonExistsContactException.getMessage());
        Mockito.verify(contactRepository, Mockito.never()).saveOrUpdate(contact);

        // Действие: изменение имени контакта
        userStateInfo.setState(State.EDIT_CONTACT_NAME);
        String newValue = "newName";
        userStateInfo.setEditInfo(contact.getName());
        Mockito.when(contactRepository.getByUsernameAndName(user.getUsername(), userStateInfo.getEditInfo()))
                .thenReturn(Optional.of(contact));
        contactService.editContact(user, userStateInfo, newValue);

        // Проверка
        Assert.assertEquals(newValue, contact.getName());
        Mockito.verify(contactRepository, Mockito.times(1)).saveOrUpdate(contact);

        // Действие: изменение номера телефона контакта
        userStateInfo.setState(State.EDIT_CONTACT_PHONE);
        String invalidPhoneNumber = "invalid";
        String newPhoneNumber = "79797979";
        userStateInfo.setEditInfo(contact.getName());
        Mockito.when(contactRepository.getByUsernameAndName(user.getUsername(), userStateInfo.getEditInfo()))
                .thenReturn(Optional.of(contact));

        // Проверка: ввод некорректных символов
        Exception invalidFormatExceptionName = Assert.assertThrows(
                InvalidNumberFormat.class, () -> contactService.editContact(user, userStateInfo, invalidPhoneNumber)
        );
        Assert.assertEquals("Нужно ввести числовое значение", invalidFormatExceptionName.getMessage());
        Mockito.verify(contactRepository, Mockito.times(1)).saveOrUpdate(contact);

        // Проверка: ввод корректных символов
        contactService.editContact(user, userStateInfo, newPhoneNumber);
        Mockito.verify(contactRepository, Mockito.times(2)).saveOrUpdate(contact);

        // Действие: изменение возраста контакта
        userStateInfo.setState(State.EDIT_CONTACT_AGE);
        String invalidAge = "invalid";
        String age = "79797979";
        userStateInfo.setEditInfo(contact.getName());
        Mockito.when(contactRepository.getByUsernameAndName(user.getUsername(), userStateInfo.getEditInfo()))
                .thenReturn(Optional.of(contact));

        // Проверка: ввод некорректных символов
        Exception invalidFormatExceptionPhone = Assert.assertThrows(
                InvalidNumberFormat.class, () -> contactService.editContact(user, userStateInfo, invalidAge)
        );
        Assert.assertEquals("Нужно ввести числовое значение", invalidFormatExceptionPhone.getMessage());
        Mockito.verify(contactRepository, Mockito.times(2)).saveOrUpdate(contact);

        // Проверка: ввод корректных символов
        contactService.editContact(user, userStateInfo, age);
        Mockito.verify(contactRepository, Mockito.times(3)).saveOrUpdate(contact);

        // Действие: изменение пола контакта
        userStateInfo.setState(State.EDIT_CONTACT_GENDER);
        String invalidGenderKind = "1234";
        String manGenderKind = "1";
        userStateInfo.setEditInfo(contact.getName());
        Mockito.when(contactRepository.getByUsernameAndName(user.getUsername(), userStateInfo.getEditInfo()))
                .thenReturn(Optional.of(contact));

        // Проверка: ввод некорректного аргумента
        Exception genderEditException = Assert.assertThrows(
                BadArgumentException.class, () -> contactService.editContact(user, userStateInfo, invalidGenderKind)
        );
        Assert.assertEquals("Введен неверный параметр", genderEditException.getMessage());
        Mockito.verify(contactRepository, Mockito.times(3)).saveOrUpdate(contact);

        // Проверка: ввод корректного аргумента
        contactService.editContact(user, userStateInfo, manGenderKind);
        Assert.assertEquals(Gender.MAN, contact.getGender());
        Mockito.verify(contactRepository, Mockito.times(4)).saveOrUpdate(contact);

        // Действие: изменение блокировки контакта
        userStateInfo.setState(State.EDIT_CONTACT_BLOCK);
        String invalidBlockKind = "1234";
        String blockKind = "1";
        userStateInfo.setEditInfo(contact.getName());
        Mockito.when(contactRepository.getByUsernameAndName(user.getUsername(), userStateInfo.getEditInfo()))
                .thenReturn(Optional.of(contact));

        // Проверка: ввод некорректного аргумента
        Exception blockEditException= Assert.assertThrows(
                BadArgumentException.class, () -> contactService.editContact(user, userStateInfo, invalidBlockKind)
        );
        Assert.assertEquals("Введен неверный параметр", blockEditException.getMessage());
        Mockito.verify(contactRepository, Mockito.times(4)).saveOrUpdate(contact);

        // Проверка: ввод корректного аргумента
        contactService.editContact(user, userStateInfo, blockKind);
        Assert.assertTrue(contact.isBlocked());
        Mockito.verify(contactRepository, Mockito.times(5)).saveOrUpdate(contact);
    }

    /**
     * Тестирование удаления контакта
     * <ol>
     *     <li>Удаление несуществующего контакта</li>
     *     <li>Удаление существующего контакта</li>
     * </ol>
     */
    @Test
    public void deleteContactTest() {
        // Подготовка
        User user = new User("testUser");
        String contactName = "contactToDelete";
        String nonExistsContactName = "nonExists";
        Contact contact = new Contact(user.getUsername(), contactName);

        // Действие: удаление несуществующего контакта
        Mockito.when(contactRepository.getByUsernameAndName(user.getUsername(), nonExistsContactName))
                .thenReturn(Optional.empty());

        // Проверка
        Exception nonExistsContactException = Assert.assertThrows(
                ContactNotExistsException.class, () -> contactService.deleteContact(user, nonExistsContactName)
        );
        Assert.assertEquals("Не удалось найти контакт 'nonExists'", nonExistsContactException.getMessage());
        Mockito.verify(contactRepository, Mockito.never()).delete(contact);

        // Действие: удаление существующего контакта
        Mockito.when(contactRepository.getByUsernameAndName(user.getUsername(), contactName))
                .thenReturn(Optional.of(contact));
        contactService.deleteContact(user, contactName);

        // Проверка
        Mockito.verify(contactRepository, Mockito.times(1)).delete(contact);
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
        Set<Contact> getContactsByNonExistsName = contactService.searchByArgument(user, userStateInfo, "kkk");

        // Проверка
        Assert.assertEquals(0, getContactsByNonExistsName.size());
        Assert.assertEquals(Set.of(), getContactsByNonExistsName);

        // Действие: поиск по полному вхождению имени
        Set<Contact> getContactsByFullName = contactService.searchByArgument(user, userStateInfo, "testFirst");

        // Проверка
        Assert.assertEquals(1, getContactsByFullName.size());
        Assert.assertEquals(Set.of(firstContact), getContactsByFullName);

        // Действие: поиск по вхождению подстроки имени
        Set<Contact> getContactsByContains = contactService.searchByArgument(user, userStateInfo, "test");

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
        Set<Contact> getContactsByNonExistsName = contactService.searchByArgument(user, userStateInfo, "888");

        // Проверка
        Assert.assertEquals(0, getContactsByNonExistsName.size());
        Assert.assertEquals(Set.of(), getContactsByNonExistsName);

        // Действие: получение контактов по полному вхождению номера телефона
        Set<Contact> getContactsByFullName = contactService.searchByArgument(user, userStateInfo, "798");

        // Проверка
        Assert.assertEquals(1, getContactsByFullName.size());
        Assert.assertEquals(Set.of(firstContact), getContactsByFullName);

        // Действие: получение контактов по вхождению подстроки номера телефона
        Set<Contact> getContactsByContains = contactService.searchByArgument(user, userStateInfo, "79");

        // Проверка
        Assert.assertEquals(2, getContactsByContains.size());
        Assert.assertEquals(Set.of(firstContact, secondContact), getContactsByContains);
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

        // Проверка: фильтрация по возрасту старше 18
        Set<Contact> contactsGreaterThan =
                contactService.filterByKind(user, userStateInfo, "1");
        Assert.assertEquals(1, contactsGreaterThan.size());
        Assert.assertEquals(Set.of(secondContact), contactsGreaterThan);

        // Проверка: фильтрация по возрасту меньше 18
        Set<Contact> contactsLessThan =
                contactService.filterByKind(user, userStateInfo, "2");
        Assert.assertEquals(1, contactsLessThan.size());
        Assert.assertEquals(Set.of(firstContact), contactsLessThan);

        // Проверка: фильтрация по возрасту равному 15
        userStateInfo.setEditInfo("15");
        Set<Contact> contactsEquals =
                contactService.filterByKind(user, userStateInfo, "3");
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

        // Проверка: фильтрация по мужчинам
        Set<Contact> manSet = contactService.filterByKind(user, userStateInfo, "1");
        Assert.assertEquals(1, manSet.size());
        Assert.assertEquals(Set.of(firstContact), manSet);

        // Проверка: фильтрация по женщинам
        Set<Contact> womanSet = contactService.filterByKind(user, userStateInfo, "2");
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

        // Проверка: фильтрация по заблокированным
        Set<Contact> blockSet =
                contactService.filterByKind(user, userStateInfo, "1");
        Assert.assertEquals(1, blockSet.size());
        Assert.assertEquals(Set.of(secondContact), blockSet);

        // Проверка: фильтрация по не заблокированным
        Set<Contact> unblockSet =
                contactService.filterByKind(user, userStateInfo, "2");
        Assert.assertEquals(1, unblockSet.size());
        Assert.assertEquals(Set.of(firstContact), unblockSet);
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

        // Действие: сортировка по возрастанию
        List<Contact> sortedContacts = new LinkedList<>(
                contactService.sortByKind(user, userStateInfo, SortDirectionKind.ASCENDING)
        );

        // Проверка
        List<Contact> expectedSortedContacts = new LinkedList<>();
        expectedSortedContacts.add(firstContact);
        expectedSortedContacts.add(secondContact);
        Assert.assertEquals(expectedSortedContacts, sortedContacts);

        // Действие: сортировка по убыванию
        List<Contact> sortedContactsReverse = new LinkedList<>(
                contactService.sortByKind(user, userStateInfo, SortDirectionKind.DESCENDING)
        );

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
        List<Contact> sortedContacts = new LinkedList<>(
                contactService.sortByKind(user, userStateInfo, SortDirectionKind.ASCENDING)
        );

        // Проверка
        List<Contact> expectedSortedContacts = new LinkedList<>();
        expectedSortedContacts.add(firstContact);
        expectedSortedContacts.add(secondContact);
        Assert.assertEquals(expectedSortedContacts, sortedContacts);

        // Действие: сортировка по убыванию
        List<Contact> sortedContactsReverse = new LinkedList<>(
                contactService.sortByKind(user, userStateInfo, SortDirectionKind.DESCENDING)
        );

        // Проверка
        List<Contact> expectedSortedContactsReverse = new LinkedList<>();
        expectedSortedContactsReverse.add(secondContact);
        expectedSortedContactsReverse.add(firstContact);
        Assert.assertEquals(expectedSortedContactsReverse, sortedContactsReverse);
    }

    /**
     * Тестирование импорта контактов из файла
     */
    @Test
    public void importDataTest() {
        // Подготовка
        File file = new File("src/test/resources/contacts.txt");

        // Действие
        Mockito.lenient()
                .when(fileServiceFactory.getServiceByFormat(Mockito.any(FileFormat.class)))
                .thenReturn(fileService);
        contactService.importContacts(user, file);

        // Проверка
        Mockito.verify(contactRepository, Mockito.times(4)).saveOrUpdate(Mockito.any());
    }

}