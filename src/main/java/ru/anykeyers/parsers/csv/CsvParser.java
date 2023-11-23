package ru.anykeyers.parsers.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.parsers.BaseParser;
import ru.anykeyers.repositories.ContactRepository;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Парсер CSV файлов
 */
public class CsvParser extends BaseParser {

    public CsvParser(ContactRepository contactRepository) {
        super(contactRepository);
    }

    @Override
    public boolean parseImport(String username, String importPath) {
        try (CSVParser parser = CSVFormat.DEFAULT.parse(new FileReader(importPath))) {
            List<CSVRecord> records = parser.getRecords();
            for (CSVRecord record : records) {
                Contact contact = new Contact(
                        record.get(1), record.get(0), record.get(2), Integer.parseInt(record.get(4)),
                        record.get(5), record.get(6), record.get(3)
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
        try (CSVPrinter printer = CSVFormat.DEFAULT.print(new FileWriter(exportPath))) {
            for (Contact contact : contactRepository.findByUsername(username)) {
                printer.printRecord(
                        contact.getId(), contact.getUsername(), contact.getName(), contact.getPhoneNumber(),
                        contact.getAge(), contact.getGender(), contact.getBlock()
                );
            }
            return true;
        } catch (IOException exception) {
            return false;
        }
    }
}
