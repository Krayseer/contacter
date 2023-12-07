package ru.anykeyers.repositories.file.parsers;

import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.Gender;

import java.util.Objects;

/**
 * Парсер для контактов
 */
public class FileContactParser implements FileObjectParser<Contact> {

    @Override
    public String parseTo(Contact object) {
        StringBuilder builder = new StringBuilder();
        builder.append(object.getUsername())
                .append(":id=").append(object.getId() == null ? "" : object.getId())
                .append(";name=").append(object.getName() == null ? "" : object.getName())
                .append(";phone_number=").append(object.getPhoneNumber() == null ? "" : object.getPhoneNumber())
                .append(";age=").append(object.getAge() == 0 ? "" : object.getAge())
                .append(";gender=").append(object.getGender() == null ? "" : object.getGender())
                .append(";block=").append(object.isBlock());
        return builder.toString();
    }

    @Override
    public Contact parseFrom(String line) {
        String[] usernameAndContact = line.split(":");
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
                    contact.setAge(value == null ? 0 : Integer.parseInt(value));
                    break;
                case "gender":
                    contact.setGender(value == null ? null : Gender.valueOf(value));
                    break;
                case "block":
                    contact.setBlock(Objects.equals(value, "true"));
                    break;

            }
        }
        return contact;
    }

}
