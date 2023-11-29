package ru.anykeyers.repositories.file;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.Group;
import ru.anykeyers.domain.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Тесты для класса {@link FileGroupRepository}
 */
public class FileGroupRepositoryTest {

    private FileGroupRepository groupRepository;

    private File tempDbFile;

    private final User user = new User("user");

    private final Contact firstContact = new Contact(user.getUsername(), "1","Ivan Ivanov", "79068065041");

    private final Contact secondContact = new Contact(user.getUsername(), "2", "Petya Petrov", "79068065042");

    private final Contact thirdContact = new Contact(user.getUsername(), "3", "Nikita Lara", "79125152");

    private final Group firstUserGroup = new Group(user.getUsername(), "4", "Учеба", Set.of(firstContact, secondContact));

    private final Group secondUserGroup = new Group(user.getUsername(), "5", "Друзья", Set.of(thirdContact));

    @Before
    public void setUp() throws Exception {
        tempDbFile = Files.createTempFile("tempDbFile", ".txt").toFile();
        groupRepository = new FileGroupRepository(tempDbFile.getPath());
    }

    /**
     * Тест метода {@link FileGroupRepository#existsByUsernameAndName(String, String)}
     */
    @Test
    public void testExistsGroupByUsernameAndName() {
        saveTempGroups();

        boolean isFirstGroupExistsInDb = groupRepository.existsByUsernameAndName(user.getUsername(), "Учеба");
        boolean isNotRealGroupExistsInDb = groupRepository.existsByUsernameAndName(user.getUsername(), "пустой");

        assertTrue(isFirstGroupExistsInDb);
        assertFalse(isNotRealGroupExistsInDb);
    }

    /**
     * Тест метода {@link FileGroupRepository#findByUsername(String)} ()}
     */
    @Test
    public void testFindGroupsByUsername() {
        saveTempGroups();

        Set<Group> actualGroups = groupRepository.findByUsername(user.getUsername());

        Set<Group> expectedGroups = Set.of(firstUserGroup, secondUserGroup);
        assertEquals(2, actualGroups.size());
        assertEquals(expectedGroups, actualGroups);
    }

    /**
     * Тест метода {@link FileGroupRepository#findByUsernameAndName(String, String)}
     */
    @Test
    public void testFindGroupsByUsernameAndName() {
        saveTempGroups();

        Group actualGroup = groupRepository.findByUsernameAndName(user.getUsername(), "Учеба");
        Group nonExistsGroup = groupRepository.findByUsernameAndName(user.getUsername(), "пустой");

        assertNull(nonExistsGroup);
        assertEquals(firstUserGroup, actualGroup);
    }

    /**
     * Тест метода {@link FileGroupRepository#findContactInGroupByName(Group, String)}
     */
    @Test
    public void testFindContactInGroup() {
        saveTempGroups();

        Contact actualContact = groupRepository.findContactInGroupByName(firstUserGroup, firstContact.getName());
        Contact nonExistsContact = groupRepository.findContactInGroupByName(firstUserGroup, "non-exists");

        assertNull(nonExistsContact);
        assertEquals(firstContact, actualContact);
    }

    /**
     * Тест метода {@link FileGroupRepository#saveOrUpdate(Group)}
     */
    @Test
    public void testSaveOrUpdateGroup() throws IOException {
        groupRepository.saveOrUpdate(firstUserGroup);
        groupRepository.saveOrUpdate(secondUserGroup);

        List<String> lines = FileUtils.readLines(tempDbFile, "UTF-8");
        String expectedString1 = String.format("%s\n%s;%s",
                "user:5,Друзья=3,Nikita Lara,79125152",
                "user:4,Учеба=1,Ivan Ivanov,79068065041",
                "2,Petya Petrov,79068065042"
        );

        String expectedString2 = String.format("%s\n%s;%s",
                "user:5,Друзья=3,Nikita Lara,79125152",
                "user:4,Учеба=2,Petya Petrov,79068065042",
                "1,Ivan Ivanov,79068065041"
        );

        String actualString = String.join("\n", lines);
        assertTrue(actualString.equals(expectedString1) || actualString.equals(expectedString2));
    }
    /**
     * Тест метода {@link FileGroupRepository#delete(Group)}
     */
    @Test
    public void testRemoveGroup() {
        saveTempGroups();

        groupRepository.delete(firstUserGroup);
        Set<Group> actualGroups = groupRepository.findByUsername(user.getUsername());


        Set<Group> expectedGroups = Set.of(secondUserGroup);
        assertEquals(1, actualGroups.size());
        assertEquals(actualGroups, expectedGroups);
    }

    /**
     * Сохранить временные группы в базу данных
     */
    private void saveTempGroups() {
        groupRepository.saveOrUpdate(firstUserGroup);
        groupRepository.saveOrUpdate(secondUserGroup);
    }

}
