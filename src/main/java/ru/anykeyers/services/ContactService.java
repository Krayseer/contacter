package ru.anykeyers.services;

import ru.anykeyers.contexts.Messages;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.ContactRepository;

import java.util.Set;


/**
 * Сервис для работы с контактами
 */
public class ContactService {

    private final Messages messages;

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        messages = new Messages();
        this.contactRepository = contactRepository;
    }

    /**
     * Существует ли контакт у пользователя
     * @param user пользователь
     * @param contactName имя контакта
     * @return {@code true}, если существует, иначе {@code false}
     */
    public boolean existsContact(User user, String contactName) {
        return contactRepository.existsByUsernameAndName(user.getUsername(), contactName);
    }

    /**
     * Получение всех контактов пользователя
     * @param user пользователь
     * @return список контактов
     */
    public Set<Contact> getAllContacts(User user) {
        return contactRepository.findByUsername(user.getUsername());
    }

    /**
     * Добавляет контакт в список контактов пользователя
     * @return результат добавления контакта
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
     * Изменить состояние контакта
     * <ol>
     *     <li>Изменить имя</li>
     *     <li>Изменить телефонный номер</li>
     * </ol>
     * @param user пользователь, которому нужно изменить контакт
     * @return результат изменения состояния контакта
     */
    public String editContact(User user, String newValue) {
        String contactNameToEdit = user.getContactNameToEdit();
        Contact contactToEdit = contactRepository.findByUsernameAndName(user.getUsername(), contactNameToEdit);

        switch (user.getState()) {
            case EDIT_CONTACT_NAME -> contactToEdit.setName(newValue);
            case EDIT_CONTACT_PHONE -> contactToEdit.setPhoneNumber(newValue);
            default -> messages.getMessageByKey("field.invalid");
        }

        contactRepository.saveOrUpdate(contactToEdit);
        return messages.getMessageByKey("contact.successful-edit-name", contactNameToEdit);
    }


    /**
     * Удаляет контакт из списка контактов пользователя
     * @return результат удаления контакта
     */
    public String deleteContact(User user, String value) {
        String username = user.getUsername();
        Contact contactToDelete = contactRepository.findByUsernameAndName(username, value);

        if (contactToDelete == null) {
            return messages.getMessageByKey("contact.not-exists", value);
        }

        contactRepository.delete(contactToDelete);
        return messages.getMessageByKey("contact.successful-delete", contactToDelete.getName());
    }

}
