package ru.anykeyers.repository.file.mapper;

import org.apache.commons.lang3.StringUtils;
import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.domain.Gender;
import ru.anykeyers.common.Mapper;

/**
 * Маппер для контакта
 */
public class FileContactMapper implements Mapper<Contact> {

    @Override
    public String format(Contact object) {
        return """
                %s:id=%s;name=%s;phone_number=%s;age=%s;gender=%s;block=%s"""
                .formatted(
                        object.getUsername(),
                        object.getId() == null ? StringUtils.EMPTY : object.getId(),
                        object.getName() == null ? StringUtils.EMPTY : object.getName(),
                        object.getPhoneNumber() == null ? StringUtils.EMPTY : object.getPhoneNumber(),
                        object.getAge() == null ? StringUtils.EMPTY : String.valueOf(object.getAge()),
                        object.getGender() == null ? StringUtils.EMPTY : object.getGender(),
                        object.isBlocked()
                );
    }

    @Override
    public Contact parse(String str) {
        String[] usernameAndContact = str.split(":");
        String username = usernameAndContact[0];
        String[] contactInfo = usernameAndContact[1].split(";");
        Contact contact = new Contact(username);
        for (String info : contactInfo) {
            String[] keyValue = info.split("=");
            String key = keyValue[0];
            String value = keyValue.length == 1 ? null : keyValue[1];
            switch (key) {
                case "id":
                    contact.setId(value);
                    break;
                case "name":
                    contact.setName(value);
                    break;
                case "phone_number":
                    contact.setPhoneNumber(value);
                    break;
                case "age":
                    contact.setAge(value == null ? null : Integer.parseInt(value));
                    break;
                case "gender":
                    contact.setGender(value == null ? null : Gender.valueOf(value));
                    break;
                case "block":
                    contact.setBlocked(Boolean.parseBoolean(value));
                    break;
            }
        }
        return contact;
    }

}
