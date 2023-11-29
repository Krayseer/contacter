package ru.anykeyers.repositories.file;

import ru.anykeyers.domain.Contact;
import ru.anykeyers.repositories.file.formatters.ContactFormatter;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.repositories.file.data.ContactData;
import ru.anykeyers.repositories.file.data.ObjectData;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализация файловой базы данных для контактов
 */
public class FileContactRepository extends BaseFileRepository<Contact> implements ContactRepository {

    public FileContactRepository(String contactFilePath) {
        super(contactFilePath);
        formatter = new ContactFormatter();
    }

    @Override
    public boolean existsByUsernameAndName(String username, String name) {
        return findByUsername(username).stream()
                .map(Contact::getName)
                .anyMatch(contactName -> contactName.equalsIgnoreCase(name));
    }

    @Override
    public Set<Contact> findByUsername(String username) {
        Map<String, ObjectData<Contact>> infoByUsername = getInfosByUsername();
        if (infoByUsername.get(username) == null) {
            infoByUsername.put(username, new ContactData());
        }
        ObjectData<Contact> contacts = infoByUsername.get(username);
        if (contacts == null) {
            return null;
        }
        return (Set<Contact>) contacts.getData();
    }

    @Override
    public Contact findByUsernameAndName(String username, String name) {
        for (Contact contact : findByUsername(username)) {
            if (contact.getName().equals(name)) {
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
        Map<String, ObjectData<Contact>> infoByUsername = getInfosByUsername();
        if (infoByUsername.get(contact.getUsername()) == null) {
            infoByUsername.put(contact.getUsername(), new ContactData());
        }
        ContactData contactInfo = (ContactData) infoByUsername.get(contact.getUsername());
        contactInfo.addData(contact);
        updateFile(infoByUsername);
    }

    @Override
    public void delete(Contact contact) {
        Map<String, ObjectData<Contact>> infoByUsername = getInfosByUsername();
        ContactData contactInfo = (ContactData) infoByUsername.get(contact.getUsername());
        contactInfo.removeData(contact);
        updateFile(infoByUsername);
    }

    private void updateFile(Map<String, ObjectData<Contact>> infoByUsername) {
        List<String> resultLines = infoByUsername.values().stream()
                .map(userInfo -> (Set<Contact>) userInfo.getData())
                .flatMap(Collection::stream)
                .map(formatter::format)
                .collect(Collectors.toList());
        saveOrUpdateFile(resultLines);
    }

}
