package ru.anykeyers.repositories.file.formatters;

import org.apache.commons.io.FileUtils;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.Group;
import ru.anykeyers.repositories.file.data.GroupData;
import ru.anykeyers.repositories.file.data.ObjectData;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Форматтер для таблицы групп
 */
public class GroupFormatter implements ObjectFormatter<Group> {

    ContactFormatter contactFormatter = new ContactFormatter();

    @Override
    public String format(Group object) {
        Set<Contact> groupContacts = object.getContacts();
        String contactsLine = groupContacts.stream()
                .map(contactFormatter::format)
                .map(str -> {
                    int index = str.indexOf(":");
                    return (index != -1)
                            ? str.substring(index + 1)
                            : str;
                }).collect(Collectors.joining(";"));
        String idAndGroupName = object.getId() + "," + object.getName();
        return String.format("%s:%s=%s", object.getUsername(), idAndGroupName, contactsLine);
    }

    @Override
    public Map<String, ObjectData<Group>> initFromFile(File file) {
        Map<String, ObjectData<Group>> collectionMap = new HashMap<>();
        try {
            List<String> lines = FileUtils.readLines(file, "UTF-8");
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
                            String phoneNumber = contactInfo.length > 2 ? contactInfo[2] : "";
                            return new Contact(username, contactInfo[0], contactInfo[1], phoneNumber);
                        })
                        .collect(Collectors.toSet())
                        : new HashSet<>();

                Group group = new Group(username, id, groupName, contacts);
                if (!collectionMap.containsKey(group.getUsername())) {
                    collectionMap.put(group.getUsername(), new GroupData());
                }
                collectionMap.get(group.getUsername()).addData(group);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return collectionMap;
    }

}
