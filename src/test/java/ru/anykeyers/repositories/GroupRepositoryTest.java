package ru.anykeyers.repositories;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.anykeyers.ApplicationProperties;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.Group;
import ru.anykeyers.domain.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class GroupRepositoryTest {

    private GroupRepository groupRepository;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        File tempDbFile = temporaryFolder.newFile("tempDbFile.txt");
        groupRepository = new GroupRepository(tempDbFile.getPath());
    }

    /**
     * Тест метода {@link GroupRepository#saveAll()}
     */
    @Test
    public void saveGroups() throws IOException {
        String username = "nikita";
        Group group = new Group("Work", List.of(new Contact("Ivan", "Ivanov", "79068065041")));
        groupRepository.save(username, group);
        groupRepository.saveAll();

        File dbFile = new File(groupRepository.getGroupFilePath());
        assertTrue(dbFile.exists());
        List<String> lines = FileUtils.readLines(dbFile, "UTF-8");

        assertTrue(lines.contains("nikita: Work: Ivan Ivanov 79068065041,"));
    }

    /**
     * Тест метода {@link GroupRepository#findGroupsByUsername(String)}
     */
    @Test
    public void findGroupsByUsername() {
        String username = "nikita";
        Group firstGroup = new Group("Work", List.of(new Contact("Ivan", "Ivanov", "79068065041")));
        groupRepository.save(username, firstGroup);
        Group secondGroup = new Group("Lessons", List.of(new Contact("Petya", "Petrov", "79068065042")));
        groupRepository.save(username, secondGroup);
        Set<Group> expectedSet = Set.of(new Group("Work", List.of(new Contact("Ivan", "Ivanov", "79068065041"))),
                new Group("Lessons", List.of(new Contact("Petya", "Petrov", "79068065042"))));
        Set<Group> actualSet = groupRepository.findGroupsByUsername(username);
        assertEquals(expectedSet, actualSet);
        assertEquals(2, actualSet.size());
    }

    /**
     * Тест метода {@link GroupRepository#save(String, Group)}
     */
    @Test
    public void save() {
        String username = "nikita";
        Group group = new Group("Work", List.of(new Contact("Ivan", "Ivanov", "79068065041")));
        groupRepository.save(username, group);
        Set<Group> actualGroups = groupRepository.findGroupsByUsername(username);
        Set<Group> expectedGroups = Set.of(new Group("Work", List.of(new Contact("Ivan", "Ivanov", "79068065041"))));
        assertEquals(expectedGroups, actualGroups);
        assertEquals(1, actualGroups.size());
    }

    /**
     * Тест метода {@link GroupRepository#findGroupByName(User, String)}
     */
    @Test
    public void findGroupByName() {
        User user = new User("nikita", "123");
        Group group = new Group("Work", List.of(new Contact("Ivan", "Ivanov", "79068065041")));
        groupRepository.save(user.getUsername(), group);
        Group actualGroup = groupRepository.findGroupByName(user, "Work");
        Group wrongGroup = groupRepository.findGroupByName(user, "Lessons");
        Group expectedGroup = new Group("Work", List.of(new Contact("Ivan", "Ivanov", "79068065041")));
        assertEquals(expectedGroup, actualGroup);
        assertNull(wrongGroup);
    }

    /**
     * Тест метода {@link GroupRepository#removeGroup(User, Group)}
     */
    @Test
    public void removeGroup() {
        User user = new User("nikita", "123");
        Group group = new Group("Work", List.of(new Contact("Ivan", "Ivanov", "79068065041")));
        groupRepository.save(user.getUsername(), group);
        groupRepository.removeGroup(user, group);
        Set<Group> actualGroups = groupRepository.findGroupsByUsername(user.getUsername());
        Set<Group> expectedGroups = new HashSet<>();
        assertEquals(expectedGroups, actualGroups);
    }

    /**
     * Тест метода {@link GroupRepository#findContactInGroup(Group, String)}
     */
    @Test
    public void findContactInGroup() {
        Group group = new Group("Work", List.of(new Contact("Ivan", "Ivanov", "79068065041")));
        Contact actualContact = groupRepository.findContactInGroup(group, "Ivan Ivanov");
        Contact wrongContact = groupRepository.findContactInGroup(group, "Igor Ivanov");
        Contact expectedContact = new Contact("Ivan", "Ivanov", "79068065041");
        assertEquals(expectedContact, actualContact);
        assertNull(wrongContact);
    }
}