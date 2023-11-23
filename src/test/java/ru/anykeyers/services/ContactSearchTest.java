package ru.anykeyers.services;

import org.junit.Before;
import org.junit.Test;
import ru.anykeyers.dataOperations.kinds.SearchingKind;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.repositories.file.FileContactRepository;

import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.*;

public class ContactSearchTest {

    private SearchService contactSearch;

    private ContactRepository contactRepository;

    private User user = new User("nikita");

    private Contact firstContact = new Contact("nikita", "1", "Никита Бураков",
            19, "Мужчина", "UNBLOCK", "7906806");
    private Contact secondContact = new Contact("nikita", "2", "Иван Бураков",
            19, "Мужчина", "UNBLOCK", "7906");

    @Before
    public void setUp() throws Exception {
        File tempDbFile = Files.createTempFile("tempDbFile", ".txt").toFile();
        contactRepository = new FileContactRepository(tempDbFile.getPath());
        contactSearch = new SearchService(contactRepository);
    }

    /**
     * Тест метода {@link  SearchService#searchContact(User, String, SearchingKind)}
     */
    @Test
    public void testSearchContact() {
        contactRepository.saveOrUpdate(firstContact);
        contactRepository.saveOrUpdate(secondContact);
        String actualResult = contactSearch.searchContact(user, "Никита Бураков", SearchingKind.NAME);
        assertEquals("Найденный контакт: 'Имя: Никита Бураков, Возраст: 19, Пол: Мужчина, Номер телефона: 7906806'", actualResult);
        actualResult = contactSearch.searchContact(user, "7906806", SearchingKind.PHONE_NUMBER);
        assertEquals("Найденный контакт: 'Имя: Никита Бураков, Возраст: 19, Пол: Мужчина, Номер телефона: 7906806'", actualResult);

    }
}