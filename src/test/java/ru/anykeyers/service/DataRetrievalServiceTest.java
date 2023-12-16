package ru.anykeyers.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.exception.group.GroupNotExistsException;
import ru.anykeyers.repository.GroupRepository;
import ru.anykeyers.service.impl.DataRetrievalServiceImpl;

import java.util.Optional;

/**
 * Тесты для сервиса {@link DataRetrievalService}
 */
@RunWith(MockitoJUnitRunner.class)
public class DataRetrievalServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private DataRetrievalServiceImpl dataRetrievalService;

    /**
     * Тестирование получения контактов в несуществующей группе
     */
    @Test
    public void getAllGroupContactsTest() {
        // Подготовка
        User user = new User("testUser");
        String nonExistsGroupName = "non-exists";

        // Действие
        Mockito.when(groupRepository.getByUsernameAndName(user.getUsername(), nonExistsGroupName))
                .thenReturn(Optional.empty());

        // Проверка
        Exception groupNotExistsException = Assert.assertThrows(
                GroupNotExistsException.class, () -> dataRetrievalService.getAllGroupContacts(user, nonExistsGroupName)
        );
        Assert.assertEquals("Не удалось найти группу 'non-exists'", groupNotExistsException.getMessage());
    }

}
