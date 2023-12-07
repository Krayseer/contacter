package ru.anykeyers.repositories.file.parsers;

import org.junit.Assert;
import org.junit.Test;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.Gender;

/**
 * Тестирование класса {@link FileContactParser}
 */
public class FileContactMapperTest {

    private final FileContactParser formatter;

    public FileContactMapperTest() {
        formatter = new FileContactParser();
    }

    /**
     * Тестирование парсинга контакта в строку
     */
    @Test
    public void parseContactToStringTest() {
        // Подготовка
        Contact contact = new Contact("testUser", "testContact");
        contact.setPhoneNumber("7777777777");
        contact.setAge(10);
        contact.setGender(Gender.MAN);
        contact.setBlock(false);

        // Действие
        String formattedContact = formatter.parseTo(contact);

        // Проверка
        String expectedResult = String.format("testUser:id=%s;name=testContact;phone_number=7777777777;age=10;gender=MAN;block=false", contact.getId());
        Assert.assertEquals(expectedResult, formattedContact);
    }

    /**
     * Тестирование парсинга строки в контакт
     */
    @Test
    public void parseContactFromStringTest() {
        // Подготовка
        String contactString = "testUser:id=111;name=testContact;phone_number=7777777777;age=10;gender=MAN;block=false";

        // Действие
        Contact contact = formatter.parseFrom(contactString);

        // Проверка
        Contact expectedContact = new Contact("testUser", "testContact");
        expectedContact.setId("111");
        expectedContact.setPhoneNumber("7777777777");
        expectedContact.setAge(10);
        expectedContact.setGender(Gender.MAN);
        expectedContact.setBlock(false);
        Assert.assertEquals(expectedContact, contact);
    }

}