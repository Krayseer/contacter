package ru.anykeyers.processor.state.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.anykeyers.domain.Message;
import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.processor.state.domain.State;
import ru.anykeyers.service.ContactService;
import ru.anykeyers.service.UserStateService;

import java.io.File;

/**
 * Тесты для класса {@link ImportExportStateProcessor}
 */
@RunWith(MockitoJUnitRunner.class)
public class ImportExportStateProcessorTest {

    @Mock
    private ContactService contactService;

    @Mock
    private UserStateService userStateService;

    @InjectMocks
    private ImportExportStateProcessor importExportStateProcessor;

    /**
     * Обработка состояния по импорту контактов
     */
    @Test
    public void handleImportContactsTest() {
        // Подготовка
        User user = new User("testUser");
        StateInfo userStateInfo = new StateInfo();
        userStateInfo.setState(State.IMPORT);

        // Действие
        Mockito.when(userStateService.getUserState(user)).thenReturn(userStateInfo);
        Message message = importExportStateProcessor.processState(user, "/nothing");

        // Проверка
        Assert.assertEquals(State.NONE, userStateInfo.getState());
        Assert.assertEquals("Импорт контактов успешно выполнен", message.getText());
        Mockito.verify(contactService, Mockito.times(1)).importContacts(user, new File("/nothing"));
    }

    /**
     * Обработка состояния по экспорту контактов
     */
    @Test
    public void handleExportContactsTest() {
        // Подготовка
        User user = new User("testUser");
        StateInfo userStateInfo = new StateInfo();
        userStateInfo.setState(State.EXPORT);

        // Действие: ввод некорректного параметра
        Mockito.when(userStateService.getUserState(user)).thenReturn(userStateInfo);
        Message errorMessage = importExportStateProcessor.processState(user, "5");

        // Проверка
        Assert.assertEquals("Введен неверный параметр", errorMessage.getText());
        Assert.assertEquals(State.EXPORT, userStateInfo.getState());

        // Действие: ввод корректного параметра
        Mockito.when(userStateService.getUserState(user)).thenReturn(userStateInfo);
        Message message = importExportStateProcessor.processState(user, "3");

        // Проверка
        Assert.assertEquals(State.NONE, userStateInfo.getState());
        Assert.assertNotNull(message.getFile());
    }

}
