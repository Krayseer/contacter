package ru.anykeyers.service.impl.import_export.xml;

import ru.anykeyers.common.Messages;
import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.service.FileService;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.Collection;

/**
 * Реализация {@link FileService сервиса} для XML файлов
 */
public class XMLFileService implements FileService<Contact> {

    private final Messages messages = Messages.getInstance();

    @Override
    public Collection<Contact> initDataFromFile(File file) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(AdaptedContacts.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            AdaptedContacts contacts = (AdaptedContacts) unmarshaller.unmarshal(file);
            return contacts.getContacts();
        } catch (JAXBException exception) {
            String errorMessage = messages.getMessageByKey("import_export.xml.error.import");
            throw new RuntimeException(errorMessage);
        }
    }

    @Override
    public void saveOrUpdateFile(File exportFile, Collection<Contact> contacts) {
        AdaptedContacts exportContacts = new AdaptedContacts();
        exportContacts.setContacts(contacts);
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(AdaptedContacts.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(exportContacts, exportFile);
        } catch (JAXBException exception) {
            String errorMessage = messages.getMessageByKey("import_export.xml.error.export");
            throw new RuntimeException(errorMessage);
        }
    }

}
