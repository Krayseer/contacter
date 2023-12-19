package ru.anykeyers.processor.state.impl;

import ru.anykeyers.common.Messages;
import ru.anykeyers.domain.Message;
import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.exception.BadArgumentException;
import ru.anykeyers.processor.state.BaseStateProcessor;
import ru.anykeyers.processor.state.domain.State;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.processor.state.domain.kinds.contact.ContactEditKind;
import ru.anykeyers.service.ContactService;
import ru.anykeyers.service.UserStateService;

import java.util.List;

/**
 * Обработчик состояний для контактов
 */
public class ContactStateProcessor extends BaseStateProcessor {

    private final Messages messages = Messages.getInstance();

    private final ContactService contactService;

    public ContactStateProcessor(UserStateService userStateService,
                                 ContactService contactService) {
        super(userStateService);
        this.contactService = contactService;
        registerStatesHandlers();
    }

    /**
     * Регистрация состояний и их обработчиков
     */
    private void registerStatesHandlers() {
        registerHandler(State.ADD_CONTACT, (user, contactName) -> {
            try {
                contactService.addContact(user, contactName);
            } catch (Exception ex) {
                return new Message(ex.getMessage());
            }
            StateInfo userStateInfo = userStateService.getUserState(user);
            userStateInfo.clear();
            return new Message(messages.getMessageByKey("contact.successful-save", contactName));
        });
        registerHandler(State.EDIT_CONTACT, (user, contactNameToEdit) -> {
            if (!contactService.existsContact(user, contactNameToEdit)) {
                return new Message(messages.getMessageByKey("contact.exception.name.not-exists", contactNameToEdit));
            }
            StateInfo userStateInfo = userStateService.getUserState(user);
            userStateInfo.setEditInfo(contactNameToEdit);
            userStateInfo.setState(State.EDIT_CONTACT_FIELD);
            return new Message(messages.getMessageByKey("contact.state.edit.field"));
        });
        registerHandler(State.EDIT_CONTACT_FIELD, (user, field) -> {
            switch (field) {
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
            throw new BadArgumentException();
        });
        List<State> editContactFieldsStates = List.of(
                State.EDIT_CONTACT_NAME, State.EDIT_CONTACT_PHONE, State.EDIT_CONTACT_AGE,
                State.EDIT_CONTACT_GENDER, State.EDIT_CONTACT_BLOCK
        );
        editContactFieldsStates.forEach(state -> registerHandler(state, (user, newValue) -> {
            StateInfo userStateInfo = userStateService.getUserState(user);
            String contactName = userStateInfo.getEditInfo();
            try {
                contactService.editContact(user, userStateInfo, newValue);
            } catch (Exception ex) {
                return new Message(ex.getMessage());
            }
            userStateInfo.clear();
            return new Message(messages.getMessageByKey("contact.successful-edit", contactName));
        }));
        registerHandler(State.DELETE_CONTACT, ((user, contactName) -> {
            try {
                contactService.deleteContact(user, contactName);
            } catch (Exception ex) {
                return new Message(ex.getMessage());
            }
            StateInfo userStateInfo = userStateService.getUserState(user);
            userStateInfo.clear();
            return new Message(messages.getMessageByKey("contact.successful-delete", contactName));
        }));
    }

    /**
     * Изменение состояния контакта
     *
     * @param user пользователь
     * @param state состояние, сигнализирующее, что нужно изменить у контакта
     * @param messageKey ключ сообщения успешного выполнения операции
     * @return результат изменения контакта
     */
    private Message processEditContact(User user, State state, String messageKey) {
        StateInfo userStateInfo = userStateService.getUserState(user);
        userStateInfo.setState(state);
        return new Message(messages.getMessageByKey(messageKey));
    }

}