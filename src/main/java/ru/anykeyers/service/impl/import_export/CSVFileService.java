package ru.anykeyers.service.impl.import_export;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import ru.anykeyers.common.Messages;
import ru.anykeyers.domain.Gender;
import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.service.FileService;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Реализация {@link FileService сервиса} для CSV файлов
 */
public class CSVFileService implements FileService<Contact> {

    private final Messages messages = Messages.getInstance();

    @Override
    public Collection<Contact> initDataFromFile(File file) {
        Set<Contact> contacts = new HashSet<>();
        try (CSVParser parser = CSVFormat.DEFAULT.parse(new FileReader(file))) {
            parser.getRecords().forEach(record -> {
                Contact contact = new Contact(
                        record.get(0),
                        record.get(1),
                        record.get(2),
                        record.get(3),
                        Objects.equals(record.get(4), "") ? null : Integer.parseInt(record.get(4)),
                        Objects.equals(record.get(5), "") ? null : Gender.valueOf(record.get(5)),
                        Boolean.parseBoolean(record.get(6))
                );
                contacts.add(contact);
            });
        } catch (IOException ex) {
            String errorMessage = messages.getMessageByKey("import_export.csv.error.import");
            throw new RuntimeException(errorMessage);
        }
        return contacts;
    }

    @Override
    public void saveOrUpdateFile(File exportFile, Collection<Contact> contacts) {
        try (CSVPrinter printer = CSVFormat.DEFAULT.print(exportFile, StandardCharsets.UTF_8)) {
            for (Contact contact : contacts) {
                printer.printRecord(
                        contact.getId(),
                        contact.getUsername(),
                        contact.getName(),
                        contact.getPhoneNumber(),
                        contact.getAge(),
                        contact.getGender(),
                        contact.isBlocked()
                );
            }
        } catch (IOException ex) {
            String errorMessage = messages.getMessageByKey("import_export.csv.error.export");
            throw new RuntimeException(errorMessage);
        }
    }

}
