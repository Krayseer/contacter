package ru.anykeyers.repositories.file;

import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.Group;
import ru.anykeyers.repositories.file.formatters.GroupFormatter;
import ru.anykeyers.repositories.GroupRepository;
import ru.anykeyers.repositories.file.data.GroupData;
import ru.anykeyers.repositories.file.data.ObjectData;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализация файловой базы данных
 */
public class FileGroupRepository extends BaseFileRepository<Group> implements GroupRepository {

    public FileGroupRepository(String groupFilePath) {
        super(groupFilePath);
        formatter = new GroupFormatter();
    }

    @Override
    public boolean existsByUsernameAndName(String username, String name) {
        return findByUsername(username).stream()
                .map(Group::getName)
                .anyMatch(groupName -> groupName.equalsIgnoreCase(name));
    }

    @Override
    public Set<Group> findByUsername(String username) {
        Map<String, ObjectData<Group>> infoByUsername = getInfosByUsername();
        if (infoByUsername.get(username) == null) {
            infoByUsername.put(username, new GroupData());
        }
        ObjectData<Group> groups = infoByUsername.get(username);
        if (groups == null) {
            return null;
        }
        return (Set<Group>) groups.getData();
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
        Map<String, ObjectData<Group>> groupsByUsername = getInfosByUsername();
        if (groupsByUsername.get(group.getUsername()) == null) {
            groupsByUsername.put(group.getUsername(), new GroupData());
        }
        ObjectData<Group> groupData = groupsByUsername.get(group.getUsername());
        groupData.addData(group);
        updateFile(groupsByUsername);
    }

    @Override
    public void delete(Group group) {
        Map<String, ObjectData<Group>> infoByUsername = getInfosByUsername();
        GroupData contactInfo = (GroupData) infoByUsername.get(group.getUsername());
        contactInfo.removeData(group);
        updateFile(infoByUsername);
    }

    private void updateFile(Map<String, ObjectData<Group>> infoByUsername) {
        List<String> resultLines = infoByUsername.values().stream()
                .map(userInfo -> (Set<Group>) userInfo.getData())
                .flatMap(Collection::stream)
                .map(formatter::format)
                .collect(Collectors.toList());
        saveOrUpdateFile(resultLines);
    }

}
