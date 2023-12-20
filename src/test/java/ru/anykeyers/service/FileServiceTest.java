package ru.anykeyers.service;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.anykeyers.domain.Gender;
import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.service.impl.import_export.CSVFileService;
import ru.anykeyers.service.impl.import_export.JSONFileService;
import ru.anykeyers.service.impl.import_export.txt.TXTFileService;
import ru.anykeyers.service.impl.import_export.txt.domain.TXTContactMapper;
import ru.anykeyers.service.impl.import_export.xml.XMLFileService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Тестирование реализаций сервиса {@link FileService}
 * <ol>
 *     <li>{@link CSVFileService}</li>
 *     <li>{@link JSONFileService}</li>
 *     <li>{@link XMLFileService}</li>
 *     <li>{@link TXTFileService}</li>
 * </ol>
 */
public class FileServiceTest {

    private final List<Contact> expectedContacts = new ArrayList<>();

    @Before
    public void setUp() {
        expectedContacts.add(new Contact("1", "testUser", "testContact", "79999", 21, Gender.MAN, false));
        expectedContacts.add(new Contact("2", "testUser", "testContact2", "79998", 22, Gender.WOMAN, true));
        expectedContacts.add(new Contact("3", "testUser", "testContact3", "1122", 11, Gender.MAN, false));
        expectedContacts.add(new Contact("4", "testUser", "testContact4", "2211", 55, Gender.WOMAN, true));
    }

    /**
     * Тестирование сервиса по CSV файлам
     */
    @Test
    public void CSVFileServiceTest() throws IOException {
        FileService<Contact> fileService = new CSVFileService();
        File file = new File("src/test/resources/contacts.csv");
        File exportFile = Files.createTempFile("contacts", ".csv").toFile();
        initDataFromFileTest(fileService, file);
        saveOrUpdateFile(fileService, exportFile);
    }

    /**
     * Тестирование сервиса по JSON файлам
     */
    @Test
    public void JSONFileServiceTest() throws IOException {
        FileService<Contact> fileService = new JSONFileService();
        File file = new File("src/test/resources/contacts.json");
        File exportFile = Files.createTempFile("contacts", ".json").toFile();
        initDataFromFileTest(fileService, file);
        saveOrUpdateFile(fileService, exportFile);
    }

    /**
     * Тестирование сервиса по XML файлам
     */
    @Test
    public void XMLFileServiceTest() throws IOException {
        FileService<Contact> fileService = new XMLFileService();
        File file = new File("src/test/resources/contacts.xml");
        File exportFile = Files.createTempFile("contacts", ".xml").toFile();
        initDataFromFileTest(fileService, file);
        saveOrUpdateFile(fileService, exportFile);
    }

    /**
     * Тестирование сервиса по TXT файлам
     */
    @Test
    public void TXTFileServiceTest() throws IOException {
        FileService<Contact> fileService = new TXTFileService<>(new TXTContactMapper());
        File file = new File("src/test/resources/contacts.txt");
        File exportFile = Files.createTempFile("contacts", ".txt").toFile();
        initDataFromFileTest(fileService, file);
        saveOrUpdateFile(fileService, exportFile);
    }

    /**
     * Тестирование метода {@link FileService#initDataFromFile(File)}
     *
     * @param fileService конкретная реализация файлового сервиса
     * @param file файл с контактами
     */
    private void initDataFromFileTest(FileService<Contact> fileService, File file) {
        Collection<Contact> actualContacts = fileService.initDataFromFile(file);
        Assert.assertTrue(actualContacts.containsAll(expectedContacts));
    }

    /**
     * Тестирование метода {@link FileService#saveOrUpdateFile(File, Collection)}
     *
     * @param fileService конкретная реализация файлового сервиса
     * @param file файл, куда нужно сохранить коллекцию контактов
     */
    private void saveOrUpdateFile(FileService<Contact> fileService, File file) throws IOException {
        fileService.saveOrUpdateFile(file, expectedContacts);
        List<String> expectedContacts = FileUtils.readLines(file, StandardCharsets.UTF_8);
        List<String> actualContacts = FileUtils.readLines(file, StandardCharsets.UTF_8);
        Assert.assertTrue(actualContacts.containsAll(expectedContacts));
    }

}