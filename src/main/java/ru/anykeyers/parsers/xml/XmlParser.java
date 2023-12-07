package ru.anykeyers.parsers.xml;

import ru.anykeyers.domain.Contact;
import ru.anykeyers.parsers.Parser;
import ru.anykeyers.repositories.ContactRepository;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.Set;

/**
 * Парсер XML файлов
 */
public class XmlParser implements Parser {

    private final ContactRepository contactRepository;

    public XmlParser(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    public boolean parseImport(String username, String importPath) {
        try {
            File importFile = new File(importPath);
            JAXBContext jaxbContext = JAXBContext.newInstance(AdapterContactXml.Contacts.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            AdapterContactXml.Contacts contacts = (AdapterContactXml.Contacts) unmarshaller.unmarshal(importFile);
            Set<Contact> importedContacts = contacts.getContacts();
            importedContacts.forEach(contact -> {
                contact.setUsername(username);
                contactRepository.saveOrUpdate(contact);
            });

            return true;
        } catch (JAXBException exception) {
            return false;
        }
    }

    @Override
    public boolean parseExport(String username, String exportPath) {
        Set<Contact> userContacts = contactRepository.findByUsername(username);
        AdapterContactXml.Contacts contacts = new AdapterContactXml.Contacts();
        contacts.setContacts(userContacts);

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(AdapterContactXml.Contacts.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            File exportFile = new File(exportPath);
            marshaller.marshal(contacts, exportFile);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

}
