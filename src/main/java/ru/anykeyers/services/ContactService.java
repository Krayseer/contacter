package ru.anykeyers.services;

import ru.anykeyers.contexts.Messages;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.ContactRepository;

import java.util.UUID;


/**
 * Класс, отвечающий за логику при работе с контактами
 */
public class ContactService {

    private final Messages messages;

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        messages = new Messages();
        this.contactRepository = contactRepository;
    }

    /**
     * Добавляет контакт в список контактов пользователя
     */
    public String addContact(User user, String contactName) {
        boolean isContactExists = contactRepository.existsByUsernameAndName(user.getUsername(), contactName);
        if (isContactExists) {
            return messages.getMessageByKey("contact.already-exists", contactName);
        }
        Contact contact = new Contact(user.getUsername(), contactName);
        contactRepository.saveOrUpdate(contact);
        return messages.getMessageByKey("contact.successful-save", contactName);
    }

    /**
     * Изменить контакт
     * @param user пользователь, которому нужно изменить контакт
     * @param contactName имя контакта и обновленное значение, которое нужно применить одному из его полей
     * @param fieldToEdit поле для изменения
     * @return сообщение об успешном или неуспешном выполнении
     */
    public String editContact(User user, String contactName, Object newValue, Contact.Field fieldToEdit) {
        Contact contactToEdit = contactRepository.findByUsernameAndName(user.getUsername(), contactName);
        if (contactToEdit == null) {
            return messages.getMessageByKey("contact.not-exists", contactName);
        }

        switch (fieldToEdit) {
            case CONTACT_NAME -> {
                String newName = (String) newValue;
                contactToEdit.setName(newName);
            }
            case CONTACT_PHONE_NUMBER -> {
                String newPhoneNumber = (String) newValue;
                contactToEdit.setPhoneNumber(newPhoneNumber);
            }
            default -> messages.getMessageByKey("field.invalid");
        }

        contactRepository.saveOrUpdate(contactToEdit);
        return messages.getMessageByKey("contact.successful-edit-name", contactName);
    }


    /**
     * Удаляет контакт из списка контактов пользователя
     */
    public String deleteContact(User user, String value, Contact.Field field) {
        String username = user.getUsername();
        Contact contactToDelete = null;

        switch (field) {
            case CONTACT_NAME -> contactToDelete = contactRepository.findByUsernameAndName(username, value);
            case CONTACT_PHONE_NUMBER -> contactToDelete = contactRepository.findByUsernameAndPhoneNumber(username, value);
            default -> messages.getMessageByKey("field.invalid");
        }

        if (contactToDelete == null) {
            return messages.getMessageByKey("contact.not-exists", value);
        }

        contactRepository.delete(contactToDelete);
        return messages.getMessageByKey("contact.successful-delete", contactToDelete.getName());
    }

}
