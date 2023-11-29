package ru.anykeyers.processors.states.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ru.anykeyers.bots.Bot;
import ru.anykeyers.bots.BotType;
import ru.anykeyers.contexts.Messages;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.User;
import ru.anykeyers.factories.StateProcessorFactory;
import ru.anykeyers.processors.commands.Command;
import ru.anykeyers.processors.commands.CommandProcessor;
import ru.anykeyers.services.AuthenticationService;
import ru.anykeyers.services.ContactService;
import ru.anykeyers.services.GroupService;

import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

// TODO: 29.11.2023 Нужно доделать 
@RunWith(MockitoJUnitRunner.class)
public class ContactStateProcessorTest {

    @Mock
    private Bot bot;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private StateProcessorFactory stateHandlerFactory;

    private Messages messages = new Messages();

    @Mock
    private ContactService contactService;

    @Mock
    private GroupService groupService;

    @InjectMocks
    private CommandProcessor commandProcessor;

    @Test
    public void testProcessCommand_HelpCommand() {
        User user = new User("testUser");
        when(authenticationService.getUserByUsername(user.getUsername())).thenReturn(user);
        doNothing().when(authenticationService).saveOrUpdateUser(user);

        commandProcessor.processMessage(user.getUsername(), "/help", BotType.CONSOLE);

        verify(authenticationService, times(1)).saveOrUpdateUser(user);
        verify(bot, times(1)).sendMessage(user.getChatIdByAppType(), Command.getAllCommands());
    }

    @Test
    public void testProcessCommand_getAllCommands() {
        // Mocking
        User user = new User("testUser");
        when(authenticationService.getUserByUsername("testUser")).thenReturn(user);

        // Assuming the command is "get_contacts"
        when(contactService.getAllContacts(user)).thenReturn(Set.of(
                new Contact("John Doe", "john@example.com"),
                new Contact("Jane Doe", "jane@example.com")
        ));

        // Testing
        commandProcessor.processMessage("testUser", "/get_contacts", BotType.CONSOLE);

        // Verifying
        verify(bot, times(1)).sendMessage(eq(user.getChatIdByAppType()), anyString());

        // Check the captured argument to the sendMessage method
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(bot).sendMessage(eq(user.getChatIdByAppType()), argumentCaptor.capture());

        // Assert the result
        String processResult = argumentCaptor.getValue();
        assertEquals("John Doe john@example.com\nJane Doe jane@example.com", processResult);
    }

//    @Test
//    public void testProcessCommand_InvalidCommand() {
//        // Arrange
//        User user = new User("testUser", "password");
//        when(authenticationService.getUserByUsername(user.getUsername())).thenReturn(user);
//        when(stateHandlerFactory.getStateProcessorByType(StateType.NONE)).thenReturn(new NoneStateProcessor(bot, authenticationService));
//        doNothing().when(authenticationService).saveOrUpdateUser(user);
//        when(messages.getMessageByKey("command.not-exists")).thenReturn("Command not exists");
//
//        // Act
//        commandProcessor.processMessage(user.getUsername(), "/invalidCommand", BotType.TELEGRAM);
//
//        // Assert
//        verify(authenticationService, times(1)).saveOrUpdateUser(user);
//        verify(bot, times(1)).sendMessage(user.getChatIdByAppType(), "Command not exists");
//    }
//
//    @Test
//    public void testProcessState_InvalidState() {
//        // Arrange
//        User user = new User("testUser", "password");
//        user.setState(State.EDIT_CONTACT_NAME);
//        when(authenticationService.getUserByUsername(user.getUsername())).thenReturn(user);
//        doNothing().when(authenticationService).saveOrUpdateUser(user);
//        when(stateHandlerFactory.getStateProcessorByType(StateType.CONTACT)).thenReturn(new ContactStateProcessor(bot, authenticationService));
//        when(messages.getMessageByKey("contact.state.edit.bad-argument")).thenReturn("Invalid argument");
//
//        // Act
//        commandProcessor.processMessage(user.getUsername(), "invalidMessage", BotType.TELEGRAM);
//
//        // Assert
//        verify(authenticationService, times(1)).saveOrUpdateUser(user);
//        verify(bot, times(1)).sendMessage(user.getChatIdByAppType(), "Invalid argument");
//    }
}