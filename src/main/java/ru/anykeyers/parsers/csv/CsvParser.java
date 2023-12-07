package ru.anykeyers.parsers.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.Gender;
import ru.anykeyers.parsers.Parser;
import ru.anykeyers.repositories.ContactRepository;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Парсер CSV файлов
 */
public class CsvParser implements Parser {

    private final ContactRepository contactRepository;

    public CsvParser(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    public boolean parseImport(String username, String importPath) {
        try (CSVParser parser = CSVFormat.DEFAULT.parse(new FileReader(importPath))) {
            List<CSVRecord> records = parser.getRecords();
            for (CSVRecord record : records) {
                Contact contact = new Contact(
                        record.get(0), username, record.get(2), record.get(3),
                        Integer.parseInt(record.get(4)), Gender.valueOf(record.get(5)), Boolean.parseBoolean(record.get(6))
                );
                contactRepository.saveOrUpdate(contact);
            }
            return true;
        } catch (IOException exception) {
            return false;
        }
    }

    @Override
    public boolean parseExport(String username, String exportPath) {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(exportPath), StandardCharsets.UTF_8);
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            for (Contact contact : contactRepository.findByUsername(username)) {
                printer.printRecord(
                        contact.getId(), contact.getUsername(), contact.getName(), contact.getPhoneNumber(),
                        contact.getAge(), contact.getGender(), contact.isBlock()
                );
            }
            return true;
        } catch (IOException exception) {
            return false;
        }
    }
}
