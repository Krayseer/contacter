package ru.anykeyers.services;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.Gender;
import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.repositories.file.FileContactRepository;
import ru.anykeyers.services.impl.ImportExportServiceImpl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Тесты для сервиса импорта/экспорта контактов
 */
public class ImportExportServiceTest {

    private ImportExportServiceImpl importExportService;

    private ContactRepository contactRepository;

    private final User user = new User("user");

    private final Contact contact = new Contact(
            "1234", "user", "Андрей Иванов", "+7999999999999", 15, Gender.MAN, false
    );

    private final Contact secondContact = new Contact(
            "st-445", "user", "Коля Андреев", "+12345678912", 20, Gender.MAN, false
    );

    private final File contactsFileJSON = new File("src/test/resources/files/contacts.json");

    private final File contactsFileXML = new File("src/test/resources/files/contacts.xml");

    private final File contactsFileCSV = new File("src/test/resources/files/contacts.csv");

    private final File contactsFileTXT = new File("src/test/resources/files/contacts.txt");

    @Before
    public void setUp() throws IOException {
        File tempDbFile = Files.createTempFile("tempDbFile", ".txt").toFile();
        contactRepository = new FileContactRepository(tempDbFile.getPath());
        importExportService = new ImportExportServiceImpl(contactRepository);
    }

    /**
     * Тест импорта в несуществующий файл
     */
    @Test
    public void importNonExistsFileTest() {
        String importResultNotExistsFile = importExportService.importData(user, "not-exists");
        assertEquals("Ошибка импорта контактов", importResultNotExistsFile);
    }

    /**
     * Тест экспорта в несуществующий файл
     */
    @Test
    public void exportNonExistsFileTest() {
        String exportResultNotExistsFile = importExportService.exportData(user, "not-exists");
        assertEquals("Ошибка экспорта контактов", exportResultNotExistsFile);
    }

    /**
     * Тест импорта контактов из JSON
     */
    @Test
    public void importJSONTest() {
        importTest(contactsFileJSON);
    }

    /**
     * Тест экспорта контактов в JSON
     */
    @Test
    public void exportJSONTest() throws IOException {
        File tempJsonFile = Files.createTempFile("temp_json", ".json").toFile();
        exportTest(contactsFileJSON, tempJsonFile);
    }

    /**
     * Тест импорта контактов из XML
     */
    @Test
    public void importXMLTest() {
        importTest(contactsFileXML);
    }

    /**
     * Тест экспорта контактов в XML
     */
    @Test
    public void exportXMLTest() throws IOException {
        File tempXmlFile = Files.createTempFile("temp_xml", ".xml").toFile();
        exportTest(contactsFileXML, tempXmlFile);

    }

    /**
     * Тест импорта контактов в CSV
     */
    @Test
    public void importCSVTest(){
        importTest(contactsFileCSV);
    }

    /**
     * Тест экспорта контактов в CSV
     */
    @Test
    public void exportCSVTest() throws IOException {
        File tempCsvFile = Files.createTempFile("temp_csv", ".csv").toFile();
        exportTest(contactsFileCSV, tempCsvFile);
    }

    /**
     * Тест импорта контактов в TXT
     */
    @Test
    public void importTXTTest() {
        importTest(contactsFileTXT);
    }

    /**
     * Тест экспорта контактов в TXT
     */
    @Test
    public void exportTXTTest() throws IOException {
        File tempTxtFile = Files.createTempFile("temp_txt", ".txt").toFile();
        exportTest(contactsFileTXT, tempTxtFile);
    }

    /**
     * Тестирование импорта контактов из файла
     * @param file файл с контактами
     */
    private void importTest(File file) {
        String importResult = importExportService.importData(user, file.getPath());

        String expectedResult = String.format("Импорт контактов из файла '%s' успешно выполнен", file.getPath());
        assertEquals(expectedResult, importResult);
        assertEquals(Set.of(contact, secondContact), contactRepository.findByUsername(user.getUsername()));
    }

    /**
     * Тестирование экспорта контактов из файла
     * @param expectedFile ожидаемый файл, который должен быть получен в результате
     * @param tempFile временный файл с экспортированными контактами
     */
    private void exportTest(File expectedFile, File tempFile) throws IOException {
        contactRepository.saveOrUpdate(contact);
        contactRepository.saveOrUpdate(secondContact);

        String exportResult = importExportService.exportData(user, tempFile.getPath());

        String expectedResult = String.format("Экспорт контактов в файл '%s' успешно выполнен", tempFile.getPath());
        assertEquals(expectedResult, exportResult);
        assertTrue(isFilesEquals(expectedFile, tempFile));
    }

    /**
     * Проверка файлов на идентичность
     * @param firstFile первый файл
     * @param secondFile сравниваемый файл
     * @return true, если файлы равны, иначе false
     */
    private boolean isFilesEquals(File firstFile, File secondFile) throws IOException {
        List<String> firstFileLines = FileUtils.readLines(firstFile, StandardCharsets.UTF_8);
        List<String> secondFileLines = FileUtils.readLines(secondFile, StandardCharsets.UTF_8);
        if (firstFileLines.size() != secondFileLines.size()) {
            return false;
        }
        for (String line : firstFileLines) {
            if (!secondFileLines.contains(line)) {
                return false;
            }
        }
        return true;
    }


}
