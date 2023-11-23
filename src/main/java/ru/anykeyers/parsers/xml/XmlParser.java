package ru.anykeyers.parsers.xml;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.parsers.BaseParser;
import ru.anykeyers.repositories.ContactRepository;
import java.io.File;
import java.util.Set;

/**
 * Парсер XML файлов
 */
public class XmlParser extends BaseParser {

    public XmlParser(ContactRepository contactRepository) {
        super(contactRepository);
    }

    @Override
    public boolean parseImport(String username, String importPath) {
        try {
            File importFile = new File(importPath);
            JAXBContext jaxbContext = JAXBContext.newInstance(AdapterContactXml.Contacts.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            AdapterContactXml.Contacts contacts = (AdapterContactXml.Contacts) unmarshaller.unmarshal(importFile);
            Set<Contact> importedContacts = contacts.getContacts();
            importedContacts.forEach(contactRepository::saveOrUpdate);

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
