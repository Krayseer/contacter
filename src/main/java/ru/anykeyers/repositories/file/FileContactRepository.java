package ru.anykeyers.repositories.file;

import org.apache.commons.io.FileUtils;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.repositories.ContactRepository;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Реализация файловой базы данных
 */
public class FileContactRepository implements ContactRepository {

    /**
     * Файловая БД
     */
    private final File dbFile;

    private final Map<String, Set<Contact>> usersContacts = new HashMap<>();

    public FileContactRepository(String contactFilePath) {
        this.dbFile = new File(contactFilePath);
        initContacts();
    }

    @Override
    public boolean existsByUsernameAndName(String username, String name) {
        return findByUsername(username).stream()
                .map(Contact::getName)
                .anyMatch(contactName -> contactName.equalsIgnoreCase(name));
    }

    @Override
    public Set<Contact> findByUsername(String username) {
        return usersContacts.getOrDefault(username, new HashSet<>());
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
        if (!usersContacts.containsKey(contact.getUsername())) {
            usersContacts.put(contact.getUsername(), new HashSet<>());
        }
        usersContacts.get(contact.getUsername()).removeIf(currentContact -> Objects.equals(currentContact.getId(), contact.getId()));
        usersContacts.get(contact.getUsername()).add(contact);
        saveContactsInDB();
    }

    private void saveContactsInDB() {
        List<String> resultLines = new ArrayList<>();
        for (Map.Entry<String, Set<Contact>> entry : usersContacts.entrySet()) {
            for (Contact contact : entry.getValue()) {
                resultLines.add(String.format("%s", contact));
            }
        }
        try {
            FileUtils.writeLines(dbFile, "UTF-8", resultLines, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Contact contact) {
        usersContacts.get(contact.getUsername()).remove(contact);
        saveContactsInDB();
    }

    /**
     * Инициализирует контакты из файла в Map
     */
    private void initContacts() {
        try {
            List<String> lines = FileUtils.readLines(dbFile, "UTF-8");
            for (String line : lines) {
                String[] usernameAndContact = line.split(":");
                String username = usernameAndContact[0];
                String[] contactInfo = usernameAndContact[1].split(",");
                String phoneNumber = contactInfo.length > 5 ? contactInfo[5] : "";
                Contact contact = new Contact(username, contactInfo[0], contactInfo[1],
                        Integer.parseInt(contactInfo[2]), contactInfo[3], contactInfo[4], phoneNumber);
                if (!usersContacts.containsKey(username)) {
                    usersContacts.put(username, new HashSet<>());
                }
                usersContacts.get(username).add(contact);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
