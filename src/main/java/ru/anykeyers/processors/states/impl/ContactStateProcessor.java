package ru.anykeyers.processors.states.impl;

import ru.anykeyers.contexts.Messages;
import ru.anykeyers.processors.states.BaseStateProcessor;
import ru.anykeyers.processors.states.domain.State;
import ru.anykeyers.domain.User;
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
            user.setContactNameToEdit(contactNameToEdit);
            user.setState(State.EDIT_CONTACT_FIELD);
            authenticationService.saveOrUpdateUser(user);
            return messages.getMessageByKey("contact.state.edit.field");
        });
        registerHandler(State.EDIT_CONTACT_FIELD, (user, field) -> {
            switch (field.toLowerCase()) {
                case ContactEditField.NAME -> {
                    return processEditContact(user, State.EDIT_CONTACT_NAME, "contact.state.edit.name");
                }
                case ContactEditField.PHONE -> {
                    return processEditContact(user, State.EDIT_CONTACT_PHONE, "contact.state.edit.phone");
                }
            }
            return messages.getMessageByKey("argument.bad");
        });
        List<State> editContactFieldsStates = List.of(State.EDIT_CONTACT_NAME, State.EDIT_CONTACT_PHONE);
        editContactFieldsStates.forEach(state -> registerHandler(state, (user, newValue) -> {
            String result = contactService.editContact(user, newValue);
            if (!result.equals(messages.getMessageByKey("contact.successful-edit-name", user.getContactNameToEdit()))) {
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

    /**
     * Класс, содержащий информацию о действиях, которые можно сделать с контактом
     */
    private final class ContactEditField {

        /**
         * Изменение имени
         */
        public final static String NAME = "1";

        /**
         * Изменение номера телефона
         */
        public final static String PHONE = "2";

    }

}
