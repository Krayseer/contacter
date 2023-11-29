package ru.anykeyers.processors.states.impl;

import ru.anykeyers.processors.states.domain.State;
import ru.anykeyers.domain.User;
import ru.anykeyers.processors.states.BaseStateProcessor;
import ru.anykeyers.bots.Bot;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.services.AuthenticationService;
import ru.anykeyers.services.ContactService;

import java.util.List;

import static ru.anykeyers.processors.states.domain.State.*;

/**
 * Класс обработчиков состояний контактов
 */
public class ContactStateProcessor extends BaseStateProcessor {

    private final ContactService contactService;

    public ContactStateProcessor(AuthenticationService authenticationService) {
        super(authenticationService);
        ContactRepository contactRepository = repositoryFactory.createContactRepository();
        contactService = new ContactService(contactRepository);
    }

    @Override
    public void registerStatesHandlers() {
        stateHandlers.put(ADD_CONTACT, (user, message) -> {
            String result = contactService.addContact(user, message);
            user.clearState();
            authenticationService.saveOrUpdateUser(user);
            return result;
        });
        stateHandlers.put(EDIT_CONTACT, (user, message) -> {
            if (!contactService.existsContact(user, message)) {
                return messages.getMessageByKey("contact.not-exists", message);
            }
            user.setContactNameToEdit(message);
            user.setState(EDIT_CONTACT_FIELD);
            authenticationService.saveOrUpdateUser(user);
            return messages.getMessageByKey("contact.state.edit.field");
        });
        stateHandlers.put(EDIT_CONTACT_FIELD, (user, message) -> {
            return switch (message.toLowerCase()) {
                case ContactEditField.NAME -> processFieldEdit(user, EDIT_CONTACT_NAME, "contact.state.edit.name");
                case ContactEditField.PHONE -> processFieldEdit(user, EDIT_CONTACT_PHONE, "contact.state.edit.phone");
                default -> messages.getMessageByKey("contact.state.edit.bad-argument");
            };
        });
        List<State> editContactFieldsStates = List.of(EDIT_CONTACT_NAME, EDIT_CONTACT_PHONE);
        editContactFieldsStates.forEach(state -> stateHandlers.put(state, (user, message) -> {
            String result = contactService.editContact(user, message);
            user.clearState();
            authenticationService.saveOrUpdateUser(user);
            return result;
        }));
        stateHandlers.put(DELETE_CONTACT, ((user, message) -> {
            String result = contactService.deleteContact(user, message);
            if (!result.equals(messages.getMessageByKey("contact.successful-delete", message))) {
                return null;
            }
            user.clearState();
            authenticationService.saveOrUpdateUser(user);
            return result;
        }));
    }

    private String processFieldEdit(User user, State state, String messageKey) {
        user.setState(state);
        authenticationService.saveOrUpdateUser(user);
        return messages.getMessageByKey(messageKey);
    }

    /**
     * Класс, содержащий информацию о действиях, которые можно сделать с контактом
     */
    private static final class ContactEditField {

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
