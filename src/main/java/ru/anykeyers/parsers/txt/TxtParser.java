package ru.anykeyers.parsers.txt;

import org.apache.commons.io.FileUtils;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.parsers.Parser;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.repositories.file.parsers.FileContactParser;
import ru.anykeyers.repositories.file.parsers.FileObjectParser;
import ru.anykeyers.repositories.file.services.FileService;
import ru.anykeyers.repositories.file.services.impl.FileServiceImpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Парсер TXT файлов
 */
public class TxtParser implements Parser {

    FileObjectParser<Contact> contactParser;

    private final ContactRepository contactRepository;

    public TxtParser(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
        contactParser = new FileContactParser();
    }

    @Override
    public boolean parseImport(String username, String importPath) {
        FileService<Contact> fileService = new FileServiceImpl<>(contactParser);
        Collection<Contact> contacts = fileService.initDataFromFile(new File(importPath));
        contacts.forEach(contact -> {
            contact.setUsername(username);
            contactRepository.saveOrUpdate(contact);
        });
        return true;
    }

    @Override
    public boolean parseExport(String username, String exportPath) {
        List<String> resultLines = new ArrayList<>();
        for (Contact contact : contactRepository.findByUsername(username)) {
            resultLines.add(contactParser.parseTo(contact));
        }
        try {
            FileUtils.writeLines(new File(exportPath), "UTF-8", resultLines, false);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
