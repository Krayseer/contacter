package ru.anykeyers.services.impl;

import ru.anykeyers.contexts.Messages;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.Gender;
import ru.anykeyers.domain.User;
import ru.anykeyers.domain.kinds.contact.ContactEditBlockKind;
import ru.anykeyers.domain.kinds.contact.ContactEditGenderKind;
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
        String contactNameToEdit = user.getEditInfo();
        Contact contactToEdit = contactRepository.findByUsernameAndName(user.getUsername(), contactNameToEdit);

        switch (user.getState()) {
            case EDIT_CONTACT_NAME -> contactToEdit.setName(newValue);
            case EDIT_CONTACT_PHONE -> {
                if (isNotNumber(newValue)) {
                    return messages.getMessageByKey("commons.number.invalid-format");
                }
                contactToEdit.setPhoneNumber(newValue);
            }
            case EDIT_CONTACT_AGE -> {
                if (isNotNumber(newValue)) {
                    return messages.getMessageByKey("commons.number.invalid-format");
                }
                int newAge = Integer.parseInt(newValue);
                contactToEdit.setAge(newAge);
            }
            case EDIT_CONTACT_GENDER -> {
                switch (newValue) {
                    case ContactEditGenderKind.MAN -> contactToEdit.setGender(Gender.MAN);
                    case ContactEditGenderKind.WOMAN -> contactToEdit.setGender(Gender.WOMAN);
                    default -> {
                        return messages.getMessageByKey("commons.bad-argument");
                    }
                }
            }
            case EDIT_CONTACT_BLOCK -> {
                switch (newValue) {
                    case ContactEditBlockKind.BLOCK -> contactToEdit.setBlock(true);
                    case ContactEditBlockKind.UNBLOCK -> contactToEdit.setBlock(false);
                    default -> {
                        return messages.getMessageByKey("commons.bad-argument");
                    }
                }
            }
            default -> messages.getMessageByKey("commons.bad-argument");
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

    /**
     * Проверить, что строка не является числом
     * @param str строка, которую нужно проверить
     * @return {@code true}, если не является числом, иначе {@code false}
     */
    private boolean isNotNumber(String str) {
        return !str.matches("\\d+");
    }

}
