package ru.anykeyers.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.Group;
import ru.anykeyers.domain.User;
import ru.anykeyers.processors.states.domain.State;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.repositories.GroupRepository;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Тесты для класса {@link GroupService}
 */
@RunWith(MockitoJUnitRunner.class)
public class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private GroupService groupService;

    /**
     * Тестирование получения всех групп пользователя
     */
    @Test
    public void testGetAllGroups() {
        String username = "testUser";
        Group group = new Group(username, UUID.randomUUID().toString(), "group1", new HashSet<>());
        when(groupRepository.findByUsername(username)).thenReturn(Set.of(group));

        Set<Group> result = groupService.getAllGroups(username);

        assertEquals(1, result.size());
        verify(groupRepository, times(1)).findByUsername(username);
    }

    /**
     * Тестирование получения всех контактов в группе пользователя
     */
    @Test
    public void testGetGroupContacts() {
        String username = "testUser";
        String groupName = "group1";
        Group group = new Group(username, UUID.randomUUID().toString(), groupName, new HashSet<>());
        when(groupRepository.findByUsernameAndName(username, groupName)).thenReturn(group);

        Set<Contact> result = groupService.getGroupContacts(username, groupName);

        assertTrue(result.isEmpty());
        verify(groupRepository, times(1)).findByUsernameAndName(username, groupName);
    }

    /**
     * Тестирование добавления группы пользователю
     */
    @Test
    public void testAddGroup() {
        User user = new User("testUser");
        String groupName = "newGroup";
        when(groupRepository.existsByUsernameAndName(user.getUsername(), groupName)).thenReturn(false);

        String result = groupService.addGroup(user, groupName);

        assertEquals("Группа 'newGroup' сохранена", result);
        verify(groupRepository, times(1)).saveOrUpdate(any(Group.class));
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
    public void testEditGroup() {
        User user = new User("testUser");
        Group group = new Group(user.getUsername(), UUID.randomUUID().toString(), "name", new HashSet<>());
        Contact contact = new Contact(user.getUsername(), "testContact");

        user.setState(State.EDIT_GROUP_NAME);
        user.setGroupNameToEdit(group.getName());
        String newValue = "newName";
        when(groupRepository.findByUsernameAndName(user.getUsername(), user.getGroupNameToEdit())).thenReturn(group);
        String editNameResult = groupService.editGroup(user, newValue);
        assertEquals("Группа 'name' успешно изменена", editNameResult);
        assertEquals(newValue, group.getName());
        verify(groupRepository, times(1)).saveOrUpdate(group);

        user.setState(State.EDIT_GROUP_ADD_CONTACT);
        user.setGroupNameToEdit(group.getName());
        when(groupRepository.findByUsernameAndName(user.getUsername(), user.getGroupNameToEdit())).thenReturn(group);
        when(contactRepository.findByUsernameAndName(user.getUsername(), contact.getName())).thenReturn(contact);
        String editGroupAddContact = groupService.editGroup(user, contact.getName());
        assertEquals(1, group.getContacts().size());
        assertEquals("Группа 'newName' успешно изменена", editGroupAddContact);
        verify(groupRepository, times(2)).saveOrUpdate(group);

        user.setState(State.EDIT_GROUP_DELETE_CONTACT);
        user.setGroupNameToEdit(group.getName());
        when(groupRepository.findByUsernameAndName(user.getUsername(), user.getGroupNameToEdit())).thenReturn(group);
        when(contactRepository.findByUsernameAndName(user.getUsername(), contact.getName())).thenReturn(contact);
        String editGroupDeleteContact = groupService.editGroup(user, contact.getName());
        assertEquals(0, group.getContacts().size());
        assertEquals("Группа 'newName' успешно изменена", editGroupDeleteContact);
        verify(groupRepository, times(3)).saveOrUpdate(group);
    }

    /**
     * Тестирование удаления группы
     */
    @Test
    public void testDeleteGroup() {
        User user = new User("testUser");
        String groupName = "testGroup";
        Group groupToDelete = new Group(user.getUsername(), UUID.randomUUID().toString(), groupName, new HashSet<>());
        when(groupRepository.findByUsernameAndName(user.getUsername(), groupName)).thenReturn(groupToDelete);

        String result = groupService.deleteGroup(user, groupName);

        assertEquals("Группа 'testGroup' успешно удалена", result);
        verify(groupRepository, times(1)).delete(groupToDelete);
    }

}