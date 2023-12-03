package ru.anykeyers.services;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.Group;
import ru.anykeyers.domain.User;
import ru.anykeyers.processors.states.domain.State;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.repositories.GroupRepository;
import ru.anykeyers.services.impl.GroupServiceImpl;

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
     */
    @Test
    public void addGroupTest() {
        // Подготовка
        User user = new User("testUser");
        String groupName = "newGroup";

        // Действие
        Mockito.when(groupRepository.existsByUsernameAndName(user.getUsername(), groupName)).thenReturn(false);
        String result = groupService.addGroup(user, groupName);

        // Проверка
        Assert.assertEquals("Группа 'newGroup' сохранена", result);
        Mockito.verify(groupRepository, Mockito.times(1)).saveOrUpdate(Mockito.any(Group.class));
    }

    /**
     * Тестирование изменения состояния группы
     * <ol>
     *     <li>Изменение название группы</li>
     *     <li>Добавления контакта в группу</li>
     *     <li>Удаление контакта из группы</li>
     * </ol>
     */
    @Test
    public void editGroupTest() {
        // Подготовка
        User user = new User("testUser");
        Group group = new Group(user.getUsername(), "name");
        Contact contact = new Contact(user.getUsername(), "testContact");

        // Действие: изменение названия группы
        user.setState(State.EDIT_GROUP_NAME);
        user.setGroupNameToEdit(group.getName());
        String newValue = "newName";
        Mockito.when(groupRepository.findByUsernameAndName(user.getUsername(), user.getGroupNameToEdit())).thenReturn(group);
        String editNameResult = groupService.editGroup(user, newValue);

        // Проверка: изменение названия группы
        Assert.assertEquals("Группа 'name' успешно изменена", editNameResult);
        Assert.assertEquals(newValue, group.getName());
        Mockito.verify(groupRepository, Mockito.times(1)).saveOrUpdate(group);

        // Действие: добавление контакта в группу
        user.setState(State.EDIT_GROUP_ADD_CONTACT);
        user.setGroupNameToEdit(group.getName());
        Mockito.when(groupRepository.findByUsernameAndName(user.getUsername(), user.getGroupNameToEdit())).thenReturn(group);
        Mockito.when(contactRepository.findByUsernameAndName(user.getUsername(), contact.getName())).thenReturn(contact);
        String editGroupAddContact = groupService.editGroup(user, contact.getName());

        // Проверка: добавление контакта в группу
        Assert.assertEquals(1, group.getContacts().size());
        Assert.assertEquals("Группа 'newName' успешно изменена", editGroupAddContact);
        Mockito.verify(groupRepository, Mockito.times(2)).saveOrUpdate(group);

        // Действие: удаление контакта из группы
        user.setState(State.EDIT_GROUP_DELETE_CONTACT);
        user.setGroupNameToEdit(group.getName());
        Mockito.when(groupRepository.findByUsernameAndName(user.getUsername(), user.getGroupNameToEdit())).thenReturn(group);
        Mockito.when(contactRepository.findByUsernameAndName(user.getUsername(), contact.getName())).thenReturn(contact);
        String editGroupDeleteContact = groupService.editGroup(user, contact.getName());

        // Проверка: удаление контакта из группы
        Assert.assertEquals(0, group.getContacts().size());
        Assert.assertEquals("Группа 'newName' успешно изменена", editGroupDeleteContact);
        Mockito.verify(groupRepository, Mockito.times(3)).saveOrUpdate(group);
    }

    /**
     * Тестирование удаления группы
     */
    @Test
    public void deleteGroupTest() {
        // Подготовка
        User user = new User("testUser");
        String groupName = "testGroup";
        Group groupToDelete = new Group(user.getUsername(), groupName);

        // Действие
        Mockito.when(groupRepository.findByUsernameAndName(user.getUsername(), groupName)).thenReturn(groupToDelete);
        String result = groupService.deleteGroup(user, groupName);

        // Проверка
        Assert.assertEquals("Группа 'testGroup' успешно удалена", result);
        Mockito.verify(groupRepository, Mockito.times(1)).delete(groupToDelete);
    }

}