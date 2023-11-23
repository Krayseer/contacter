package ru.anykeyers.services;

import ru.anykeyers.contexts.Messages;
import ru.anykeyers.dataOperations.kinds.SearchingKind;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.ContactRepository;

public class SearchService {

    private final ContactRepository contactRepository;

    private final Messages messages;

    public SearchService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
        this.messages = new Messages();
    }

    /**
     * Ищет контакт
     * @param user текущий пользователь
     * @param newValue значение для поиска
     * @param field поле для поиска
     * @return найденный контакт
     */
    public String searchContact(User user, String newValue, SearchingKind field) {
        Contact contact = switch (field) {
            case NAME -> contactRepository.findByUsernameAndName(user.getUsername(), newValue);
            case PHONE_NUMBER -> contactRepository.findByUsernameAndPhoneNumber(user.getUsername(), newValue);
        };
        if (contact != null) {
            String contactAge = contact.getAge() < 0
                    ? "Возраст не определен"
                    : Integer.toString(contact.getAge());
            String contactGender = (!contact.getGender().equals("Мужчина") && !contact.getGender().equals("Женщина"))
                    ? "Пол не определен"
                    : contact.getGender();
            String contactNumber = contact.getPhoneNumber().equals("")
                    ? "Номер телефона не определен"
                    : contact.getPhoneNumber();
            String contactForConsole = String.format("Имя: %s, Возраст: %s, Пол: %s, Номер телефона: %s", contact.getName(),
                    contactAge, contactGender, contactNumber);
            return messages.getMessageByKey("contact.searched-contact", contactForConsole);
        } else {
            return messages.getMessageByKey("contact.not-searched");
        }
    }

}
