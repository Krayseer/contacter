package ru.anykeyers.services;

import org.junit.Before;
import org.junit.Test;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.Group;
import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.repositories.GroupRepository;
import ru.anykeyers.repositories.file.FileContactRepository;
import ru.anykeyers.repositories.file.FileGroupRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Тесты для класса {@link GroupRepository}
 */
public class GroupServiceTest {

    private GroupService groupService;

    private GroupRepository groupRepository;

    private final User user = new User("username");

    private final Contact contact = new Contact(user.getUsername(), "Ivan Ivanov");

    private final Contact secondContact = new Contact(user.getUsername(), "Petr Petrov");

    private final String groupName = "Учеба";

    @Before
    public void setUp() throws IOException {
        File tempDbFile = Files.createTempFile("tempDbFile", ".txt").toFile();
        groupRepository = new FileGroupRepository(tempDbFile.getPath());
        ContactRepository contactRepository = new FileContactRepository(tempDbFile.getPath());
        groupService = new GroupService(groupRepository, contactRepository);

        contactRepository.saveOrUpdate(contact);
        contactRepository.saveOrUpdate(secondContact);
    }

    /**
     * Тест метода {@link GroupService#addGroup(User, String)}
     */
    @Test
    public void testAddGroup() {
        String addGroupResult = groupService.addGroup(user, groupName);
        String exceptedAddGroupResult = String.format("Группа '%s' сохранена", groupName);
        assertEquals(exceptedAddGroupResult, addGroupResult);
        assertTrue(groupRepository.existsByUsernameAndName(user.getUsername(), groupName));

        String repeatAddGroup = groupService.addGroup(user, groupName);
        String expectedRepeatResult = String.format("Группа '%s' уже существует", groupName);
        assertEquals(expectedRepeatResult, repeatAddGroup);
    }

    /**
     * Тест метода {@link ContactService#editContact(User, String, Object, Contact.Field)}
     */
    @Test
    public void testEditGroupName() {
        String newGroupName = "Практика";
        String nonExistsGroupName = "non-exists";
        groupService.addGroup(user, groupName);

        String editNonExistsGroupNameResult = groupService.editGroupName(user, nonExistsGroupName, newGroupName);
        String expectedEditNonExistsGroupName = String.format("Не удалось найти группу '%s'", nonExistsGroupName);
        assertEquals(expectedEditNonExistsGroupName, editNonExistsGroupNameResult);

        String editGroupNameResult = groupService.editGroupName(user, groupName, newGroupName);
        String expectedGroupNameResult = String.format("Группа '%s' успешно изменена", groupName);
        assertEquals(expectedGroupNameResult, editGroupNameResult);
        assertTrue(groupRepository.existsByUsernameAndName(user.getUsername(), newGroupName));
    }

    /**
     * Тест метода {@link GroupService#addContactInGroup(User, String, String)}
     */
    @Test
    public void testAddContactInGroup() {
        String nonExistsGroupName = "non-exists";
        groupService.addGroup(user, groupName);

        String addContactInNonExistsGroupResult = groupService.addContactInGroup(user, nonExistsGroupName, contact.getName());
        String expectedAddContactInNonExistsGroupResult = String.format("Не удалось найти группу '%s'", nonExistsGroupName);
        assertEquals(expectedAddContactInNonExistsGroupResult, addContactInNonExistsGroupResult);

        String addContactInGroupResult = groupService.addContactInGroup(user, groupName, contact.getName());
        String expectedAddContactInGroupResult = String.format("Контакт '%s' успешно добавлен в группу '%s'", contact.getName(), groupName);
        assertEquals(expectedAddContactInGroupResult, addContactInGroupResult);
        Group group = groupRepository.findByUsernameAndName(user.getUsername(), groupName);
        assertEquals(contact, groupRepository.findContactInGroupByName(group, contact.getName()));
    }

    /**
     * Тест метода {@link GroupService#deleteContactFromGroup(User, String, String)}
     */
    @Test
    public void testDeleteContactFromGroup() {
        String nonExistsGroupName = "non-exists";
        groupService.addGroup(user, groupName);

        String addContactInNonExistsGroupResult = groupService.deleteContactFromGroup(user, nonExistsGroupName, contact.getName());
        String expectedAddContactInNonExistsGroupResult = String.format("Не удалось найти группу '%s'", nonExistsGroupName);
        assertEquals(expectedAddContactInNonExistsGroupResult, addContactInNonExistsGroupResult);

        groupService.addContactInGroup(user, groupName, contact.getName());
        groupService.addContactInGroup(user, groupName, secondContact.getName());

        String addContactInGroupResult = groupService.deleteContactFromGroup(user, groupName, contact.getName());
        String expectedAddContactInGroupResult = String.format("Контакт '%s' успешно удален из группы '%s'", contact.getName(), groupName);
        assertEquals(expectedAddContactInGroupResult, addContactInGroupResult);
        Group group = groupRepository.findByUsernameAndName("username", groupName);
        Group actualGroup = groupRepository.findByUsernameAndName("username", groupName);
        assertEquals(Set.of(secondContact), actualGroup.getContacts());
    }

    /**
     * Тест метода {@link GroupService#deleteGroup(User, String)}
     */
    @Test
    public void testDeleteGroup() {
        String nonExistsGroupName = "non-exists";
        groupService.addGroup(user, groupName);

        String nonExistsDeleteGroupResult = groupService.deleteGroup(user, nonExistsGroupName);
        String expectedNonExistsDeleteGroupResult = String.format("Не удалось найти группу '%s'", nonExistsGroupName);
        assertEquals(expectedNonExistsDeleteGroupResult, nonExistsDeleteGroupResult);

        String deleteGroupResult = groupService.deleteGroup(user, groupName);
        String expectedDeleteGroupResult = String.format("Группа '%s' успешно удалена", groupName);
        assertEquals(expectedDeleteGroupResult, deleteGroupResult);
        assertFalse(groupRepository.existsByUsernameAndName(user.getUsername(), groupName));
    }

}