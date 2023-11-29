package ru.anykeyers.repositories.file.data;

import ru.anykeyers.domain.Contact;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Информация о контактах
 */
public class ContactData implements ObjectData<Contact> {

    private final Set<Contact> contacts;

    public ContactData() {
        contacts = new HashSet<>();
    }

    @Override
    public Object getData() {
        return contacts;
    }

    @Override
    public void addData(Contact contact) {
        contacts.removeIf(currentContact -> Objects.equals(currentContact.getId(), contact.getId()));
        contacts.add(contact);
    }

    @Override
    public void removeData(Contact contact) {
        contacts.remove(contact);
    }

}
