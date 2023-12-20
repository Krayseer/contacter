package ru.anykeyers.service.import_export.txt.domain;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.domain.entity.Group;
import ru.anykeyers.repository.ContactRepository;
import ru.anykeyers.service.impl.import_export.txt.domain.TXTGroupMapper;

import java.util.Optional;

/**
 * Тестирование класса {@link TXTGroupMapper}
 */
@RunWith(MockitoJUnitRunner.class)
public class TXTGroupMapperTest {

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private TXTGroupMapper mapper;

    /**
     * Тестирование форматирования группы в строку
     */
    @Test
    public void parseGroupToStringTest() {
        // Подготовка
        Contact contact = new Contact("testUser", "testContact");
        Group group = new Group("testUser", "testGroup");
        group.addContactInGroup(contact);

        // Действие
        String formattedGroup = mapper.format(group);

        // Проверка
        String expectedResult = String.format("testUser:id=%s;name=testGroup;contacts=%s", group.getId(), contact.getId());
        Assert.assertEquals(expectedResult, formattedGroup);
    }

    /**
     * Тестирование форматирования строки в группу
     */
    @Test
    public void parseGroupFromStringTest() {
        // Подготовка
        Contact contact = new Contact("testUser", "testContact");
        String groupString = "testUser:id=111;name=testGroup;contacts=222";

        // Действие
        Mockito.when(contactRepository.getByUsernameAndId("testUser", "222")).thenReturn(Optional.of(contact));
        Group group = mapper.parse(groupString);

        // Проверка
        Group expectedGroup = new Group("testUser", "testGroup");
        expectedGroup.setId("111");
        expectedGroup.addContactInGroup(contact);
        Assert.assertEquals(expectedGroup, group);
    }

}