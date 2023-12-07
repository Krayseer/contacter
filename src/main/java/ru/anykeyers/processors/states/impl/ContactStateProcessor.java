package ru.anykeyers.processors.states.impl;

import ru.anykeyers.contexts.Messages;
import ru.anykeyers.processors.states.BaseStateProcessor;
import ru.anykeyers.processors.states.domain.State;
import ru.anykeyers.domain.User;
import ru.anykeyers.domain.kinds.contact.ContactEditKind;
import ru.anykeyers.services.AuthenticationService;
import ru.anykeyers.services.ContactService;

import java.util.List;

/**
 * Обработчик состояний для контактов
 */
public class ContactStateProcessor extends BaseStateProcessor {

    private final ContactService contactService;

    private final AuthenticationService authenticationService;

    private final Messages messages;

    public ContactStateProcessor(AuthenticationService authenticationService,
                                 ContactService contactService) {
        this.contactService = contactService;
        this.authenticationService = authenticationService;
        messages = new Messages();
        registerStatesHandlers();
    }

    /**
     * Регистрация состояний и их обработчиков
     */
    private void registerStatesHandlers() {
        registerHandler(State.ADD_CONTACT, (user, contactName) -> {
            String result = contactService.addContact(user, contactName);
            user.clearState();
            authenticationService.saveOrUpdateUser(user);
            return result;
        });
        registerHandler(State.EDIT_CONTACT, (user, contactNameToEdit) -> {
            if (!contactService.existsContact(user, contactNameToEdit)) {
                return messages.getMessageByKey("contact.not-exists", contactNameToEdit);
            }
            user.setEditInfo(contactNameToEdit);
            user.setState(State.EDIT_CONTACT_FIELD);
            authenticationService.saveOrUpdateUser(user);
            return messages.getMessageByKey("contact.state.edit.field");
        });
        registerHandler(State.EDIT_CONTACT_FIELD, (user, field) -> {
            switch (field.toLowerCase()) {
                case ContactEditKind.NAME -> {
                    return processEditContact(user, State.EDIT_CONTACT_NAME, "contact.state.edit.name");
                }
                case ContactEditKind.PHONE -> {
                    return processEditContact(user, State.EDIT_CONTACT_PHONE, "contact.state.edit.phone");
                }
                case ContactEditKind.AGE -> {
                    return processEditContact(user, State.EDIT_CONTACT_AGE, "contact.state.edit.age");
                }
                case ContactEditKind.GENDER -> {
                    return processEditContact(user, State.EDIT_CONTACT_GENDER, "contact.state.edit.gender");
                }
                case ContactEditKind.BLOCK -> {
                    return processEditContact(user, State.EDIT_CONTACT_BLOCK, "contact.state.edit.block");
                }
            }
            return messages.getMessageByKey("commons.bad-argument");
        });
        List<State> editContactFieldsStates = List.of(
                State.EDIT_CONTACT_NAME, State.EDIT_CONTACT_PHONE, State.EDIT_CONTACT_AGE,
                State.EDIT_CONTACT_GENDER, State.EDIT_CONTACT_BLOCK, State.EDIT_CONTACT_UNBLOCK
        );
        editContactFieldsStates.forEach(state -> registerHandler(state, (user, newValue) -> {
            String result = contactService.editContact(user, newValue);
            if (!result.equals(messages.getMessageByKey("contact.successful-edit-name", user.getEditInfo()))) {
                return result;
            }
            user.clearState();
            authenticationService.saveOrUpdateUser(user);
            return result;
        }));
        registerHandler(State.DELETE_CONTACT, ((user, contactName) -> {
            String result = contactService.deleteContact(user, contactName);
            if (!result.equals(messages.getMessageByKey("contact.successful-delete", contactName))) {
                return result;
            }
            user.clearState();
            authenticationService.saveOrUpdateUser(user);
            return result;
        }));
    }

    /**
     * Изменение состояния контакта
     * @param user пользователь
     * @param state состояние, сигнализирующее что нужно изменить у контакта
     * @param messageKey ключ сообщения успешного выполнения операции
     * @return результат изменения контакта
     */
    private String processEditContact(User user, State state, String messageKey) {
        user.setState(state);
        authenticationService.saveOrUpdateUser(user);
        return messages.getMessageByKey(messageKey);
    }

}
