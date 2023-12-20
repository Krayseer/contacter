package ru.anykeyers.service.impl.import_export.xml;

import ru.anykeyers.domain.entity.Contact;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;

/**
 * Адаптер списка контактов для XML маппинга
 */
@XmlRootElement(name = "contacts")
public class AdaptedContacts {

    private Collection<Contact> contacts;

    @XmlElement(name = "contact")
    public Collection<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(Collection<Contact> contacts) {
        this.contacts = contacts;
    }

}
