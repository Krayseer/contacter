package ru.anykeyers.processors.states.impl;

import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.User;
import ru.anykeyers.processors.states.domain.State;
import ru.anykeyers.processors.states.BaseStateProcessor;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.repositories.GroupRepository;
import ru.anykeyers.services.AuthenticationService;
import ru.anykeyers.services.GroupService;

import java.util.List;
import java.util.stream.Collectors;

import static ru.anykeyers.processors.states.domain.State.*;

public class GroupStateProcessor extends BaseStateProcessor {

    private final GroupService groupService;

    public GroupStateProcessor(AuthenticationService authenticationService) {
        super(authenticationService);
        GroupRepository groupRepository = repositoryFactory.createGroupRepository();
        ContactRepository contactRepository = repositoryFactory.createContactRepository();
        groupService = new GroupService(groupRepository, contactRepository);
    }

    @Override
    public void registerStatesHandlers() {
        stateHandlers.put(GET_GROUP_CONTACTS, (user, message) -> {
            return groupService.getGroupContacts(user.getUsername(), message).stream()
                    .map(Contact::getInfo)
                    .collect(Collectors.joining("\n"));
        });
        stateHandlers.put(ADD_GROUP, (user, message) -> {
            String result = groupService.addGroup(user, message);
            user.clearState();
            authenticationService.saveOrUpdateUser(user);
            return result;
        });
        stateHandlers.put(EDIT_GROUP, (user, message) -> {
            user.setGroupNameToEdit(message);
            user.setState(EDIT_GROUP_FIELD);
            authenticationService.saveOrUpdateUser(user);
            return messages.getMessageByKey("group.state.edit.todo");
        });
        stateHandlers.put(EDIT_GROUP_FIELD, (user, message) -> {
            return switch (message.toLowerCase()) {
                case GroupKind.NAME -> processFieldEdit(user, EDIT_GROUP_NAME, "group.state.edit.name");
                case GroupKind.ADD_CONTACT -> processFieldEdit(user, EDIT_GROUP_ADD_CONTACT, "group.state.edit.contact.add");
                case GroupKind.DELETE_CONTACT -> processFieldEdit(user, EDIT_GROUP_DELETE_CONTACT, "group.state.edit.contact.delete");
                default -> messages.getMessageByKey("contact.state.edit.bad-argument");
            };
        });
        List<State> editContactFieldsStates = List.of(EDIT_GROUP_NAME, EDIT_GROUP_ADD_CONTACT, EDIT_GROUP_DELETE_CONTACT);
        editContactFieldsStates.forEach(state -> stateHandlers.put(state, (user, message) -> {
            String result = groupService.editGroup(user, message);
            user.clearState();
            authenticationService.saveOrUpdateUser(user);
            return result;
        }));
        stateHandlers.put(DELETE_GROUP, (user, message) -> {
            String result = groupService.deleteGroup(user, message);
            if (result.equals(messages.getMessageByKey("group.successful-delete", message))) {
                user.clearState();
                authenticationService.saveOrUpdateUser(user);
            }
            return result;
        });
    }

    private String processFieldEdit(User user, State state, String messageKey) {
        user.setState(state);
        authenticationService.saveOrUpdateUser(user);
        return messages.getMessageByKey(messageKey);
    }

    /**
     * Класс, содержащий информацию о действиях, которые можно сделать с группой
     */
    private static final class GroupKind {

        /**
         * Изменение названия группы
         */
        public static final String NAME = "1";

        /**
         * Добавление контакта в группу
         */
        public static final String ADD_CONTACT = "2";

        /**
         * Удаление контакта из группы
         */
        public static final String DELETE_CONTACT = "3";

    }


}
