package ru.anykeyers.parsers.txt;

import org.apache.commons.io.FileUtils;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.parsers.BaseParser;
import ru.anykeyers.repositories.ContactRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Парсер TXT файлов
 */
public class TxtParser extends BaseParser {

    public TxtParser(ContactRepository contactRepository) {
        super(contactRepository);
    }

    @Override
    public boolean parseImport(String username, String importPath) {
        try {
            List<String> lines = FileUtils.readLines(new File(importPath), "UTF-8");
            for (String line : lines) {
                String[] usernameAndContact = line.split(":");
                String user = usernameAndContact[0];
                String[] contactInfo = usernameAndContact[1].split(",");
                Contact contact = new Contact(user, contactInfo[0], contactInfo[1],
                        Integer.parseInt(contactInfo[2]), contactInfo[3], contactInfo[4], contactInfo[5]);
                contactRepository.saveOrUpdate(contact);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean parseExport(String username, String exportPath) {
        List<String> resultLines = new ArrayList<>();
        for (Contact contact : contactRepository.findByUsername(username)) {
            resultLines.add(contact.toString());
        }
        try {
            FileUtils.writeLines(new File(exportPath), "UTF-8", resultLines, false);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
