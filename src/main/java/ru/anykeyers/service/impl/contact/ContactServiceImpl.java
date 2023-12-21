package ru.anykeyers.service.impl.contact;

import ru.anykeyers.utils.EnumUtils;
import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.domain.Gender;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.exception.BadArgumentException;
import ru.anykeyers.exception.InvalidUserStateException;
import ru.anykeyers.exception.contact.ContactAlreadyExistsException;
import ru.anykeyers.exception.contact.ContactNotExistsException;
import ru.anykeyers.exception.InvalidNumberFormat;
import ru.anykeyers.processor.state.domain.kinds.contact.EditContactBlockKind;
import ru.anykeyers.processor.state.domain.kinds.contact.EditContactGenderKind;
import ru.anykeyers.processor.state.domain.kinds.sort.SortDirectionKind;
import ru.anykeyers.repository.ContactRepository;
import ru.anykeyers.service.ContactService;
import ru.anykeyers.utils.StringUtils;

import java.util.Optional;
import java.util.Set;

/**
 * Реализация сервиса {@link ContactRepository}
 */
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    private final SearchContactService searchContactService;

    private final FilterContactService filterContactService;

    private final SortContactService sortContactService;

    private final EnumUtils enumUtils;

    private final StringUtils stringUtils;

    public ContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
        searchContactService = new SearchContactService();
        filterContactService = new FilterContactService();
        sortContactService = new SortContactService();
        enumUtils = new EnumUtils();
        stringUtils = new StringUtils();
    }

    @Override
    public Set<Contact> findAll(User user) {
        return contactRepository.findByUsername(user.getUsername());
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
                if (stringUtils.isNotNumber(newValue)) {
                    throw new InvalidNumberFormat();
                }
                contactToEdit.get().setPhoneNumber(newValue);
            }
            case EDIT_CONTACT_AGE -> {
                if (stringUtils.isNotNumber(newValue)) {
                    throw new InvalidNumberFormat();
                }
                int newAge = Integer.parseInt(newValue);
                contactToEdit.get().setAge(newAge);
            }
            case EDIT_CONTACT_GENDER -> {
                Enum<EditContactGenderKind> kind = enumUtils.getEnumKindByField(EditContactGenderKind.values(), newValue);
                if (kind.equals(EditContactGenderKind.MALE)) {
                    contactToEdit.get().setGender(Gender.MAN);
                } else if (kind.equals(EditContactGenderKind.FEMALE)) {
                    contactToEdit.get().setGender(Gender.WOMAN);
                } else {
                    throw new BadArgumentException();
                }
            }
            case EDIT_CONTACT_BLOCK -> {
                Enum<EditContactBlockKind> kind = enumUtils.getEnumKindByField(EditContactBlockKind.values(), newValue);
                if (kind.equals(EditContactBlockKind.BLOCK)) {
                    contactToEdit.get().setBlocked(true);
                } else if (kind.equals(EditContactBlockKind.UNBLOCK)) {
                    contactToEdit.get().setBlocked(false);
                } else {
                    throw new BadArgumentException();
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

    @Override
    public Set<Contact> searchByArgument(User user, StateInfo userStateInfo, String substring) {
        Set<Contact> contacts = contactRepository.findByUsername(user.getUsername());
        return searchContactService.searchContacts(contacts, userStateInfo.getState(), substring);
    }

    @Override
    public Set<Contact> filterByKind(User user, StateInfo userStateInfo, String kind) {
        Set<Contact> contacts = contactRepository.findByUsername(user.getUsername());
        switch (userStateInfo.getState()) {
            case FILTER_AGE_KIND -> {
                return filterContactService.filterByAgeKind(contacts, userStateInfo.getEditInfo(), kind);
            }
            case FILTER_GENDER -> {
                return filterContactService.filterByGender(contacts, kind);
            }
            case FILTER_BLOCK -> {
                return filterContactService.filterByBlock(contacts, kind);
            }
            default -> throw new InvalidUserStateException();
        }
    }

    @Override
    public Set<Contact> sortByKind(User user, StateInfo userStateInfo, Enum<SortDirectionKind> kind) {
        Set<Contact> contacts = contactRepository.findByUsername(user.getUsername());
        return sortContactService.sortContacts(contacts, userStateInfo.getState(), kind);
    }

}
