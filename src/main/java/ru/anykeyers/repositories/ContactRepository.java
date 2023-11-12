package ru.anykeyers.repositories;

import org.apache.commons.io.FileUtils;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.User;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Класс, отвечающий за работу с сохранением и получением контактов
 */
public class ContactRepository implements FileDBRepository {

    private final String contactFilePath;

    private final Map<String, Set<Contact>> usersContacts = new HashMap<>();

    public ContactRepository(String contactFilePath) {
        this.contactFilePath = contactFilePath;
        initContacts();
    }

    /**
     * Инициализирует контакты из файла в Map
     */
    private void initContacts() {
        File file = new File(contactFilePath);
        try {
            List<String> lines = FileUtils.readLines(file, "UTF-8");
            for (String line : lines) {
                String[] info = line.split(": ");
                String userName = info[0];
                String[] userInfo = info[1].split(" ");
                if (!usersContacts.containsKey(userName)) {
                    usersContacts.put(userName, new HashSet<>());
                }
                usersContacts.get(userName).add(new Contact(userInfo[0], userInfo[1], userInfo[2]));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Сохраняет контакты в файл
     */
    @Override
    public void saveAll() {
        File file = new File(contactFilePath);
        try {
            StringBuilder dataForSave = new StringBuilder();
            for (Map.Entry<String, Set<Contact>> entry : usersContacts.entrySet()) {
                String userName = entry.getKey();
                Set<Contact> contactsList = entry.getValue();
                for (Contact contact : contactsList) {
                    String contactInfo = contact.toString();
                    String line = userName + ": " + contactInfo;
                    dataForSave.append(line).append("\n");
                }
            }
            FileUtils.writeStringToFile(file, dataForSave.toString(), "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Находит сет контактов пользователя
     * @param username имя пользователя
     * @return сет контактов
     */
    public Set<Contact> findContactsByUsername(String username) {
        return usersContacts.getOrDefault(username, new HashSet<>());
    }

    /**
     * Ищет контакт авторизованного пользователя по имени или номеру телефона
     * @param user текущий пользователь
     * @param nameOrNumberContactToFind имя или номер телефона контакта для поиска
     * @return контакт пользователя
     */
    public Contact findContactByNameOrNumber(User user, String nameOrNumberContactToFind) {
        Set<Contact> userContacts = usersContacts.getOrDefault(user.getUsername(), new HashSet<>());
        for (Contact userContact : userContacts) {
            String nameUserContact = userContact.getFirstname() + " " + userContact.getLastname();
            if (nameUserContact.equals(nameOrNumberContactToFind) ||
                    userContact.getPhoneNumber().equals(nameOrNumberContactToFind)) {
                return userContact;
            }
        }
        return null;
    }

    /**
     * Удаляет контакт пользователя из его списка контактов
     * @param user текущий пользователь
     * @param contactToDelete удаляемый контакт
     */
    public void removeContact(User user, Contact contactToDelete) {
        usersContacts.get(user.getUsername()).remove(contactToDelete);
    }

    /**
     * Сохраняет контакт в список контактов пользователя
     * @param username имя пользователя
     * @param contact контакт для сохранения
     */
    public void save(String username, Contact contact) {
        if (!usersContacts.containsKey(username)) {
            usersContacts.put(username, new HashSet<>());
        }
        usersContacts.get(username).add(contact);
    }

    public String getContactFilePath() {
        return contactFilePath;
    }

}
