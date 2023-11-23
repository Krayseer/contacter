package ru.anykeyers.parsers.xml;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import ru.anykeyers.domain.Contact;

import java.util.Set;

/**
 * Адаптер класса Contact для перевода контакта в формат XML
 */
public class AdapterContactXml extends XmlAdapter<AdapterContactXml.AdaptedContact, Contact> {

    @Override
    public Contact unmarshal(AdaptedContact adaptedContact) {
        return new Contact(
                adaptedContact.username, adaptedContact.id, adaptedContact.name,
                adaptedContact.age, adaptedContact.gender, adaptedContact.block, adaptedContact.phoneNumber
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
        adaptedContact.block = contact.getBlock();
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
        public String gender;

        @XmlElement
        public String block;

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
