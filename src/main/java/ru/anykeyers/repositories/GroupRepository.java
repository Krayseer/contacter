package ru.anykeyers.repositories;

import org.apache.commons.io.FileUtils;
import ru.anykeyers.ApplicationProperties;
import ru.anykeyers.domain.User;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.Group;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Класс, отвечающий за сохранение и получение групп
 */
public class GroupRepository implements FileDBRepository {

    private final String groupFilePath;

    private final Map<String, Set<Group>> usersGroups = new HashMap<>();

    public GroupRepository(String groupFilePath) {
        this.groupFilePath = groupFilePath;
        initGroups();
    }

    /**
     * Инициализирует группы из файла в Map
     */
    private void initGroups() {
        File file = new File(groupFilePath);
        try {
            List<String> lines = FileUtils.readLines(file, "UTF-8");
            for (String line : lines) {
                String[] info = line.split(": ");
                String username = info[0];
                String groupName = info[1];
                List<Contact> contacts = new ArrayList<>();
                if (info.length > 2) {
                    String[] groupContacts = info[2].split(",");
                    for (String groupContact : groupContacts) {
                        String[] contactInfo = groupContact.split(" ");
                        Contact contact = new Contact(contactInfo[0], contactInfo[1], contactInfo[2]);
                        contacts.add(contact);
                    }
                }

                Group group = new Group(groupName, contacts);
                if (!usersGroups.containsKey(username)) {
                    usersGroups.put(username, new HashSet<>());
                }
                usersGroups.get(username).add(group);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Сохраняет группы из Map в файл
     */
    @Override
    public void saveAll() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(groupFilePath))) {
            for (Map.Entry<String, Set<Group>> entry : usersGroups.entrySet()) {
                String userName = entry.getKey();
                Set<Group> userGroups = entry.getValue();
                for (Group group : userGroups) {
                    String line = userName + ": " + group.toString();
                    writer.write(line + "\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Находит сет групп пользователя
     * @param username имя пользователя
     * @return сет групп
     */
    public Set<Group> findGroupsByUsername(String username) {
        return usersGroups.getOrDefault(username, new HashSet<>());
    }

    /**
     * Сохраняет группу в список групп пользователя
     * @param username имя пользователя
     * @param group группа для сохранения
     */
    public void save(String username, Group group) {
        if (!usersGroups.containsKey(username)) {
            usersGroups.put(username, new HashSet<>());
        }
        usersGroups.get(username).add(group);
    }

    /**
     * Ищет группу текущего пользователя по имени
     * @param user пользователь
     * @param nameGroupToFind имя группы для поиска
     * @return группа или null, если группа отсутствует
     */
    public Group findGroupByName(User user, String nameGroupToFind) {
        Set<Group> userGroups = usersGroups.getOrDefault(user.getUsername(), new HashSet<>());
        for (Group userGroup : userGroups) {
            if (userGroup.getName().equals(nameGroupToFind)) {
                return userGroup;
            }
        }
        return null;
    }

    /**
     * Удаляет группу из списка групп пользователя
     * @param user текущий пользователь
     * @param groupToDelete группа для удаления
     */
    public void removeGroup(User user, Group groupToDelete) {
        usersGroups.get(user.getUsername()).remove(groupToDelete);
    }

    /**
     * Находит нужный контакт в группе
     * @param group группа для поиска
     * @param contactName имя контакта для поиска
     * @return контакт или null, если контакт не был найден
     */
    public Contact findContactInGroup(Group group, String contactName) {
        List<Contact> groupContacts = group.getMembers();
        for (Contact groupContact : groupContacts) {
            String tempContactName = groupContact.getFirstname() + " " + groupContact.getLastname();
            if (tempContactName.equals(contactName)) {
                return groupContact;
            }
        }
        return null;
    }

    public String getGroupFilePath() {
        return groupFilePath;
    }
}
