package ru.anykeyers.repositories.file;

import ru.anykeyers.domain.Contact;
import ru.anykeyers.repositories.file.mapper.ContactMapper;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.repositories.file.services.FileService;
import ru.anykeyers.repositories.file.services.impl.FileServiceImpl;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализация файловой базы данных для контактов
 */
public class FileContactRepository implements ContactRepository {

    private final Map<String, Set<Contact>> contactsByUsername;

    private final File dbFile;

    private final FileService<Contact> fileService;

    public FileContactRepository(String contactFilePath) {
        dbFile = new File(contactFilePath);
        fileService = new FileServiceImpl<>(new ContactMapper());
        Collection<Contact> contacts = fileService.initDataFromFile(dbFile);
        contactsByUsername = contacts.stream()
                .collect(Collectors.groupingBy(Contact::getUsername, Collectors.toSet()));
    }

    @Override
    public boolean existsByUsernameAndName(String username, String name) {
        Set<Contact> contacts = findByUsername(username);
        if (contacts == null) {
            return false;
        }
        return contacts.stream()
                .map(Contact::getName)
                .anyMatch(contactName -> contactName.equals(name));
    }

    @Override
    public Set<Contact> findByUsername(String username) {
        return contactsByUsername.get(username);
    }

    @Override
    public Contact findByUsernameAndId(String username, String contactId) {
        for (Contact contact : findByUsername(username)) {
            if (contact.getId() != null && contact.getId().equals(contactId)) {
                return contact;
            }
        }
        return null;
    }

    @Override
    public Contact findByUsernameAndName(String username, String name) {
        for (Contact contact : findByUsername(username)) {
            if (contact.getName() != null && contact.getName().equals(name)) {
                return contact;
            }
        }
        return null;
    }

    @Override
    public Contact findByUsernameAndPhoneNumber(String username, String phoneNumber) {
        for (Contact contact : findByUsername(username)) {
            if (contact.getPhoneNumber() != null && contact.getPhoneNumber().equals(phoneNumber)) {
                return contact;
            }
        }
        return null;
    }

    @Override
    public void saveOrUpdate(Contact contact) {
        if (!contactsByUsername.containsKey(contact.getUsername())) {
            contactsByUsername.put(contact.getUsername(), new HashSet<>());
        }
        Set<Contact> contacts = contactsByUsername.get(contact.getUsername());
        contacts.add(contact);
        fileService.saveOrUpdateFile(dbFile, contactsByUsername);
    }

    @Override
    public void delete(Contact contact) {
        Set<Contact> contacts = contactsByUsername.get(contact.getUsername());
        contacts.remove(contact);
        fileService.saveOrUpdateFile(dbFile, contactsByUsername);
    }

}
