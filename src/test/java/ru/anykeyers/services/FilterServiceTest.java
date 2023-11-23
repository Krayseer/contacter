package ru.anykeyers.services;

import org.junit.Before;
import org.junit.Test;
import ru.anykeyers.dataOperations.kinds.FilteringKind;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.repositories.file.FileContactRepository;

import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.*;

public class FilterServiceTest {

    private FilterService filterService;

    private ContactRepository contactRepository;

    private User user = new User("nikita");

    private Contact firstContact = new Contact("nikita", "1", "Никита Бураков",
            35, "Женщина", "BLOCK", "7906806");
    private Contact secondContact = new Contact("nikita", "2", "Иван Бураков",
            19, "Мужчина", "UNBLOCK", "7906");

    @Before
    public void setUp() throws Exception {
        File tempDbFile = Files.createTempFile("tempDbFile", ".txt").toFile();
        this.contactRepository = new FileContactRepository(tempDbFile.getPath());
        this.filterService = new FilterService(contactRepository);
    }

    /**
     * Тест метода {@link FilterService#filterContacts(User, String, FilteringKind)}
     */
    @Test
    public void testFilterContacts() {
        contactRepository.saveOrUpdate(firstContact);
        contactRepository.saveOrUpdate(secondContact);
        String actualResult = filterService.filterContacts(user, "BLOCK", FilteringKind.BLOCK);
        assertEquals("Имя: Никита Бураков, Возраст: 35, Пол: Женщина, Номер телефона: 7906806", actualResult);
        actualResult = filterService.filterContacts(user, "UNBLOCK", FilteringKind.UNBLOCK);
        assertEquals("Имя: Иван Бураков, Возраст: 19, Пол: Мужчина, Номер телефона: 7906", actualResult);
        actualResult = filterService.filterContacts(user, "Мужчина", FilteringKind.GENDER);
        assertEquals("Имя: Иван Бураков, Возраст: 19, Пол: Мужчина, Номер телефона: 7906", actualResult);
        actualResult = filterService.filterContacts(user, ">,25", FilteringKind.AGE);
        assertEquals("Имя: Никита Бураков, Возраст: 35, Пол: Женщина, Номер телефона: 7906806", actualResult);
    }
}