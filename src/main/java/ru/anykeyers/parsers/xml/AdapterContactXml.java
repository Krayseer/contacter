package ru.anykeyers.parsers.xml;

import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.Gender;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Set;

/**
 * Адаптер класса Contact для перевода контакта в формат XML
 */
public class AdapterContactXml extends XmlAdapter<AdapterContactXml.AdaptedContact, Contact> {

    @Override
    public Contact unmarshal(AdaptedContact adaptedContact) {
        return new Contact(
                adaptedContact.id, adaptedContact.username,
                adaptedContact.name, adaptedContact.phoneNumber, adaptedContact.age,
                adaptedContact.gender, adaptedContact.block
        );
    }

    @Override
    public AdaptedContact marshal(Contact contact) {
        AdaptedContact adaptedContact = new AdaptedContact();
        adaptedContact.id = contact.getId();
        adaptedContact.username = contact.getUsername();
        adaptedContact.name = contact.getName();
        adaptedContact.phoneNumber = contact.getPhoneNumber();
        adaptedContact.age = contact.getAge();
        adaptedContact.gender = contact.getGender();
        adaptedContact.block = contact.isBlock();
        return adaptedContact;
    }

    @XmlRootElement(name = "contact")
    static class AdaptedContact {

        @XmlElement
        public String id;

        @XmlElement
        public String username;

        @XmlElement
        public String name;

        @XmlElement
        public String phoneNumber;

        @XmlElement
        public int age;

        @XmlElement
        public Gender gender;

        @XmlElement
        public boolean block;

    }

    @XmlRootElement(name = "contacts")
    static class Contacts {

        private Set<Contact> contacts;

        @XmlElement(name = "contact")
        public Set<Contact> getContacts() {
            return contacts;
        }

        public void setContacts(Set<Contact> contacts) {
            this.contacts = contacts;
        }

    }

}
