package ru.anykeyers.repositories.file;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.Group;
import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.ContactRepository;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;

/**
 * Тесты для класса {@link FileGroupRepository}
 */
@RunWith(MockitoJUnitRunner.class)
public class FileGroupRepositoryTest {

    private FileGroupRepository groupRepository;

    @Mock
    private ContactRepository contactRepository;

    private File tempDbFileGroup;

    private User user;

    @Before
    public void setUp() throws Exception {
        tempDbFileGroup = Files.createTempFile("tempDbFileGroup", ".txt").toFile();
        groupRepository = new FileGroupRepository(tempDbFileGroup.getPath(), contactRepository);
        user = new User("user");
    }

    /**
     * Тест метода {@link FileGroupRepository#existsByUsernameAndName(String, String)}
     */
    @Test
    public void existsGroupByUsernameAndNameTest() {
        // Подготовка
        Group savedGroup = new Group(user.getUsername(), "testGroup");
        Group nonSavedGroup = new Group(user.getUsername(), "testGroupSecond");
        groupRepository.saveOrUpdate(savedGroup);

        // Действие
        boolean isFirstGroupExistsInDb = groupRepository.existsByUsernameAndName(
                user.getUsername(), savedGroup.getName()
        );
        boolean isNotSecondGroupExistsInDb = groupRepository.existsByUsernameAndName(
                user.getUsername(), nonSavedGroup.getName()
        );

        // Проверка
        Assert.assertTrue(isFirstGroupExistsInDb);
        Assert.assertFalse(isNotSecondGroupExistsInDb);
    }

    /**
     * Тест метода {@link FileGroupRepository#findByUsername(String)}
     */
    @Test
    public void findGroupsByUsernameTest() {
        // Подготовка
        Group firstGroup = new Group(user.getUsername(), "testGroup");
        Group secondGroup = new Group(user.getUsername(), "testGroupSecond");
        groupRepository.saveOrUpdate(firstGroup);
        groupRepository.saveOrUpdate(secondGroup);

        // Действие
        Set<Group> actualGroups = groupRepository.findByUsername(user.getUsername());

        // Проверка
        Set<Group> expectedGroups = Set.of(firstGroup, secondGroup);
        Assert.assertEquals(2, actualGroups.size());
        Assert.assertEquals(expectedGroups, actualGroups);
    }

    /**
     * Тест метода {@link FileGroupRepository#findByUsernameAndName(String, String)}
     */
    @Test
    public void findGroupsByUsernameAndNameTest() {
        // Подготовка
        Group firstGroup = new Group(user.getUsername(), "testGroup");
        Group secondGroup = new Group(user.getUsername(), "testGroupSecond");
        groupRepository.saveOrUpdate(firstGroup);

        // Действие
        Group actualGroup = groupRepository.findByUsernameAndName(user.getUsername(), firstGroup.getName());
        Group nonExistsGroup = groupRepository.findByUsernameAndName(user.getUsername(), secondGroup.getName());

        // Проверка
        Assert.assertNull(nonExistsGroup);
        Assert.assertEquals(firstGroup, actualGroup);
    }

    /**
     * Тест метода {@link FileGroupRepository#findContactInGroupByName(Group, String)}
     */
    @Test
    public void findContactInGroupTest() {
        // Подготовка
        Group group = new Group(user.getUsername(), "testGroup");
        Contact contact = new Contact(user.getUsername(), "testContact");
        group.addContactInGroup(contact);
        groupRepository.saveOrUpdate(group);

        // Действие
        Contact actualContact = groupRepository.findContactInGroupByName(group, contact.getName());
        Contact nonExistsContact = groupRepository.findContactInGroupByName(group, "non-exists-name");

        // Проверка
        Assert.assertNull(nonExistsContact);
        Assert.assertEquals(contact, actualContact);
    }

    /**
     * Тест метода {@link FileGroupRepository#saveOrUpdate(Group)}
     */
    @Test
    public void saveOrUpdateGroupTest() throws IOException {
        // Подготовка
        Group firstGroup = new Group(user.getUsername(), "testGroup");
        Group secondGroup = new Group(user.getUsername(), "testGroupSecond");
        Contact contact = new Contact(user.getUsername(), "testContact");
        firstGroup.addContactInGroup(contact);

        // Действие
        Mockito.lenient().when(contactRepository.findByUsernameAndId(user.getUsername(), contact.getId()))
                .thenReturn(contact);
        groupRepository.saveOrUpdate(firstGroup);
        groupRepository.saveOrUpdate(secondGroup);
        List<String> actualLines = FileUtils.readLines(tempDbFileGroup, StandardCharsets.UTF_8);

        // Проверка
        List<String> expectedLines = List.of(
                String.format("user:id=%s;name=testGroup;contacts=%s", firstGroup.getId(), contact.getId()),
                String.format("user:id=%s;name=testGroupSecond;contacts=", secondGroup.getId())
        );
        expectedLines.forEach(line -> Assert.assertTrue(actualLines.contains(line)));
    }

    /**
     * Тест метода {@link FileGroupRepository#delete(Group)}
     */
    @Test
    public void deleteGroupTest() {
        // Подготовка
        Group firstGroup = new Group(user.getUsername(), "testGroup");
        Group secondGroup = new Group(user.getUsername(), "testGroupSecond");
        groupRepository.saveOrUpdate(firstGroup);
        groupRepository.saveOrUpdate(secondGroup);

        // Действие
        groupRepository.delete(firstGroup);
        Set<Group> actualGroups = groupRepository.findByUsername(user.getUsername());

        // Проверка
        Set<Group> expectedGroups = Set.of(secondGroup);
        Assert.assertEquals(1, actualGroups.size());
        Assert.assertEquals(actualGroups, expectedGroups);
    }

}
