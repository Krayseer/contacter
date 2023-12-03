package ru.anykeyers.services.impl;

import ru.anykeyers.contexts.Messages;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.services.ContactService;


/**
 * Реализация сервиса для работы с контактами
 */
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    private final Messages messages;

    public ContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
        messages = new Messages();
    }

    @Override
    public boolean existsContact(User user, String contactName) {
        return contactRepository.existsByUsernameAndName(user.getUsername(), contactName);
    }

    @Override
    public String addContact(User user, String contactName) {
        boolean isContactExists = contactRepository.existsByUsernameAndName(user.getUsername(), contactName);
        if (isContactExists) {
            return messages.getMessageByKey("contact.already-exists", contactName);
        }
        Contact contact = new Contact(user.getUsername(), contactName);
        contactRepository.saveOrUpdate(contact);
        return messages.getMessageByKey("contact.successful-save", contactName);
    }

    @Override
    public String editContact(User user, String newValue) {
        String contactNameToEdit = user.getContactNameToEdit();
        Contact contactToEdit = contactRepository.findByUsernameAndName(user.getUsername(), contactNameToEdit);

        switch (user.getState()) {
            case EDIT_CONTACT_NAME -> contactToEdit.setName(newValue);
            case EDIT_CONTACT_PHONE -> contactToEdit.setPhoneNumber(newValue);
            default -> messages.getMessageByKey("argument.bad");
        }

        contactRepository.saveOrUpdate(contactToEdit);
        return messages.getMessageByKey("contact.successful-edit-name", contactNameToEdit);
    }

    @Override
    public String deleteContact(User user, String contactName) {
        Contact contactToDelete = contactRepository.findByUsernameAndName(user.getUsername(), contactName);
        if (contactToDelete == null) {
            return messages.getMessageByKey("contact.not-exists", contactName);
        }
        contactRepository.delete(contactToDelete);
        return messages.getMessageByKey("contact.successful-delete", contactToDelete.getName());
    }

}
