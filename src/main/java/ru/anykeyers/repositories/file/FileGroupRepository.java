package ru.anykeyers.repositories.file;

import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.Group;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.repositories.file.mapper.GroupMapper;
import ru.anykeyers.repositories.GroupRepository;
import ru.anykeyers.repositories.file.mapper.Mapper;
import ru.anykeyers.repositories.file.services.FileService;
import ru.anykeyers.repositories.file.services.impl.FileServiceImpl;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализация файловой базы данных для групп
 */
public class FileGroupRepository implements GroupRepository {

    private final Map<String, Set<Group>> groupsByUsername;

    private final File dbFile;

    private final FileService<Group> fileService;

    public FileGroupRepository(String groupFilePath,
                               ContactRepository contactRepository) {
        dbFile = new File(groupFilePath);
        Mapper<Group> groupMapper = new GroupMapper(contactRepository);
        fileService = new FileServiceImpl<>(groupMapper);
        Collection<Group> groups = fileService.initDataFromFile(dbFile);
        groupsByUsername = groups.stream()
                .collect(Collectors.groupingBy(Group::getUsername, Collectors.toSet()));
    }

    @Override
    public boolean existsByUsernameAndName(String username, String name) {
        Set<Group> groups = findByUsername(username);
        if (groups == null) {
            return false;
        }
        return groups.stream()
                .map(Group::getName)
                .anyMatch(groupName -> groupName.equals(name));
    }

    @Override
    public Set<Group> findByUsername(String username) {
        return groupsByUsername.get(username);
    }

    @Override
    public Group findByUsernameAndName(String username, String groupName) {
        for (Group group : findByUsername(username)) {
            if (group.getName() != null && group.getName().equals(groupName)) {
                return group;
            }
        }
        return null;
    }

    @Override
    public Contact findContactInGroupByName(Group group, String contactName) {
        Set<Contact> groupContacts = group.getContacts();
        if (groupContacts == null) {
            return null;
        }
        for (Contact contact : groupContacts) {
            if (contact.getName() != null && contact.getName().equals(contactName)) {
                return contact;
            }
        }
        return null;
    }

    @Override
    public void saveOrUpdate(Group group) {
        if (!groupsByUsername.containsKey(group.getUsername())) {
            groupsByUsername.put(group.getUsername(), new HashSet<>());
        }
        Collection<Group> groups = groupsByUsername.get(group.getUsername());
        groups.add(group);
        fileService.saveOrUpdateFile(dbFile, groupsByUsername);
    }

    @Override
    public void delete(Group group) {
        Collection<Group> contacts = groupsByUsername.get(group.getUsername());
        contacts.remove(group);
        fileService.saveOrUpdateFile(dbFile, groupsByUsername);
    }

}
