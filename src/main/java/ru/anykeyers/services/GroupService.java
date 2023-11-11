package ru.anykeyers.services;

import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.Group;
import ru.anykeyers.domain.User;
import ru.anykeyers.repositories.GroupRepository;

import java.util.*;

/**
 * Класс, отвечающий за логику при работе с группами
 */
public class GroupService {

    private final int NEW_GROUP_NAME = 1;

    private final int ADD_CONTACT = 2;

    private final int DELETE_CONTACT = 3;

    private final int WRONG_COMMAND = 4;

    private final AuthenticationService authenticationService;

    private final ConsoleService consoleService;

    private final GroupRepository groupRepository;

    private final ContactService contactService;

    public GroupService (AuthenticationService authenticationService,
                         ContactService contactService,
                         GroupRepository groupRepository) {
        this.authenticationService = authenticationService;
        this.groupRepository = groupRepository;
        this.contactService = contactService;

        consoleService = new ConsoleService();
    }

    /**
     * Добавляет группу в список групп пользователя
     */
    public void addGroup() {
        try (Scanner scanner = new Scanner(System.in)) {
            User user = authenticationService.getCurrentUser();
            Group group = consoleService.readGroupFromConsole(scanner);
            Set<Group> userGroups = groupRepository.findGroupsByUsername(user.getUsername());
            if (!userGroups.contains(group)) {
                groupRepository.save(user.getUsername(), group);
                System.out.println("Группа сохранена");
            } else {
                System.out.println("Такая группа уже существует");
            }
        }
    }

    /**
     * Удаляет группу из списка групп пользователя
     */
    public void deleteGroup() {
        try (Scanner scanner = new Scanner(System.in)) {
            User user = authenticationService.getCurrentUser();
            String nameGroupToDelete = consoleService.deleteGroupFromConsole(scanner);
            Group groupToDelete = groupRepository.findGroupByName(user, nameGroupToDelete);
            if (groupToDelete != null) {
                groupRepository.removeGroup(user, groupToDelete);
                System.out.println("Группа удалена");
            } else {
                System.out.println("Не удалось найти группу");
            }
        }
    }

    /**
     * Изменяет группу в списке групп пользователя
     */
    public void editGroup() {
        try (Scanner scanner = new Scanner(System.in)){
            User user = authenticationService.getCurrentUser();
            String nameGroupToEdit = consoleService.editGroupFromConsole(scanner);
            Group groupToEdit = groupRepository.findGroupByName(user, nameGroupToEdit);
            if (groupToEdit == null) {
                System.out.println("Не удалось найти группу");
                return;
            }

            int choice = consoleService.editGroupInfo(scanner);
            switch (choice) {
                case NEW_GROUP_NAME -> editGroupName(scanner, groupToEdit);
                case ADD_CONTACT -> addContactInGroup(scanner, user, groupToEdit);
                case DELETE_CONTACT -> deleteContactFromGroup(scanner, groupToEdit);
                case WRONG_COMMAND -> System.out.println("Введена неверная команда");
            }
        }
    }

    /**
     * Изменяет имя группы
     * @param groupToEdit группа для изменения
     */
    private void editGroupName(Scanner scanner, Group groupToEdit) {
        String newGroupName = consoleService.editGroupName(scanner);
        groupToEdit.setName(newGroupName);
        System.out.println("Имя группы изменено");
    }

    /**
     * Удаляет контакт из группы
     * @param groupToEdit группа для изменения
     */
    private void deleteContactFromGroup(Scanner scanner, Group groupToEdit) {
        String contactName = consoleService.deleteContactFromGroup(scanner);
        Contact contactToDeleteFromGroup = groupRepository.findContactInGroup(groupToEdit, contactName);
        if (contactToDeleteFromGroup != null) {
            groupToEdit.getMembers().remove(contactToDeleteFromGroup);
            System.out.println("Контакт удален из группы");
        } else {
            System.out.println("Такой контакт не найден");
        }
    }

    /**
     * Добавляет контакт в группу
     * @param user текущий пользователь
     * @param groupToEdit группа для изменения
     */
    private void addContactInGroup(Scanner scanner, User user, Group groupToEdit) {
        String contactName = consoleService.addContactInGroup(scanner);
        Contact contactToAddInGroup = contactService.findContact(user, contactName);
        if (contactToAddInGroup != null) {
            groupToEdit.getMembers().add(contactToAddInGroup);
            System.out.println("Контакт добавлен в группу");
        } else {
            System.out.println("Нет такого контакта в вашем списке контактов");
        }
    }

}
