package ru.anykeyers.repositories.file.mapper;

import org.junit.Assert;
import org.junit.Test;
import ru.anykeyers.domain.Contact;

/**
 * Тестирование класса {@link ContactMapper}
 */
public class FileContactMapperTest {

    private final ContactMapper mapper;

    public FileContactMapperTest() {
        mapper = new ContactMapper();
    }

    /**
     * Тестирование парсинга контакта в строку
     */
    @Test
    public void parseContactToStringTest() {
        // Подготовка
        Contact contact = new Contact("testUser", "testContact");
        contact.setPhoneNumber("7777777777");

        // Действие
        String formattedContact = mapper.format(contact);

        // Проверка
        String expectedResult = String.format("testUser:id=%s;name=testContact;phone_number=7777777777", contact.getId());
        Assert.assertEquals(expectedResult, formattedContact);
    }

    /**
     * Тестирование парсинга строки в контакт
     */
    @Test
    public void parseContactFromStringTest() {
        // Подготовка
        String contactString = "testUser:id=111;name=testContact;phone_number=7777777777";

        // Действие
        Contact contact = mapper.parse(contactString);

        // Проверка
        Contact expectedContact = new Contact("testUser", "testContact");
        expectedContact.setId("111");
        expectedContact.setPhoneNumber("7777777777");
        Assert.assertEquals(expectedContact, contact);
    }

}