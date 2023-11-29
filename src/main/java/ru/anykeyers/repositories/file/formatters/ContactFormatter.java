package ru.anykeyers.repositories.file.formatters;

import org.apache.commons.io.FileUtils;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.repositories.file.data.ContactData;
import ru.anykeyers.repositories.file.data.ObjectData;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Форматтер для таблицы контактов
 */
public class ContactFormatter implements ObjectFormatter<Contact> {

    @Override
    public String format(Contact object) {
        return String.format("%s:%s,%s,%s",
                object.getUsername(), object.getId(), object.getName(), object.getPhoneNumber());
    }

    @Override
    public Map<String, ObjectData<Contact>> initFromFile(File dbFile) {
        Map<String, ObjectData<Contact>> collectionMap = new HashMap<>();
        try {
            List<String> lines = FileUtils.readLines(dbFile, "UTF-8");
            lines.forEach(line -> {
                String[] usernameAndContact = line.split(":");
                String username = usernameAndContact[0];
                String[] contactInfo = usernameAndContact[1].split(",");
                String phoneNumber = contactInfo.length > 2 ? contactInfo[2] : "";
                Contact contact = new Contact(username, contactInfo[0], contactInfo[1], phoneNumber);
                if (!collectionMap.containsKey(username)) {
                    collectionMap.put(username, new ContactData());
                }
                collectionMap.get(username).addData(contact);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return collectionMap;
    }

}
