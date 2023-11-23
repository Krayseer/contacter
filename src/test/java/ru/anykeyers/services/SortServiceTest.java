package ru.anykeyers.services;

import org.junit.Before;
import org.junit.Test;
import ru.anykeyers.dataOperations.kinds.SortingKind;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.repositories.file.FileContactRepository;

import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.*;

public class SortServiceTest {

    private SortService sortService;

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
        this.sortService = new SortService(contactRepository);
    }

    /**
     * Тест метода {@link SortService#sortContacts(User, String, SortingKind)}
     */
    @Test
    public void sortContacts() {
        contactRepository.saveOrUpdate(firstContact);
        contactRepository.saveOrUpdate(secondContact);
        String actualResult = sortService.sortContacts(user, "asc", SortingKind.NAME);
        assertEquals("Имя: Иван Бураков, Возраст: 19, Пол: Мужчина, Номер телефона: 7906\n" +
                "Имя: Никита Бураков, Возраст: 35, Пол: Женщина, Номер телефона: 7906806", actualResult);
        actualResult = sortService.sortContacts(user, "desc", SortingKind.NAME);
        assertEquals("Имя: Никита Бураков, Возраст: 35, Пол: Женщина, Номер телефона: 7906806\n" +
                "Имя: Иван Бураков, Возраст: 19, Пол: Мужчина, Номер телефона: 7906", actualResult);
        actualResult = sortService.sortContacts(user, "desc", SortingKind.AGE);
        assertEquals("Имя: Никита Бураков, Возраст: 35, Пол: Женщина, Номер телефона: 7906806\n" +
                "Имя: Иван Бураков, Возраст: 19, Пол: Мужчина, Номер телефона: 7906", actualResult);
        actualResult = sortService.sortContacts(user, "asc", SortingKind.AGE);
        assertEquals("Имя: Иван Бураков, Возраст: 19, Пол: Мужчина, Номер телефона: 7906\n" +
                "Имя: Никита Бураков, Возраст: 35, Пол: Женщина, Номер телефона: 7906806", actualResult);
    }
}