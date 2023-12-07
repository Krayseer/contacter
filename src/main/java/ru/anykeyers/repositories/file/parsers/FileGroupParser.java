package ru.anykeyers.repositories.file.parsers;

import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.Group;
import ru.anykeyers.repositories.ContactRepository;

import java.util.stream.Collectors;

/**
 * Парсер для группы
 */
public class FileGroupParser implements FileObjectParser<Group> {

    private final ContactRepository contactRepository;

    public FileGroupParser(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    public String parseTo(Group object) {
        StringBuilder builder = new StringBuilder();
        builder.append(object.getUsername())
                .append(":id=").append(object.getId())
                .append(";name=").append(object.getName())
                .append(";contacts=").append(object.getContacts() == null
                        ? ""
                        : object.getContacts()
                        .stream()
                        .map(Contact::getId)
                        .collect(Collectors.joining(",")));
        return builder.toString();
    }

    @Override
    public Group parseFrom(String line) {
        String[] usernameAndGroup = line.split(":");
        String username = usernameAndGroup[0];
        String[] groupInfo = usernameAndGroup[1].split(";");
        Group group = new Group(username);

        for (String info : groupInfo) {
            String[] keyValue = info.split("=");
            String key = keyValue[0];
            String value = keyValue.length == 1 ? null : keyValue[1];
            switch (key) {
                case "id":
                    group.setId(value);
                    break;
                case "name":
                    group.setName(value);
                    break;
                case "contacts":
                    if (value == null || value.equals("null")) {
                        break;
                    }
                    String[] contactsIds = value.split(",");
                    for (String contactId : contactsIds) {
                        Contact contact = contactRepository.findByUsernameAndId(username, contactId);
                        group.addContactInGroup(contact);
                    }
                    break;
            }
        }
        return group;
    }

}
