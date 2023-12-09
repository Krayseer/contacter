package ru.anykeyers.repositories.file.mapper;

import ru.anykeyers.domain.Contact;

/**
 * Парсер для контактов
 */
public class ContactMapper implements Mapper<Contact> {

    @Override
    public String format(Contact object) {
        StringBuilder builder = new StringBuilder();
        builder.append(object.getUsername())
                .append(":id=").append(object.getId())
                .append(";name=").append(object.getName())
                .append(";phone_number=").append(object.getPhoneNumber());
        return builder.toString();
    }

    @Override
    public Contact parse(String line) {
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
            }
        }
        return contact;
    }

}
