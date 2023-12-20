package ru.anykeyers.service;

import org.junit.Assert;
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
import ru.anykeyers.exception.contact.ContactNotExistsException;
import ru.anykeyers.exception.group.ContactAlreadyExistsInGroupException;
import ru.anykeyers.exception.group.ContactNotExistsInGroupException;
import ru.anykeyers.exception.group.GroupAlreadyExistsException;
import ru.anykeyers.exception.group.GroupNotExistsException;
import ru.anykeyers.processor.state.domain.State;
import ru.anykeyers.repository.ContactRepository;
import ru.anykeyers.repository.GroupRepository;
import ru.anykeyers.service.impl.GroupServiceImpl;

import java.util.Optional;

/**
 * Тесты для сервиса {@link GroupService}
 */
@RunWith(MockitoJUnitRunner.class)
public class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private GroupServiceImpl groupService;

    /**
     * Тестирование добавления группы пользователю
     * <ol>
     *     <li>Добавление группы уже с существующим именем</li>
     *     <li>Добавление новой группы</li>
     * </ol>
     */
    @Test
    public void addGroupTest() {
        // Подготовка
        User user = new User("testUser");
        String nonExistsGroupName = "non-exists";
        String groupName = "newGroup";

        // Действие: добавление группы уже с существующим названием
        Mockito.when(groupRepository.existsByUsernameAndName(user.getUsername(), nonExistsGroupName)).thenReturn(true);

        // Проверка
        Exception groupAlreadyExistsException = Assert.assertThrows(
                GroupAlreadyExistsException.class, () -> groupService.addGroup(user, nonExistsGroupName)
        );
        Assert.assertEquals("Группа 'non-exists' уже существует", groupAlreadyExistsException.getMessage());
        Mockito.verify(groupRepository, Mockito.never()).saveOrUpdate(Mockito.any(Group.class));

        // Действие: добавление группы с новым названием
        Mockito.lenient().when(contactRepository.existsByUsernameAndName(user.getUsername(), groupName)).thenReturn(false);
        groupService.addGroup(user, groupName);

        // Проверка
        Mockito.verify(groupRepository, Mockito.times(1)).saveOrUpdate(Mockito.any(Group.class));
    }

    /**
     * Тестирование изменения состояния группы
     * <ol>
     *     <li>Изменение названия группы</li>
     *     <li>
     *         Добавления контакта в группу
     *         <ul>
     *             <li>Добавление несуществующего контакта</li>
     *             <li>Добавление уже существующего в группе контакта</li>
     *             <li>Добавление существующего контакта в группу</li>
     *         </ul>
     *     </li>
     *     <li>
     *         Удаление контакта из группы
     *         <ul>
     *             <li>Удаление несуществующего контакта</li>
     *             <li>Удаление существующего контакта в группе</li>
     *             <li>Удаление несуществующего в группе контакта</li>
     *         </ul>
     *     </li>
     * </ol>
     */
    @Test
    public void editGroupTest() {
        // Подготовка
        User user = new User("testUser");
        StateInfo userStateInfo = new StateInfo();
        Group group = new Group(user.getUsername(), "name");
        String contactName = "testContact";
        String nonExistsContactName = "non-exists";
        Contact contact = new Contact(user.getUsername(), contactName);

        // Действие: изменение названия группы
        userStateInfo.setState(State.EDIT_GROUP_NAME);
        userStateInfo.setEditInfo(group.getName());
        String newValue = "newName";
        Mockito.when(groupRepository.getByUsernameAndName(user.getUsername(), userStateInfo.getEditInfo()))
                .thenReturn(Optional.of(group));
        groupService.editGroup(user, userStateInfo, newValue);

        // Проверка
        Assert.assertEquals(newValue, group.getName());
        Mockito.verify(groupRepository, Mockito.times(1)).saveOrUpdate(group);

        // Действие: добавление несуществующего контакта в группу
        userStateInfo.setState(State.EDIT_GROUP_ADD_CONTACT);
        userStateInfo.setEditInfo(group.getName());
        Mockito.when(groupRepository.getByUsernameAndName(user.getUsername(), userStateInfo.getEditInfo())).
                thenReturn(Optional.of(group));
        Mockito.when(contactRepository.getByUsernameAndName(user.getUsername(), nonExistsContactName))
                .thenReturn(Optional.empty());

        // Проверка
        Exception contactNotExistsException = Assert.assertThrows(
                ContactNotExistsException.class, () -> groupService.editGroup(user, userStateInfo, nonExistsContactName)
        );
        Assert.assertEquals("Не удалось найти контакт 'non-exists'", contactNotExistsException.getMessage());
        Mockito.verify(groupRepository, Mockito.times(1)).saveOrUpdate(group);

        // Действие: добавление существующего контакта в группу
        Mockito.when(groupRepository.getByUsernameAndName(user.getUsername(), userStateInfo.getEditInfo())).
                thenReturn(Optional.of(group));
        Mockito.when(contactRepository.getByUsernameAndName(user.getUsername(), contactName))
                .thenReturn(Optional.of(contact));
        groupService.editGroup(user, userStateInfo, contactName);

        // Проверка
        Assert.assertEquals(1, group.getContacts().size());
        Mockito.verify(groupRepository, Mockito.times(2)).saveOrUpdate(group);

        // Проверка: добавление контакта уже существующего в группе
        Assert.assertThrows(
                ContactAlreadyExistsInGroupException.class, () -> groupService.editGroup(user, userStateInfo, contactName)
        );
        Assert.assertEquals(1, group.getContacts().size());
        Mockito.verify(groupRepository, Mockito.times(2)).saveOrUpdate(group);

        // Действие: удаление несуществующего контакта из группы
        userStateInfo.setState(State.EDIT_GROUP_DELETE_CONTACT);
        userStateInfo.setEditInfo(group.getName());
        Mockito.when(groupRepository.getByUsernameAndName(user.getUsername(), userStateInfo.getEditInfo())).
                thenReturn(Optional.of(group));
        Mockito.when(contactRepository.getByUsernameAndName(user.getUsername(), nonExistsContactName))
                .thenReturn(Optional.empty());

        // Проверка
        Exception contactNonExistsException = Assert.assertThrows(
                ContactNotExistsException.class, () -> groupService.editGroup(user, userStateInfo, nonExistsContactName)
        );
        Assert.assertEquals("Не удалось найти контакт 'non-exists'", contactNonExistsException.getMessage());
        Assert.assertEquals(1, group.getContacts().size());
        Mockito.verify(groupRepository, Mockito.times(2)).saveOrUpdate(group);

        // Действие: удаление контакта из группы
        Mockito.when(groupRepository.getByUsernameAndName(user.getUsername(), userStateInfo.getEditInfo())).
                thenReturn(Optional.of(group));
        Mockito.when(contactRepository.getByUsernameAndName(user.getUsername(), nonExistsContactName))
                .thenReturn(Optional.of(contact));
        groupService.editGroup(user, userStateInfo, contactName);

        // Проверка
        Assert.assertEquals(0, group.getContacts().size());
        Mockito.verify(groupRepository, Mockito.times(3)).saveOrUpdate(group);

        // Действие: удаление несуществующего в группе контакта
        Mockito.when(groupRepository.getByUsernameAndName(user.getUsername(), userStateInfo.getEditInfo())).
                thenReturn(Optional.of(group));
        Mockito.lenient().when(contactRepository.getByUsernameAndName(user.getUsername(), nonExistsContactName))
                .thenReturn(Optional.of(contact));

        // Проверка
        Exception contactNotExistsInGroupException = Assert.assertThrows(
                ContactNotExistsInGroupException.class, () -> groupService.editGroup(user, userStateInfo, contactName)
        );
        Assert.assertEquals("Контакта 'testContact' нет в группе", contactNotExistsInGroupException.getMessage());
        Mockito.verify(groupRepository, Mockito.times(3)).saveOrUpdate(group);
    }

    /**
     * Тестирование удаления группы
     * <ol>
     *    <li>Удаление существующей группы</li>
     *    <li>Удаление несуществующей группы</li>
     * </ol>
     */
    @Test
    public void deleteGroupTest() {
        // Подготовка
        User user = new User("testUser");
        String groupName = "testGroup";
        Group groupToDelete = new Group(user.getUsername(), groupName);

        // Действие: успешное удаление группы
        Mockito.when(groupRepository.getByUsernameAndName(user.getUsername(), groupName))
                .thenReturn(Optional.of(groupToDelete));
        groupService.deleteGroup(user, groupName);

        // Проверка
        Mockito.verify(groupRepository, Mockito.times(1)).delete(groupToDelete);

        // Действие: удаление несуществующей группы
        Mockito.when(groupRepository.getByUsernameAndName(user.getUsername(), groupName))
                .thenReturn(Optional.empty());

        // Проверка
        Exception groupNotExistsException = Assert.assertThrows(
                GroupNotExistsException.class, () -> groupService.deleteGroup(user, groupName)
        );
        Assert.assertEquals("Не удалось найти группу 'testGroup'", groupNotExistsException.getMessage());
        Mockito.verify(groupRepository, Mockito.times(1)).delete(groupToDelete);
    }

    /**
     * Тестирование получения контактов в несуществующей группе
     */
    @Test
    public void getAllGroupContactsTest() {
        // Подготовка
        User user = new User("testUser");
        String nonExistsGroupName = "non-exists";

        // Действие
        Mockito.when(groupRepository.getByUsernameAndName(user.getUsername(), nonExistsGroupName))
                .thenReturn(Optional.empty());

        // Проверка
        Exception groupNotExistsException = Assert.assertThrows(
                GroupNotExistsException.class, () -> groupService.findAllGroupContacts(user, nonExistsGroupName)
        );
        Assert.assertEquals("Не удалось найти группу 'non-exists'", groupNotExistsException.getMessage());
    }

}