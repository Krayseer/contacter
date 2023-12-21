package ru.anykeyers.service.impl.contact;

import ru.anykeyers.common.Utils;
import ru.anykeyers.domain.FileFormat;
import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.domain.Gender;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.exception.BadArgumentException;
import ru.anykeyers.exception.InvalidUserStateException;
import ru.anykeyers.exception.contact.ContactAlreadyExistsException;
import ru.anykeyers.exception.contact.ContactNotExistsException;
import ru.anykeyers.exception.InvalidNumberFormat;
import ru.anykeyers.service.impl.contact.import_export.FileServiceFactory;
import ru.anykeyers.processor.state.domain.kinds.contact.EditContactBlockKind;
import ru.anykeyers.processor.state.domain.kinds.contact.EditContactGenderKind;
import ru.anykeyers.processor.state.domain.kinds.sort.SortDirectionKind;
import ru.anykeyers.repository.ContactRepository;
import ru.anykeyers.service.ContactService;
import ru.anykeyers.service.FileService;

import java.io.File;
import java.util.Collection;
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

    private final FileServiceFactory fileServiceFactory;

    private final Utils utils;


    public ContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
        searchContactService = new SearchContactService();
        filterContactService = new FilterContactService();
        sortContactService = new SortContactService();
        fileServiceFactory = new FileServiceFactory();
        utils = new Utils();
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
                if (utils.isNotNumber(newValue)) {
                    throw new InvalidNumberFormat();
                }
                contactToEdit.get().setPhoneNumber(newValue);
            }
            case EDIT_CONTACT_AGE -> {
                if (utils.isNotNumber(newValue)) {
                    throw new InvalidNumberFormat();
                }
                int newAge = Integer.parseInt(newValue);
                contactToEdit.get().setAge(newAge);
            }
            case EDIT_CONTACT_GENDER -> {
                EditContactGenderKind kind =
                        (EditContactGenderKind) utils.getEnumKindByField(EditContactGenderKind.values(), newValue);
                switch (kind) {
                    case MALE -> contactToEdit.get().setGender(Gender.MAN);
                    case FEMALE -> contactToEdit.get().setGender(Gender.WOMAN);
                    default -> throw new BadArgumentException();
                }
            }
            case EDIT_CONTACT_BLOCK -> {
                EditContactBlockKind kind =
                        (EditContactBlockKind) utils.getEnumKindByField(EditContactBlockKind.values(), newValue);
                switch (kind) {
                    case BLOCK -> contactToEdit.get().setBlocked(true);
                    case UNBLOCK -> contactToEdit.get().setBlocked(false);
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
    public Set<Contact> sortByKind(User user, StateInfo userStateInfo, SortDirectionKind kind) {
        Set<Contact> contacts = contactRepository.findByUsername(user.getUsername());
        return sortContactService.sortContacts(contacts, userStateInfo.getState(), kind);
    }

    @Override
    public void importContacts(User user, File importFile) {
        FileFormat fileFormat = utils.getFileFormat(importFile.getName());
        FileService<Contact> service = fileServiceFactory.getServiceByFormat(fileFormat);
        Collection<Contact> contacts = service.initDataFromFile(importFile);
        contacts.forEach(contact -> {
            contact.setUsername(user.getUsername());
            contactRepository.saveOrUpdate(contact);
        });
    }

    @Override
    public void exportContacts(User user, File exportFile) {
        FileFormat fileFormat = utils.getFileFormat(exportFile.getName());
        FileService<Contact> mapper = fileServiceFactory.getServiceByFormat(fileFormat);
        Set<Contact> contacts = contactRepository.findByUsername(user.getUsername());
        mapper.saveOrUpdateFile(exportFile, contacts);
    }

}
