package ru.anykeyers.repositories.file.parsers;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.Group;
import ru.anykeyers.repositories.ContactRepository;

/**
 * Тестирование класса {@link FileGroupParser}
 */
@RunWith(MockitoJUnitRunner.class)
public class FileGroupMapperTest {

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private FileGroupParser formatter;

    /**
     * Тестирование парсинга группы в строку
     */
    @Test
    public void parseGroupToStringTest() {
        // Подготовка
        Contact contact = new Contact("testUser", "testContact");
        Group group = new Group("testUser", "testGroup");
        group.addContactInGroup(contact);

        // Действие
        String formattedGroup = formatter.parseTo(group);

        // Проверка
        String expectedResult = String.format("testUser:id=%s;name=testGroup;contacts=%s", group.getId(), contact.getId());
        Assert.assertEquals(expectedResult, formattedGroup);
    }

    /**
     * Тестирование парсинга из строки в группу
     */
    @Test
    public void parseGroupFromStringTest() {
        // Подготовка
        Contact contact = new Contact("testUser", "testContact");
        String groupString = "testUser:id=111;name=testGroup;contacts=222";

        // Действие
        Mockito.when(contactRepository.findByUsernameAndId("testUser", "222")).thenReturn(contact);
        Group group = formatter.parseFrom(groupString);

        // Проверка
        Group expectedGroup = new Group("testUser", "testGroup");
        expectedGroup.setId("111");
        expectedGroup.addContactInGroup(contact);
        Assert.assertEquals(expectedGroup, group);
    }

}