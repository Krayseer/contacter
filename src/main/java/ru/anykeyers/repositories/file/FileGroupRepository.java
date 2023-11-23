package ru.anykeyers.repositories.file;

import org.apache.commons.io.FileUtils;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.Group;
import ru.anykeyers.repositories.GroupRepository;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализация файловой базы данных
 */
public class FileGroupRepository implements GroupRepository {

    /**
     * Файловая БД
     */
    private final File dbFile;

    private final Map<String, Set<Group>> usersGroups = new HashMap<>();

    public FileGroupRepository(String groupFilePath) {
        this.dbFile = new File(groupFilePath);
        initGroups();
    }

    @Override
    public boolean existsByUsernameAndName(String username, String name) {
        return findByUsername(username).stream()
                .map(Group::getName)
                .anyMatch(groupName -> groupName.equalsIgnoreCase(name));
    }

    @Override
    public Set<Group> findByUsername(String username) {
        return usersGroups.getOrDefault(username, new HashSet<>());
    }

    @Override
    public Group findByUsernameAndName(String username, String nameGroupToFind) {
        for (Group userGroup : findByUsername(username)) {
            if (userGroup.getName().equals(nameGroupToFind)) {
                return userGroup;
            }
        }
        return null;
    }

    @Override
    public Contact findContactInGroupByName(Group group, String contactName) {
        Set<Contact> groupContacts = group.getContacts();
        for (Contact groupContact : groupContacts) {
            if (groupContact.getName().equals(contactName)) {
                return groupContact;
            }
        }
        return null;
    }

    @Override
    public void saveOrUpdate(Group group) {
        addGroup(group);
        saveGroupsInDB();
    }

    private void saveGroupsInDB() {
        List<String> resultLines = new ArrayList<>();
        for (Map.Entry<String, Set<Group>> entry : usersGroups.entrySet()) {
            for (Group group : entry.getValue()) {
                Set<Contact> groupContacts = group.getContacts();
                String contactsLine = groupContacts.stream()
                        .map(Contact::toString)
                        .map(str -> {
                            int index = str.indexOf(":");
                            return (index != -1)
                                    ? str.substring(index + 1)
                                    : str;
                        }).collect(Collectors.joining(";"));
                String idAndGroupName = group.getId() + "," + group.getName();
                resultLines.add(String.format("%s:%s=%s", group.getUsername(), idAndGroupName, contactsLine));
            }
        }
        try {
            FileUtils.writeLines(dbFile, "UTF-8", resultLines, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Group group) {
        usersGroups.get(group.getUsername()).remove(group);
        saveGroupsInDB();
    }

    /**
     * Инициализирует группы из файла в Map
     */
    private void initGroups() {
        try {
            List<String> lines = FileUtils.readLines(dbFile, "UTF-8");
            for (String line : lines) {
                String[] usernameAndGroup = line.split(":");
                String username = usernameAndGroup[0];
                String[] groupNameAndContacts = usernameAndGroup[1].split("=");

                String id = groupNameAndContacts[0].split(",")[0];
                String groupName = groupNameAndContacts[0].split(",")[1];
                String contactsStrings = groupNameAndContacts.length > 1 ? groupNameAndContacts[1] : null;

                Set<Contact> contacts = contactsStrings != null
                        ? Arrays.stream(contactsStrings.split(";"))
                            .map(contact -> {
                                String[] contactInfo = contact.split(",");
                                String phoneNumber = contactInfo.length > 5 ? contactInfo[5] : "";
                                return new Contact(username, contactInfo[0], contactInfo[1],
                                        Integer.parseInt(contactInfo[2]), contactInfo[3], contactInfo[4], phoneNumber);
                            })
                            .collect(Collectors.toSet())
                        : new HashSet<>();

                addGroup(new Group(username, id, groupName, contacts));
                System.out.println("");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Добавить группу по имени пользователя в {@link #usersGroups}
     */
    private void addGroup(Group group) {
        if (!usersGroups.containsKey(group.getUsername())) {
            usersGroups.put(group.getUsername(), new HashSet<>());
        }
        usersGroups.get(group.getUsername()).removeIf(currentGroup -> Objects.equals(currentGroup.getId(), group.getId()));
        usersGroups.get(group.getUsername()).add(group);
    }

}
