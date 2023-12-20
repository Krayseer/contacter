package ru.anykeyers.service;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.anykeyers.domain.FileFormat;
import ru.anykeyers.domain.Gender;
import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.factory.ImportExportMapperFactory;
import ru.anykeyers.repository.ContactRepository;
import ru.anykeyers.service.impl.ContactImportExportService;
import ru.anykeyers.service.impl.import_export.txt.TXTFileService;
import ru.anykeyers.service.impl.import_export.txt.domain.TXTContactMapper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;

/**
 * Тестирование сервиса {@link ContactImportExportService}
 */
@RunWith(MockitoJUnitRunner.class)
public class ContactImportExportServiceTest {

    @Mock
    private ImportExportMapperFactory importExportMapperFactory;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private FileService<Contact> fileService;

    @InjectMocks
    private ContactImportExportService contactImportExportService;

    private User user;

    private Set<Contact> contacts;

    @Before
    public void setUp() {
        user = new User("testUser");

        Contact firstContact = new Contact("testUser", "testContact");
        firstContact.setId("1");
        firstContact.setPhoneNumber("79999");
        firstContact.setAge(21);
        firstContact.setGender(Gender.MAN);
        firstContact.setBlocked(false);

        Contact secondContact = new Contact("testUser", "testContact2");
        secondContact.setId("2");
        secondContact.setPhoneNumber("79998");
        secondContact.setAge(22);
        secondContact.setGender(Gender.WOMAN);
        secondContact.setBlocked(true);

        contacts = Set.of(firstContact, secondContact);
    }

    /**
     * Тестирование импорта данных в файл
     */
    @Test
    public void importDataTest() {
        // Подготовка
        File file = new File("src/test/resources/contacts.txt");

        // Действие
        Mockito.when(importExportMapperFactory.getServiceByFormat(Mockito.any(FileFormat.class))).thenReturn(fileService);
        Mockito.when(fileService.initDataFromFile(Mockito.any(File.class))).thenReturn(contacts);
        contactImportExportService.importData(user, file);

        // Проверка
        Mockito.verify(contactRepository, Mockito.times(2)).saveOrUpdate(Mockito.any());
    }

    /**
     * Тестирование экспорта данных из файла
     */
    @Test
    public void exportDataTest() throws IOException {
        // Подготовка
        File actualFile = Files.createTempFile("contacts", ".txt").toFile();

        // Действие
        FileService<Contact> fileService = new TXTFileService<>(new TXTContactMapper());
        Mockito.when(importExportMapperFactory.getServiceByFormat(Mockito.any())).thenReturn(fileService);
        Mockito.when(contactRepository.findByUsername(user.getUsername()))
                .thenReturn(contacts);
        contactImportExportService.exportData(user, actualFile);

        // Проверка
        File expectedFile = new File("src/test/resources/contacts.txt");
        List<String> expectedContacts = FileUtils.readLines(expectedFile, StandardCharsets.UTF_8);
        List<String> actualContacts = FileUtils.readLines(actualFile, StandardCharsets.UTF_8);
        Assert.assertEquals(2, actualContacts.size());
        actualContacts.forEach(actualContact -> Assert.assertTrue(expectedContacts.contains(actualContact)));
    }

}
