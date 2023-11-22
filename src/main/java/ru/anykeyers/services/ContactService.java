package ru.anykeyers.services;

import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.ContactRepository;

import java.util.*;


/**
 * Класс, отвечающий за логику при работе с контактами
 */
public class ContactService {

    /**
     * Константа, обозначающая выбор изменения имени контакта
     */
    private final int NEW_NAME = 1;

    /**
     * Константа, обозначающая выбор изменения номера телефона контакта
     */
    private final int NEW_PHONE_NUMBER = 2;

    /**
     * Константа, обозначающая неверный выбор
     */
    private final int WRONG_COMMAND = 3;

    private final ContactRepository contactRepository;

    public ContactService (ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    /**
     * Добавляет контакт в список контактов пользователя
     */
    public void addContact() {
        // TODO: 22.11.2023 Переделать
//        try (Scanner scanner = new Scanner(System.in)) {
//            User user = authenticationService.getCurrentUser();
//            Contact contact = consoleService.readContactFromConsole(scanner);
//            Set<Contact> contacts = contactRepository.findContactsByUsername(user.getUsername());
//            if (!contacts.contains(contact)) {
//                contactRepository.save(user.getUsername(), contact);
//                System.out.println("Пользователь успешно сохранен!");
//            } else {
//                System.out.println("Такой контакт уже существует");
//            }
//        }
    }

    /**
     * Удаляет контакт из списка контактов пользователя
     */
    public void deleteContact() {
        // TODO: 22.11.2023 Переделать
//        try (Scanner scanner = new Scanner(System.in)) {
//            User user = authenticationService.getCurrentUser();
//            String nameContactToDelete = consoleService.deleteContactFromConsole(scanner);
//            Contact contactToDelete = contactRepository.findContactByNameOrNumber(user, nameContactToDelete);
//            if (contactToDelete != null) {
//                contactRepository.removeContact(user, contactToDelete);
//                System.out.println("Контакт удален");
//            } else {
//                System.out.println("Не удалось найти контакт");
//            }
//        }

    }

    /**
     * Изменяет контакт пользователя
     */
    public void editContact() {
        // TODO: 22.11.2023 Переделать
//        try (Scanner scanner = new Scanner(System.in)) {
//            User user = authenticationService.getCurrentUser();
//            String nameContactToEdit = consoleService.editContactFromConsole(scanner);
//            Contact contactToEdit = contactRepository.findContactByNameOrNumber(user, nameContactToEdit);
//            if (contactToEdit == null) {
//                System.out.println("Не удалось найти контакт");
//                return;
//            }
//
//            int choice = consoleService.editInfo(scanner);
//            switch (choice) {
//                case NEW_NAME -> editContactName(scanner, contactToEdit);
//                case NEW_PHONE_NUMBER -> editPhoneNumber(scanner, contactToEdit);
//                case WRONG_COMMAND -> System.out.println("Введена некорректная команда");
//            }
//        }
    }

    /**
     * Изменяет имя контакта
     * @param contactToEdit контакт для изменения
     */
    private void editContactName(Scanner scanner, Contact contactToEdit) {
        // TODO: 22.11.2023 Переделать
//        String[] newContactName = consoleService.editName(scanner).split(" ");
//        if (newContactName.length == 2) {
//            contactToEdit.setFirstname(newContactName[0]);
//            contactToEdit.setLastname(newContactName[1]);
//            System.out.println("Имя контакта изменено");
//        } else {
//            System.out.println("Введено некорректное значение");
//        }
    }


    /**
     * Изменяет номер телефона контакта
     * @param contactToEdit контакт для изменения
     */
    private void editPhoneNumber(Scanner scanner, Contact contactToEdit) {
        // TODO: 22.11.2023 Переделать
//        String newPhoneNumber = consoleService.editPhoneNumber(scanner);
//        contactToEdit.setPhoneNumber(newPhoneNumber);
    }
}
