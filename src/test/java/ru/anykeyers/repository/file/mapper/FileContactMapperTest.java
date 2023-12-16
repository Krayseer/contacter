package ru.anykeyers.repository.file.mapper;

import org.junit.Assert;
import org.junit.Test;
import ru.anykeyers.common.Mapper;
import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.domain.Gender;

/**
 * Тестирование класса {@link FileContactMapper}
 */
public class FileContactMapperTest {

    private final Mapper<Contact> contactMapper = new FileContactMapper();

    /**
     * Тестирование форматирования контакта в строку
     */
    @Test
    public void parseContactToStringTest() {
        // Подготовка
        Contact contact = new Contact("testUser", "testContact");
        contact.setPhoneNumber("7777777777");
        contact.setAge(10);
        contact.setGender(Gender.MAN);
        contact.setBlocked(false);

        // Действие
        String formattedContact = contactMapper.format(contact);

        // Проверка
        String expectedResult = String.format("testUser:id=%s;name=testContact;phone_number=7777777777;age=10;gender=MAN;block=false", contact.getId());
        Assert.assertEquals(expectedResult, formattedContact);
    }

    /**
     * Тестирование форматирования строки в контакт
     */
    @Test
    public void parseContactFromStringTest() {
        // Подготовка
        String contactString = "testUser:id=111;name=testContact;phone_number=7777777777;age=10;gender=MAN;block=false";

        // Действие
        Contact contact = contactMapper.parse(contactString);

        // Проверка
        Contact expectedContact = new Contact("testUser", "testContact");
        expectedContact.setId("111");
        expectedContact.setPhoneNumber("7777777777");
        expectedContact.setAge(10);
        expectedContact.setGender(Gender.MAN);
        expectedContact.setBlocked(false);
        Assert.assertEquals(expectedContact, contact);
    }

}