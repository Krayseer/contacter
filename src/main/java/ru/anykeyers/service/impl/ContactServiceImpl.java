package ru.anykeyers.service.impl;

import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.domain.Gender;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.exception.BadArgumentException;
import ru.anykeyers.exception.contact.ContactAlreadyExistsException;
import ru.anykeyers.exception.contact.ContactNotExistsException;
import ru.anykeyers.exception.InvalidNumberFormat;
import ru.anykeyers.processor.state.domain.kinds.contact.EditContactBlockKind;
import ru.anykeyers.processor.state.domain.kinds.contact.EditContactGenderKind;
import ru.anykeyers.repository.ContactRepository;
import ru.anykeyers.service.ContactService;

import java.util.Optional;

/**
 * Реализация сервиса {@link ContactRepository}
 */
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    public ContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    public boolean existsContact(User user, String contactName) {
        return contactRepository.existsByUsernameAndName(user.getUsername(), contactName);
    }

    @Override
    public void addContact(User user, String contactName) {
        boolean isContactExists = contactRepository.existsByUsernameAndName(user.getUsername(), contactName);
        if (isContactExists) {
            throw new ContactAlreadyExistsException(contactName);
        }
        Contact contact = new Contact(user.getUsername(), contactName);
        contactRepository.saveOrUpdate(contact);
    }

    @Override
    public void editContact(User user, StateInfo userStateInfo, String newValue) {
        String contactNameToEdit = userStateInfo.getEditInfo();
        Optional<Contact> contactToEdit = contactRepository.getByUsernameAndName(user.getUsername(), contactNameToEdit);
        if (contactToEdit.isEmpty()) {
            throw new ContactNotExistsException(contactNameToEdit);
        }
        switch (userStateInfo.getState()) {
            case EDIT_CONTACT_NAME -> contactToEdit.get().setName(newValue);
            case EDIT_CONTACT_PHONE -> {
                if (isNotNumber(newValue)) {
                    throw new InvalidNumberFormat();
                }
                contactToEdit.get().setPhoneNumber(newValue);
            }
            case EDIT_CONTACT_AGE -> {
                if (isNotNumber(newValue)) {
                    throw new InvalidNumberFormat();
                }
                int newAge = Integer.parseInt(newValue);
                contactToEdit.get().setAge(newAge);
            }
            case EDIT_CONTACT_GENDER -> {
                switch (newValue) {
                    case EditContactGenderKind.MALE -> contactToEdit.get().setGender(Gender.MAN);
                    case EditContactGenderKind.FEMALE -> contactToEdit.get().setGender(Gender.WOMAN);
                    default -> throw new BadArgumentException();
                }
            }
            case EDIT_CONTACT_BLOCK -> {
                switch (newValue) {
                    case EditContactBlockKind.BLOCK -> contactToEdit.get().setBlocked(true);
                    case EditContactBlockKind.UNBLOCK -> contactToEdit.get().setBlocked(false);
                    default -> throw new BadArgumentException();
                }
            }
            default -> throw new BadArgumentException();
        }
        contactRepository.saveOrUpdate(contactToEdit.get());
    }

    @Override
    public void deleteContact(User user, String contactName) {
        Optional<Contact> contactToDelete = contactRepository.getByUsernameAndName(user.getUsername(), contactName);
        if (contactToDelete.isEmpty()) {
            throw new ContactNotExistsException(contactName);
        }
        contactRepository.delete(contactToDelete.get());
    }

    /**
     * Проверить, что строка не является числом
     *
     * @param str строка, которую нужно проверить
     * @return {@code true}, если не является числом, иначе {@code false}
     */
    private boolean isNotNumber(String str) {
        return !str.matches("\\d+");
    }

}
