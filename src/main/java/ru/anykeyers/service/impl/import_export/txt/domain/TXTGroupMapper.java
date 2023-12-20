package ru.anykeyers.service.impl.import_export.txt.domain;

import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.domain.entity.Group;
import ru.anykeyers.common.Mapper;
import ru.anykeyers.repository.ContactRepository;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Маппер для группы
 */
public class TXTGroupMapper implements Mapper<Group> {

    private final ContactRepository contactRepository;

    public TXTGroupMapper(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    public String format(Group object) {
        return """
                %s:id=%s;name=%s;contacts=%s"""
                .formatted(
                        object.getUsername(),
                        object.getId(),
                        object.getName(),
                        object.getContacts().stream()
                                .map(Contact::getId)
                                .collect(Collectors.joining(","))
                );
    }

    @Override
    public Group parse(String str) {
        String[] usernameAndGroup = str.split(":");
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
                        Optional<Contact> contact = contactRepository.getByUsernameAndId(username, contactId);
                        contact.ifPresent(group::addContactInGroup);
                    }
                    break;
            }
        }
        return group;
    }

}
