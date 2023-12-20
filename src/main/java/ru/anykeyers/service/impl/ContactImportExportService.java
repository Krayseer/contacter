package ru.anykeyers.service.impl;

import ru.anykeyers.common.Messages;
import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.service.FileService;
import ru.anykeyers.domain.FileFormat;
import ru.anykeyers.factory.ImportExportMapperFactory;
import ru.anykeyers.repository.ContactRepository;
import ru.anykeyers.service.ImportExportService;

import java.io.File;
import java.util.Collection;
import java.util.Set;

/**
 * Реализация сервиса {@link ImportExportService} для контактов
 */
public class ContactImportExportService implements ImportExportService {

    private final Messages messages = Messages.getInstance();

    private final ImportExportMapperFactory importExportMapperFactory;

    private final ContactRepository contactRepository;

    public ContactImportExportService(ImportExportMapperFactory importExportMapperFactory,
                                      ContactRepository contactRepository) {
        this.importExportMapperFactory = importExportMapperFactory;
        this.contactRepository = contactRepository;
    }

    @Override
    public void importData(User user, File importFile) {
        FileFormat fileFormat = getFileFormat(importFile.getName());
        FileService<Contact> service = importExportMapperFactory.getServiceByFormat(fileFormat);
        Collection<Contact> contacts = service.initDataFromFile(importFile);
        contacts.forEach(contact -> {
            contact.setUsername(user.getUsername());
            contactRepository.saveOrUpdate(contact);
        });
    }

    @Override
    public void exportData(User user, File exportFile) {
        FileFormat fileFormat = getFileFormat(exportFile.getName());
        FileService<Contact> mapper = importExportMapperFactory.getServiceByFormat(fileFormat);
        Set<Contact> contacts = contactRepository.findByUsername(user.getUsername());
        mapper.saveOrUpdateFile(exportFile, contacts);
    }

    /**
     * Получить формат файла
     *
     * @param filePath путь до файла
     */
    private FileFormat getFileFormat(String filePath) {
        FileFormat fileFormat;
        try {
            fileFormat = FileFormat.valueOf(filePath.split("\\.")[1].toUpperCase());
        } catch (RuntimeException ex) {
            String errorMessage = messages.getMessageByKey("exception.file.invalid-format");
            throw new RuntimeException(errorMessage);
        }
        return fileFormat;
    }

}
