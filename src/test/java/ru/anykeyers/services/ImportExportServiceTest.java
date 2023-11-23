package ru.anykeyers.services;

import org.junit.Test;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.ContactRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Тесты для сервиса импорта/экспорта контактов
 */
public class ImportExportServiceTest {

    private ImportExportService importExportService;

    private ContactRepository contactRepository;

    private final User user = new User("user");

    private final Contact contact = new Contact("user", "1234", "Андрей Иванов", 15, "Мужской", "UNBLOCK", "+7999999999999");

    private final Contact secondContact = new Contact("user", "st-445", "Коля Андреев", 21, "Мужской", "UNBLOCK", "+12345678912");

    /**
     * Тестирование импорта контактов из файла
     */
    @Test
    public void importTest() {
        File importFile = new File("none");

        String importResult = importExportService.importData(user, importFile.getPath());

        String expectedResult = String.format("Импорт контактов из файла '%s' успешно выполнен", importFile.getPath());
        assertEquals(expectedResult, importResult);
        assertEquals(Set.of(contact, secondContact), contactRepository.findByUsername(user.getUsername()));
    }

    /**
     * Тестирование экспорта контактов из файла
     */
    @Test
    public void exportTest() throws IOException {
        File expectedFile = new File("none");
        File exportFile = new File("none");

        contactRepository.saveOrUpdate(contact);
        contactRepository.saveOrUpdate(secondContact);

        String exportResult = importExportService.exportData(user, exportFile.getPath());

        String expectedResult = String.format("Экспорт контактов в файл '%s' успешно выполнен", exportFile.getPath());
        assertEquals(expectedResult, exportResult);
        assertTrue(isFilesEquals(expectedFile, exportFile));
    }

    /**
     * Проверка файлов на идентичность
     * @param firstFile первый файл
     * @param secondFile сравниваемый файл
     * @return true, если файлы равны, иначе false
     */
    private boolean isFilesEquals(File firstFile, File secondFile) throws IOException {
        byte[] firstFileData = Files.readAllBytes(firstFile.toPath());
        byte[] secondFileData = Files.readAllBytes(secondFile.toPath());
        return Arrays.equals(firstFileData, secondFileData);
    }


}
