package ru.anykeyers.parsers.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.parsers.Parser;
import ru.anykeyers.repositories.ContactRepository;

import java.io.File;
import java.util.Set;

/**
 * Парсер JSON файлов
 */
public class JsonParser implements Parser {

    private final ObjectMapper objectMapper;

    private final ContactRepository contactRepository;

    public JsonParser(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
        objectMapper = new ObjectMapper();
    }

    @Override
    public boolean parseImport(String username, String importPath) {
        try {
            File importFile = new File(importPath);
            Set<Contact> importedContacts = objectMapper.readValue(importFile, new TypeReference<>() {});
            importedContacts.forEach(contact -> {
                contact.setUsername(username);
                contactRepository.saveOrUpdate(contact);
            });
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    @Override
    public boolean parseExport(String username, String exportPath) {
        try {
            File exportFile = new File(exportPath);
            objectMapper.writeValue(exportFile, contactRepository.findByUsername(username));
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

}
