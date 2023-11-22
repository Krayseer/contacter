//package ru.anykeyers.services;
//
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.rules.TemporaryFolder;
//import ru.anykeyers.domain.Contact;
//import ru.anykeyers.domain.Group;
//import ru.anykeyers.repositories.ContactRepository;
//import ru.anykeyers.repositories.GroupRepository;
//import ru.anykeyers.repositories.UserRepository;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Set;
//
//import static org.junit.Assert.*;
//import static ru.anykeyers.utils.StreamUtils.writeDataInSystemInputStream;
//
//public class GroupServiceTest {
//
//    private GroupService groupService;
//
//    private ContactService contactService;
//
//    private GroupRepository groupRepository;
//
//    private ContactRepository contactRepository;
//
//    private AuthenticationService authenticationService;
//
//    private UserRepository userRepository;
//
//    @Rule
//    public TemporaryFolder temporaryFolder = new TemporaryFolder();
//
//    @Before
//    public void setUp() throws Exception {
//        File tempDbFile = temporaryFolder.newFile("tempDbFile.txt");
//        groupRepository = new GroupRepository(tempDbFile.getPath());
//        userRepository = new UserRepository(tempDbFile.getPath());
//        contactRepository = new ContactRepository(tempDbFile.getPath());
//        authenticationService = new AuthenticationService(userRepository);
//        contactService = new ContactService(contactRepository, authenticationService);
//        groupService = new GroupService(authenticationService, contactService, groupRepository);
//    }
//
//    /**
//     * Тест метода {@link GroupService#addGroup()}
//     */
//    @Test
//    public void addGroup() {
//        String userInfo = "nikita\n123\n";
//        writeDataInSystemInputStream(userInfo);
//        authenticationService.authenticate();
//        String groupInfo = "Work\n";
//        writeDataInSystemInputStream(groupInfo);
//        groupService.addGroup();
//        Set<Group> actualSet = groupRepository.findGroupsByUsername("nikita");
//        Set<Group> expectedSet = Set.of(new Group ("Work", new ArrayList<>()));
//        assertEquals(expectedSet, actualSet);
//    }
//
//    /**
//     * Тест метода {@link GroupService#deleteGroup()}
//     */
//    @Test
//    public void deleteGroup() {
//        String userInfo = "nikita\n123\n";
//        writeDataInSystemInputStream(userInfo);
//        authenticationService.authenticate();
//        groupRepository.save("nikita", new Group("Work", new ArrayList<>()));
//        groupRepository.save("nikita", new Group("Lessons", new ArrayList<>()));
//        String deleteGroupName = "Work\n";
//        writeDataInSystemInputStream(deleteGroupName);
//        groupService.deleteGroup();
//
//        Set<Group> actualSet = groupRepository.findGroupsByUsername("nikita");
//        Set<Group> expectedSet = Set.of(new Group("Lessons", new ArrayList<>()));
//        assertEquals(expectedSet, actualSet);
//    }
//
//    /**
//     * Тест метода {@link GroupService#editGroup()}
//     */
//    @Test
//    public void editGroup() {
//    }
//}